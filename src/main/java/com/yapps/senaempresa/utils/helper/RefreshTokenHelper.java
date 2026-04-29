package com.yapps.senaempresa.utils.helper;

import com.yapps.senaempresa.model.entity.Account;
import com.yapps.senaempresa.model.entity.RefreshToken;
import com.yapps.senaempresa.repository.RefreshTokenRepository;
import com.yapps.senaempresa.utils.enums.StatusEnum;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RefreshTokenHelper {

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String ACTIVE_STATUS = StatusEnum.ACTIVO.getValue();

    public String generateRefreshToken(Account account) {

        // Invalidate existing tokens for the user
        invalidateExistingTokens(account);

        String tokenValue = generateSecureToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(account)
                .refreshToken(hash(tokenValue))
                .expiresAt(Instant.now().plusMillis(refreshTokenDurationMs))
                .createdAt(LocalDateTime.now())
                .status(ACTIVE_STATUS)
                .build();
                
        refreshTokenRepository.save(refreshToken);

        return tokenValue;
    }

    public boolean verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token is expired. Please sign in again.");
        }
        return true;
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByRefreshTokenAndStatus(hash(token), ACTIVE_STATUS)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }

    private void invalidateExistingTokens(Account user) {
        List<RefreshToken> existingTokens = refreshTokenRepository.findAllByUserAndStatus(user, ACTIVE_STATUS);
        refreshTokenRepository.deleteAll(existingTokens);
    }

    private String generateSecureToken() {
        byte[] tokenBytes = new byte[64];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }

}