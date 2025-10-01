package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Payment;
import com.Ankit.Score.Score.Payloads.PaymentVerificationRequest;

import java.util.Map;

public interface PaymentService {

    boolean verifySignature(String orderId, String paymentId, String signature) throws Exception;

    boolean verifyPayment(String paymentId) throws Exception;

    Payment verifyAndSavePayment(PaymentVerificationRequest request) throws Exception;

    Map<String, Object> createPaymentOrder(int amount, String currency, String receipt) throws Exception;

    Map<String, Object> createOrderForSlot(Long slotId, String currency) throws Exception;

    Payment verifyAndSavePaymentForSlot(Long userId, Long slotId, String orderId,
                                        String paymentId, String signature) throws Exception;
}
