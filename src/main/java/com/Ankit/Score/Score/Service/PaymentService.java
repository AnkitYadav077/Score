package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Config.Utils;
import com.Ankit.Score.Score.Entity.Payment;
import com.Ankit.Score.Score.Payloads.PaymentVerificationRequest;
import com.Ankit.Score.Score.Repo.PaymentRepo;
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
}
