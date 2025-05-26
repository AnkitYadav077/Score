package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.PaymentVerificationRequest;
import com.Ankit.Score.Score.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public String createOrder(@RequestParam int amount,
                              @RequestParam String currency,
                              @RequestParam String receipt) throws Exception {
        return paymentService.createPaymentOrder(amount, currency, receipt);
    }

    @PostMapping("/verify")
    public String verifyPayment(@RequestBody PaymentVerificationRequest request) throws Exception {
        boolean isValid = paymentService.verifySignature(
                request.getOrderId(),
                request.getPaymentId(),
                request.getSignature()
        );

        if (isValid) {
            String result = paymentService.updatePaymentStatus(request);
            return "Payment verified and status updated: " + result;
        } else {
            return "Payment verification failed ❌";
        }
    }

}
