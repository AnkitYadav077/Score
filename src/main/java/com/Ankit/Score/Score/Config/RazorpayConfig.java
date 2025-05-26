package com.Ankit.Score.Score.Config;

import com.razorpay.RazorpayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Bean
    public RazorpayClient razorpayClient() throws Exception {
        return new RazorpayClient("rzp_test_7SEhinYJRzQIpS", "nY8tqv7s7OiZ1KmoxToHwN7D");
    }
}
