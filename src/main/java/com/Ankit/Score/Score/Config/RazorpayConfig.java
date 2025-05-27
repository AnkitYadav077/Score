package com.Ankit.Score.Score.Config;

import com.razorpay.RazorpayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    private static final String KEY_ID = "rzp_test_7SEhinYJRzQIpS";
    private static final String KEY_SECRET = "nY8tqv7s7OiZ1KmoxToHwN7D";

    @Bean
    public RazorpayClient razorpayClient() throws Exception {
        return new RazorpayClient(KEY_ID, KEY_SECRET);
    }
}
