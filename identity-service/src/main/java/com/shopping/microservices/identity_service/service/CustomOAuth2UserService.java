package com.shopping.microservices.identity_service.service;

import com.shopping.microservices.identity_service.entity.CustomOAuth2User;
import com.shopping.microservices.identity_service.entity.RoleEntity;
import com.shopping.microservices.identity_service.enumeration.UserDefaultType;
import com.shopping.microservices.identity_service.repository.RoleRepository;
import com.shopping.microservices.identity_service.entity.UserEntity;
import com.shopping.microservices.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        String email = oauth2User.getAttribute("email");
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2 login request from provider: {} with email: {}", registrationId, email);

        UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
            log.info("No existing user found with email: {}. Creating new user...", email);

            RoleEntity role = roleService.getRoleByName(UserDefaultType.USER.name());
            return userRepository.save(UserEntity.builder()
                    .email(email)
                    .username(email)
                    .name(oauth2User.getAttribute("name"))
                    .roles(Set.of(role))
                    .oauthId(registrationId)
                    .build());
        });

        return new CustomOAuth2User(user, oauth2User.getAuthorities());
    }
}
