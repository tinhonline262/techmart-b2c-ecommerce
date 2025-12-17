package com.shopping.microservices.identity_service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.microservices.identity_service.exception.LoginNotValidException;
import com.shopping.microservices.identity_service.dto.LoginResponseDTO;
import com.shopping.microservices.identity_service.service.AuthService;
import com.shopping.microservices.identity_service.entity.UserEntity;
import com.shopping.microservices.identity_service.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    private final AuthService authService;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, ObjectMapper objectMapper,
                                     @Lazy AuthService authService) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String username = authentication.getName();
        UserEntity user = userRepository.findByUsername(username).orElseThrow(
                () -> new LoginNotValidException("User with username [%s] not found".formatted(username)));

        // Sample code to response json directly, it should add cookie and redirect to frontend url
        LoginResponseDTO loginResponse = authService.getLoginResponseWithAssignedTokens(user);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), loginResponse);
    }
}
