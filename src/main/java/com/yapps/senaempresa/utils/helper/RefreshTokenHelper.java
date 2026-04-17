package com.yapps.senaempresa.utils.helper;

import com.yapps.senaempresa.model.entity.Account;
import com.yapps.senaempresa.model.entity.RefreshToken;
import com.yapps.senaempresa.repository.RefreshTokenRepository;
import com.yapps.senaempresa.utils.enums.StatusEnum;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenHelper {

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenDurationMs;
    private static final String ACTIVE_STATUS = StatusEnum.ACTIVO.getValue();
    private static final String INACTIVE_STATUS = StatusEnum.INACTIVO.getValue();

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken generateRefreshToken(Account account) {

        // Invalidate existing tokens for the user
        invalidateExistingTokens(account);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(account)
                .refreshToken(UUID.randomUUID().toString())
                .expiresAt(Date.from(Instant.now().plusMillis(refreshTokenDurationMs)))
                .createdAt(new Date())
                .status(ACTIVE_STATUS)
                .build();
                
        return refreshTokenRepository.save(refreshToken);
    }

    public boolean verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().before(new Date())) {
            token.setStatus(INACTIVE_STATUS);
            refreshTokenRepository.save(token);
            throw new RuntimeException("Refresh token is expired. Please sign in again.");
        }
        return true;
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByRefreshTokenAndStatus(token, ACTIVE_STATUS)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }

    private void invalidateExistingTokens(Account user) {
        List<RefreshToken> existingTokens = refreshTokenRepository.findAllByUserAndStatus(user, ACTIVE_STATUS);

        // Mark existing tokens as inactive
        existingTokens.forEach(token -> token.setStatus(INACTIVE_STATUS));

        refreshTokenRepository.saveAll(existingTokens);
    }

}