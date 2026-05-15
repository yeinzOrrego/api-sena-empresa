package com.yapps.senaempresa.service.impl;

import com.yapps.senaempresa.model.dto.AuthRequestDto;
import com.yapps.senaempresa.model.dto.AuthResponseDto;
import com.yapps.senaempresa.model.entity.User;
import com.yapps.senaempresa.model.entity.RefreshToken;
import com.yapps.senaempresa.repository.UserRepository;
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

    private final UserRepository UserRepository;
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

        User User = (User) authentication.getPrincipal();
        log.info("Authentication successful for user: {}", User.getUserId());

        String jwtToken = jwtService.generateToken(extraClaims(User), User);
        String refreshToken = refreshTokenHelper.generateRefreshToken(User);

        log.info("Tokens generated successfully for user: {}", User.getUserId());

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

        User User = UserRepository.findById(token.getUser().getUserId())
                .orElseThrow(() -> {
                    log.error("Failed token refresh: User {} not found", token.getUser().getUserId());
                    return new IllegalArgumentException("User not found");
                });

        String newJwtToken = jwtService.generateToken(extraClaims(User), User);
        String newRefreshToken = refreshTokenHelper.generateRefreshToken(User);

        log.info("New tokens generated successfully for user: {}", User.getUserId());

        return AuthResponseDto.builder()
                .accessToken(newJwtToken)
                .refreshToken(newRefreshToken)
                .build();
    }
    
    private Map<String, Object> extraClaims(User User) {
        return Map.of(
                "userId", User.getUserId(),
                "name", User.getUserFirstname() + " " + User.getUserLastname()
        );
    }
}
