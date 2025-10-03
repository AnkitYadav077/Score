package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Security.CustomOAuth2User;
import com.Ankit.Score.Score.Security.JwtHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final JwtHelper jwtHelper;

    @GetMapping("/user")
    public Map<String, Object> getUserInfo(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest request) {

        Map<String, Object> userInfo = new HashMap<>();
        System.out.println("=== OAuth2 User API Called ===");
        System.out.println("Principal: " + customOAuth2User);
        System.out.println("Auth Header: " + authHeader);

        // Method 1: Check JWT Token first
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("Token received: " + token);

            try {
                if (jwtHelper.validateToken(token)) {
                    String email = jwtHelper.getUsernameFromToken(token);
                    System.out.println("Email from token: " + email);

                    userInfo.put("name", jwtHelper.getClaimFromToken(token, claims -> claims.get("name", String.class)));
                    userInfo.put("email", email);
                    userInfo.put("roles", jwtHelper.getRolesFromToken(token));
                    userInfo.put("userId", jwtHelper.getUserIdFromToken(token));
                    userInfo.put("provider", jwtHelper.getClaimFromToken(token, claims -> claims.get("provider", String.class)));
                    userInfo.put("userType", jwtHelper.getUserTypeFromToken(token));
                    userInfo.put("source", "jwt_token");
                    userInfo.put("message", "User authenticated via JWT token");

                    System.out.println("User info from token: " + userInfo);
                    return userInfo;
                }
            } catch (Exception e) {
                System.out.println("Token validation failed: " + e.getMessage());
                // Continue to check other authentication methods
            }
        }

        // Method 2: Check OAuth2 Principal
        if (customOAuth2User != null) {
            System.out.println("OAuth2 User found: " + customOAuth2User.getUser().getEmail());

            userInfo.put("name", customOAuth2User.getUser().getName());
            userInfo.put("email", customOAuth2User.getUser().getEmail());
            userInfo.put("roles", customOAuth2User.getUser().getRoles());
            userInfo.put("userId", customOAuth2User.getUser().getUserId());
            userInfo.put("provider", customOAuth2User.getUser().getProvider());
            userInfo.put("source", "oauth2_principal");
            userInfo.put("message", "User authenticated via OAuth2 session");

            System.out.println("User info from OAuth2: " + userInfo);
        } else {
            userInfo.put("error", "User not authenticated");
            userInfo.put("message", "Please login via OAuth2 first or provide valid JWT token");
            userInfo.put("loginUrl", "http://localhost:8080/oauth2/authorization/google");
            System.out.println("No authentication found");
        }

        return userInfo;
    }
}