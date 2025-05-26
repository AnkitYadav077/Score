package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Config.Utils;
import com.Ankit.Score.Score.Entity.Booking;
import com.Ankit.Score.Score.Entity.FoodOrder;
import com.Ankit.Score.Score.Payloads.PaymentVerificationRequest;
import com.Ankit.Score.Score.Repo.BookingRepo;
import com.Ankit.Score.Score.Repo.FoodOrderRepo;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private FoodOrderRepo foodOrderRepository;

    @Autowired
    private BookingRepo bookingRepository;

    private RazorpayClient razorpayClient;

    private static final String RAZORPAY_KEY_ID = "rzp_test_7SEhinYJRzQIpS"; // Your Razorpay Key ID
    private static final String RAZORPAY_KEY_SECRET = "nY8tqv7s7OiZ1KmoxToHwN7D"; // Your Razorpay Secret

    public PaymentService() throws Exception {
        this.razorpayClient = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET);
    }

    // Create Razorpay Order
    public String createPaymentOrder(int amount, String currency, String receipt) throws Exception {
        JSONObject options = new JSONObject();
        options.put("amount", amount * 100); // Razorpay expects amount in paise
        options.put("currency", currency);
        options.put("receipt", receipt);

        Order order = razorpayClient.orders.create(options);
        return order.toString();
    }

    // Verify Razorpay Signature
    public boolean verifySignature(String orderId, String paymentId, String signature) throws Exception {
        String payload = orderId + "|" + paymentId;
        return Utils.verifySignature(payload, signature, RAZORPAY_KEY_SECRET);
    }

    // Capture and verify payment (optional)
    public boolean verifyPayment(String paymentId) throws Exception {
        Payment payment = razorpayClient.payments.fetch(paymentId);
        return "captured".equals(payment.get("status"));
    }

    public String updatePaymentStatus(PaymentVerificationRequest request) {
        if (request.getFoodOrderId() != null) {
            FoodOrder foodOrder = foodOrderRepository.findById(request.getFoodOrderId())
                    .orElseThrow(() -> new RuntimeException("FoodOrder not found!"));
            foodOrder.setPaymentStatus("PAID");
            foodOrder.setStatus("CONFIRMED");
            foodOrderRepository.save(foodOrder);
            return "FoodOrder updated ✅";
        } else if (request.getBookingId() != null) {
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Booking not found!"));
            booking.setPaymentStatus("PAID");
            booking.setStatus("CONFIRMED");
            bookingRepository.save(booking);
            return "Booking updated ✅";
        } else {
            return "No entity found to update ❓";
        }
    }
}
