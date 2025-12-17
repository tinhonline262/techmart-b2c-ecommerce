package com.shopping.microservices.identity_service.controller;

import com.shopping.microservices.identity_service.dto.*;
import com.shopping.microservices.identity_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ApiResponse.success(HttpStatus.OK.value(), "Login successfully", authService.login(loginRequestDTO));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    void logout(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO, @RequestHeader("Authorization") String authHeader) {
        final String accessToken = authHeader.substring(7);
        final String refreshToken = refreshTokenDTO.refreshToken();
        authService.logout(accessToken, refreshToken);
    }

    @PostMapping("/refresh-token/{refreshToken}")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<TokenResponseDTO> refreshToken(@PathVariable String refreshToken) {
        return ApiResponse.success(HttpStatus.OK.value(), "Refresh token successfully", authService.refreshToken(refreshToken));
    }

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<Void> signUp(@Valid @RequestBody RegistrationDTO registrationDTO) {
        authService.signup(registrationDTO);
        return ApiResponse.success(HttpStatus.OK.value(), "User registered successfully", null);
    }

    @PostMapping("/verification/{userVerifyToken}")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<Void> verifyUser(@PathVariable String userVerifyToken) {
        authService.verifyUserRegistration(userVerifyToken);
        return ApiResponse.success(HttpStatus.OK.value(), "User verified successfully", null);
    }

    @PostMapping("/refresh-user-verification")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<Void> refreshUserVerification(@RequestParam("username") String username) {
        authService.refreshUserVerification(username);
        return ApiResponse.success(HttpStatus.OK.value(), "User verification email sent successfully", null);
    }
}
