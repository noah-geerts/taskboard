package com.noahgeerts.taskboard.service;

import java.nio.file.AccessDeniedException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import javax.naming.NameNotFoundException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.noahgeerts.taskboard.domain.RefreshToken;
import com.noahgeerts.taskboard.domain.User;
import com.noahgeerts.taskboard.domain.dto.AuthInternalDto;
import com.noahgeerts.taskboard.domain.dto.AuthRequestDto;
import com.noahgeerts.taskboard.persistence.RefreshTokenRepository;
import com.noahgeerts.taskboard.persistence.UserRepository;

@Service
public class AuthService {

    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private JwtService jwtService;

    public AuthService(UserRepository userRepository, JwtService jwtService,
            RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public String generateRefreshToken() {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public void signUp(AuthRequestDto signupRequest) {
        User newUser = new User();
        newUser.setEmail(signupRequest.getEmail());
        newUser.setPassword(signupRequest.getPassword());
        userRepository.save(newUser);
    }

    public AuthInternalDto logIn(AuthRequestDto loginRequest) throws NameNotFoundException, AccessDeniedException {
        // Verify authentication
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty())
            throw new NameNotFoundException();
        User user = userOptional.get();
        if (!user.getPassword().equals(loginRequest.getPassword()))
            throw new AccessDeniedException("Password incorrect");

        // Generate refresh token and jwt
        String jwt = jwtService.generateToken(user);
        RefreshToken refreshToken = RefreshToken.builder().token(generateRefreshToken()).user(user)
                .expiryDate(Instant.now().plus(Duration.ofDays(30))).revoked(false).build();
        refreshTokenRepository.save(refreshToken);
        return AuthInternalDto.builder().jwt(jwt).refreshToken(refreshToken.getToken()).build();
    }

    public AuthInternalDto refresh(String refreshToken) throws AccessDeniedException {
        // Check that refresh token is valid
        Optional<RefreshToken> optionalToken = refreshTokenRepository.findByToken(refreshToken);
        if (optionalToken.isEmpty())
            throw new AccessDeniedException("Refresh token is invalid");
        RefreshToken oldToken = optionalToken.get();
        if (oldToken.getExpiryDate().isBefore(Instant.now()))
            throw new AccessDeniedException("Refresh token is expired");

        // Delete old refresh token, create new one
        refreshTokenRepository.delete(oldToken);
        RefreshToken newToken = RefreshToken.builder().token(generateRefreshToken()).user(oldToken.getUser())
                .expiryDate(Instant.now().plus(Duration.ofDays(30))).revoked(false).build();
        refreshTokenRepository.save(newToken);

        // Create JWT
        String jwt = jwtService.generateToken(oldToken.getUser());
        return AuthInternalDto.builder().jwt(jwt).refreshToken(newToken.getToken()).build();
    }

}
