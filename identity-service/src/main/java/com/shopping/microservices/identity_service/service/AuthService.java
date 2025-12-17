package com.shopping.microservices.identity_service.service;

import com.shopping.microservices.identity_service.dto.LoginRequestDTO;
import com.shopping.microservices.identity_service.dto.LoginResponseDTO;
import com.shopping.microservices.identity_service.dto.RegistrationDTO;
import com.shopping.microservices.identity_service.dto.TokenResponseDTO;
import com.shopping.microservices.identity_service.entity.UserEntity;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    void logout(String accessToken, String refreshToken);

    TokenResponseDTO refreshToken(String refreshToken);

    LoginResponseDTO getLoginResponseWithAssignedTokens(UserEntity userEntity);

    void signup(RegistrationDTO registrationDTO);

    void verifyUserRegistration(String token);

    void refreshUserVerification(String username);
}
