package com.Ankit.Score.Score.Security;

import com.Ankit.Score.Score.Entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtHelper jwtHelper;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        User user = oauthUser.getUser();

        // Generate JWT token using the NEW method (without affecting existing logic)
        String token = jwtHelper.generateTokenFromOAuthUser(oauthUser);

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Create response data
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("message", "Login successful");
        responseData.put("token", token);
        responseData.put("user", createUserResponse(user));

        // Write JSON response
        objectMapper.writeValue(response.getWriter(), responseData);

        // Don't redirect, send JSON response instead
        clearAuthenticationAttributes(request);
    }

    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("userId", user.getUserId());
        userResponse.put("name", user.getName());
        userResponse.put("email", user.getEmail());
        userResponse.put("mobileNo", user.getMobileNo());
        userResponse.put("provider", user.getProvider());
        userResponse.put("roles", user.getRoles());
        return userResponse;
    }
}
