package com.yapps.senaempresa.service.impl;

import com.yapps.senaempresa.model.dto.AuthRequestDto;
import com.yapps.senaempresa.model.dto.AuthResponseDto;
import com.yapps.senaempresa.model.entity.Account;
import com.yapps.senaempresa.model.entity.RefreshToken;
import com.yapps.senaempresa.repository.AccountRepository;
import com.yapps.senaempresa.security.JwtService;
import com.yapps.senaempresa.service.AuthService;
import com.yapps.senaempresa.utils.helper.RefreshTokenHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenHelper refreshTokenHelper;

    @Override
    @Transactional
    public AuthResponseDto authenticate(AuthRequestDto request) {
        log.info("Starting authentication process for user: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Account account = (Account) authentication.getPrincipal();
        log.info("Authentication successful for user: {}", account.getUserId());

        String jwtToken = jwtService.generateToken(extraClaims(account), account);
        String refreshToken = refreshTokenHelper.generateRefreshToken(account);

        log.info("Tokens generated successfully for user: {}", account.getUserId());

        return AuthResponseDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override 
    @Transactional
    public AuthResponseDto refreshToken(String refreshToken) {
        log.info("Starting token refresh process");

        RefreshToken token = refreshTokenHelper.findByToken(refreshToken);

        if (!refreshTokenHelper.verifyExpiration(token)) {
            log.warn("Failed token refresh: Refresh token is expired or invalid for user: {}", token.getUser().getUserId());
            throw new IllegalArgumentException("Invalid refresh token");
        }

        log.info("Refreshing token for identified user: {}", token.getUser().getUserId());

        Account account = accountRepository.findById(token.getUser().getUserId())
                .orElseThrow(() -> {
                    log.error("Failed token refresh: User {} not found", token.getUser().getUserId());
                    return new IllegalArgumentException("User not found");
                });

        String newJwtToken = jwtService.generateToken(extraClaims(account), account);
        String newRefreshToken = refreshTokenHelper.generateRefreshToken(account);

        log.info("New tokens generated successfully for user: {}", account.getUserId());

        return AuthResponseDto.builder()
                .accessToken(newJwtToken)
                .refreshToken(newRefreshToken)
                .build();
    }
    
    private Map<String, Object> extraClaims(Account account) {
        return Map.of(
                "userId", account.getUserId(),
                "name", account.getUserFirstname() + " " + account.getUserLastname()
        );
    }
}
