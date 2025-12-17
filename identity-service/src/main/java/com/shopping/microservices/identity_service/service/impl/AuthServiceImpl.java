package com.shopping.microservices.identity_service.service.impl;

import com.shopping.microservices.identity_service.exception.BlacklistedTokenException;
import com.shopping.microservices.identity_service.exception.InvalidTokenException;
import com.shopping.microservices.identity_service.exception.LoginNotValidException;
import com.shopping.microservices.identity_service.exception.SignUpNotValidException;
import com.shopping.microservices.identity_service.utility.TimeUtils;
import com.shopping.microservices.identity_service.dto.LoginRequestDTO;
import com.shopping.microservices.identity_service.dto.LoginResponseDTO;
import com.shopping.microservices.identity_service.dto.RegistrationDTO;
import com.shopping.microservices.identity_service.dto.TokenResponseDTO;
import com.shopping.microservices.identity_service.entity.SignUpEntity;
import com.shopping.microservices.identity_service.enumeration.SignUpStatus;
import com.shopping.microservices.identity_service.mapper.AuthMapper;
import com.shopping.microservices.identity_service.properties.JwtProperties;
import com.shopping.microservices.identity_service.repository.SignUpRepository;
import com.shopping.microservices.identity_service.service.AuthService;
import com.shopping.microservices.identity_service.service.JwtKeyService;
import com.shopping.microservices.identity_service.service.TokenBlacklistService;
import com.shopping.microservices.identity_service.entity.UserEntity;
import com.shopping.microservices.identity_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final int EXPIRED_VERIFICATION_TOKEN_SECONDS = 300; // 300s
    private final AuthMapper authMapper;
    private final SignUpRepository signUpRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    private final JwtKeyService jwtKeyService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        log.info("Login attempt for username: {}", loginRequestDTO.getUsername());

        UserEntity user = userService.getUserByUsername(loginRequestDTO.getUsername());
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            log.warn("Invalid password attempt for user: {}", loginRequestDTO.getUsername());
            throw new LoginNotValidException("Invalid password");
        }

        log.info("User {} logged in successfully", loginRequestDTO.getUsername());
        return getLoginResponseWithAssignedTokens(user);
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        tokenBlacklistService.blacklistAccessToken(accessToken);
        tokenBlacklistService.blacklistRefreshToken(refreshToken);
    }

    @Override
    public TokenResponseDTO refreshToken(String refreshToken) {
        if (!jwtKeyService.validateToken(refreshToken)) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }
        if (tokenBlacklistService.isRefreshTokenBlacklisted(refreshToken)) {
            throw new BlacklistedTokenException("Refresh token is blacklisted");
        }

        String username = jwtKeyService.extractUsername(refreshToken);
        UserEntity user = userService.getUserByUsername(username);

        // Create new access token
        String newAccessToken = jwtKeyService.generateToken(user, jwtProperties.getTokenExp());
        // Create new refresh token
        String newRefreshToken = jwtKeyService.generateRefreshToken(user, jwtProperties.getRefreshTokenExp());

        // Blacklist the old refresh token cũ
        tokenBlacklistService.blacklistRefreshToken(refreshToken);

        return TokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public LoginResponseDTO getLoginResponseWithAssignedTokens(UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        LoginResponseDTO responseDTO = authMapper.map(user);

        // Generate token and refresh token
        responseDTO.setToken(jwtKeyService.generateToken(user, jwtProperties.getTokenExp()));
        responseDTO.setRefreshToken(jwtKeyService.generateRefreshToken(user, jwtProperties.getRefreshTokenExp()));

        log.info("Generated tokens for user: {}", user.getUsername());
        return responseDTO;
    }

    @Override
    public void signup(RegistrationDTO registrationDTO) {
        log.info("Signing up user: {}", registrationDTO.getUsername());

        checkExistingSignUpUser(registrationDTO);
        SignUpEntity signUpEntity = authMapper.map(registrationDTO, new SignUpEntity());
        signUpEntity.setStatus(SignUpStatus.PENDING);
        signUpEntity.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        assignNewVerifyToken(signUpEntity);

        signUpRepository.save(signUpEntity);
        log.info("User {} signed up successfully with status PENDING", registrationDTO.getUsername());
    }

    @Override
    public void verifyUserRegistration(String token) {
        log.info("Verifying user registration with token: {}", token);
        SignUpEntity signUpEntity = signUpRepository.findByCurrentVerificationTokenAndStatusIn(token,
                        List.of(SignUpStatus.PENDING))
                .orElseThrow(() -> new SignUpNotValidException("Token is not valid: " + token));

        // Validate token
        if (signUpEntity.getExpiredVerificationTokenDate() == null) {
            log.warn("Verification token has no expiry date: {}", token);
            throw new SignUpNotValidException("Token is not valid: " + token);
        }
        if (LocalDateTime.now().isAfter(signUpEntity.getExpiredVerificationTokenDate())) {
            log.warn("Verification token expired: {}", token);
            throw new SignUpNotValidException("Token is expired: " + token);
        }

        signUpEntity.setStatus(SignUpStatus.SUCCESS);
        signUpRepository.save(signUpEntity);

        // Create user entity
        userService.createUser(signUpEntity);

        log.info("User '{}' verified and created successfully", signUpEntity.getUsername());
    }

    @Override
    public void refreshUserVerification(String username) {
        log.info("Refreshing verification token for username: {}", username);
        SignUpEntity signUpEntity = signUpRepository.findByUsernameAndStatusIn(username,
                        List.of(SignUpStatus.PENDING))
                .orElseThrow(() -> new SignUpNotValidException("Username is not found: " + username));
        assignNewVerifyToken(signUpEntity);
        signUpRepository.save(signUpEntity);
    }

    private void checkExistingSignUpUser(RegistrationDTO dto) {
        List<SignUpStatus> validStatuses = List.of(SignUpStatus.PENDING, SignUpStatus.SUCCESS);

        if (signUpRepository.existsByUsernameAndStatusIn(dto.getUsername(), validStatuses)) {
            log.warn("Attempt to signup with existing username: {}", dto.getUsername());
            throw new SignUpNotValidException("Username already exists");
        }

        if (signUpRepository.existsByEmailAndStatusIn(dto.getEmail(), validStatuses)) {
            log.warn("Attempt to signup with existing email: {}", dto.getEmail());
            throw new SignUpNotValidException("Email already registered");
        }
    }

    private void assignNewVerifyToken(SignUpEntity signUpEntity) {
        final String token = UUID.randomUUID().toString();
        signUpEntity.setCurrentVerificationToken(token);
        signUpEntity.setExpiredVerificationTokenDate(TimeUtils.getExpiredTime(EXPIRED_VERIFICATION_TOKEN_SECONDS));
        log.info("Assign new token {} for user {}", token, signUpEntity.getName());
    }
}
