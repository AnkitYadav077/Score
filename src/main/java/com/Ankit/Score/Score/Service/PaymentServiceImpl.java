package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Config.Utils;
import com.Ankit.Score.Score.Entity.Payment;
import com.Ankit.Score.Score.Entity.SportSlot;
import com.Ankit.Score.Score.Exceptions.ResourceNotFoundException;
import com.Ankit.Score.Score.Payloads.PaymentVerificationRequest;
import com.Ankit.Score.Score.Repo.PaymentRepo;
import com.Ankit.Score.Score.Repo.SportSlotRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepo paymentRepository;
    private final SportSlotRepo slotRepo;
    private final SportSlotServiceImpl sportSlotService; // Add this dependency

    public boolean verifySignature(String orderId, String paymentId, String signature) throws Exception {
        String payload = orderId + "|" + paymentId;
        String razorpaySecret = "nY8tqv7s7OiZ1KmoxToHwN7D";
        return Utils.verifySignature(payload, signature, razorpaySecret);
    }

    public boolean verifyPayment(String paymentId) throws Exception {
        com.razorpay.Payment payment = razorpayClient.payments.fetch(paymentId);
        return "captured".equalsIgnoreCase(payment.get("status"));
    }

    public Payment verifyAndSavePayment(PaymentVerificationRequest request) throws Exception {
        if (!verifySignature(request.getRazorpayOrderId(), request.getRazorpayPaymentId(), request.getRazorpaySignature())) {
            throw new IllegalStateException("Payment signature verification failed");
        }

        if (!verifyPayment(request.getRazorpayPaymentId())) {
            throw new IllegalStateException("Payment not captured yet");
        }

        Payment payment = Payment.builder()
                .userId(request.getUserId())
                .amount(request.getAmount() / 100.0)
                .paymentMethod("RAZORPAY")
                .paymentStatus("SUCCESS")
                .transactionId(request.getRazorpayPaymentId())
                .paymentDateTime(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    public Map<String, Object> createPaymentOrder(int amount, String currency, String receipt) throws Exception {
        JSONObject options = new JSONObject();
        options.put("amount", amount * 100); // Razorpay expects amount in paise
        options.put("currency", currency);
        options.put("receipt", receipt);

        Order order = razorpayClient.orders.create(options);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", order.get("amount"));
        response.put("currency", order.get("currency"));
        response.put("status", order.get("status"));
        return response;
    }

    public Map<String, Object> createOrderForSlot(Long slotId, String currency) throws Exception {
        SportSlot slot = slotRepo.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", "id", slotId));

        // Use the same calculation as SportSlotServiceImpl
        Integer totalAmount = sportSlotService.calculateTotalPrice(slot);
        if (totalAmount == null || totalAmount <= 0) {
            throw new IllegalStateException("Invalid slot price calculation");
        }

        int amountInPaise = totalAmount * 100;

        // Create Razorpay order
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", currency);
        orderRequest.put("payment_capture", 1);

        Order order = razorpayClient.orders.create(orderRequest);

        // Response Map
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", amountInPaise);
        response.put("currency", currency);
        response.put("totalPrice", totalAmount); // Send total price in INR

        return response;
    }

    public Payment verifyAndSavePaymentForSlot(Long userId, Long slotId, String orderId,
                                               String paymentId, String signature) throws Exception {
        // Signature verification
        if (!verifySignature(orderId, paymentId, signature)) {
            throw new IllegalStateException("Payment signature verification failed");
        }

        // Fetch slot and calculate total price using consistent method
        SportSlot slot = slotRepo.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", "id", slotId));

        Integer totalAmount = sportSlotService.calculateTotalPrice(slot);
        if (totalAmount == null || totalAmount <= 0) {
            throw new IllegalStateException("Invalid slot price calculation");
        }

        // Amount verification - Check if paid amount matches expected amount
        com.razorpay.Payment razorpayPayment = razorpayClient.payments.fetch(paymentId);
        int paidAmount = Integer.parseInt(razorpayPayment.get("amount").toString());
        int expectedAmountInPaise = totalAmount * 100;

        if (paidAmount != expectedAmountInPaise) {
            throw new IllegalStateException("Paid amount doesn't match expected amount. Expected: " + expectedAmountInPaise + ", Paid: " + paidAmount);
        }

        // Payment captured status
        if (!verifyPayment(paymentId)) {
            throw new IllegalStateException("Payment not captured yet");
        }

        // Save payment with correct total amount
        Payment payment = Payment.builder()
                .userId(userId)
                .slotId(slotId)
                .amount((double) totalAmount)
                .paymentMethod("RAZORPAY")
                .paymentStatus("SUCCESS")
                .transactionId(paymentId)
                .paymentDateTime(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }
}