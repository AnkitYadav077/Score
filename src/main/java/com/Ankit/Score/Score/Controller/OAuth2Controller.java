package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Security.CustomOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @GetMapping("/user")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Map<String, Object> userInfo = new HashMap<>();
        if (customOAuth2User != null) {
            userInfo.put("name", customOAuth2User.getUser().getName());
            userInfo.put("email", customOAuth2User.getUser().getEmail());
            userInfo.put("roles", customOAuth2User.getUser().getRoles());
        }
        return userInfo;
    }
}