package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.User;
import com.Ankit.Score.Score.Repo.UserRepo;
import com.Ankit.Score.Score.Security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {


    private final UserRepo userRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email;
        String name;
        String providerUserId;

        if ("google".equals(provider)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            providerUserId = (String) attributes.get("sub");
        } else if ("github".equals(provider)) {
            providerUserId = String.valueOf(attributes.get("id"));
            email = (String) attributes.get("email");
            if (email == null) {
                email = providerUserId + "@github.com";
            }
            name = (String) attributes.get("name");
            if (name == null) {
                name = (String) attributes.get("login");
            }
        } else if ("facebook".equals(provider)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            providerUserId = String.valueOf(attributes.get("id"));
        } else {
            throw new OAuth2AuthenticationException("Provider not supported");
        }

        Optional<User> userOptional = userRepo.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!provider.equals(user.getProvider())) {
                user.setProvider(provider);
                user.setProviderUserId(providerUserId);
                userRepo.save(user);
            }
        } else {
            // Create new user
            user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setProvider(provider);
            user.setProviderUserId(providerUserId);
            user.setRoles(new HashSet<>());
            user.getRoles().add("ROLE_USER");

            // Set mobileNo to null instead of empty string
            user.setMobileNo(null);

            userRepo.save(user);
        }

        return new CustomOAuth2User(user, attributes);
    }
}