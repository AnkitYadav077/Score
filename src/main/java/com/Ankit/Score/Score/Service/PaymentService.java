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
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepo paymentRepository;
    @Autowired
    private SportSlotRepo slotRepo;

    @Autowired
    public PaymentService(RazorpayClient razorpayClient, PaymentRepo paymentRepository) {
        this.razorpayClient = razorpayClient;
        this.paymentRepository = paymentRepository;
    }

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
                .amount(request.getAmount())
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

        int hourRate = (slot.getStartTime().getHour() < 19) ? slot.getCategory().getBasePrice() : slot.getCategory().getEveningPrice();

        // Calculate duration in hours (e.g., 9:00 - 12:00 = 3 hours)
        long durationHours = java.time.Duration.between(slot.getStartTime(), slot.getEndTime()).toHours();
        if (durationHours <= 0) durationHours = 1; // Fallback in case of invalid data

        int totalAmount = hourRate * (int) durationHours;
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
        response.put("totalPrice", totalAmount); // Optional: Send total price in INR too

        return response;
    }
    public Payment verifyAndSavePaymentForSlot(Long userId, Long slotId, String orderId, String paymentId, String signature) throws Exception {
        // Signature verification
        if (!verifySignature(orderId, paymentId, signature)) {
            throw new IllegalStateException("Payment signature verification failed");
        }

        // Payment captured status
        if (!verifyPayment(paymentId)) {
            throw new IllegalStateException("Payment not captured yet");
        }

        // Fetch slot and calculate price
        SportSlot slot = slotRepo.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", "id", slotId));
        int hour = slot.getStartTime().getHour();
        int price = (hour < 19) ? slot.getCategory().getBasePrice() : slot.getCategory().getEveningPrice();

        // Save payment
        Payment payment = Payment.builder()
                .userId(userId)
                .slotId(slotId)
                .amount((double) price)
                .paymentMethod("RAZORPAY")
                .paymentStatus("SUCCESS")
                .transactionId(paymentId)
                .paymentDateTime(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }
}


