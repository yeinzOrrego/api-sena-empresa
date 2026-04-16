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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Account account = (Account) authentication.getPrincipal();

        String jwtToken = jwtService.generateToken(extraClaims(account), account);
        String refreshToken = refreshTokenHelper.generateRefreshToken(account).getRefreshToken();

        return AuthResponseDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override 
    @Transactional
    public AuthResponseDto refreshToken(String refreshToken, String accessToken) {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        RefreshToken token = refreshTokenHelper.findByToken(refreshToken);

        if (!refreshTokenHelper.verifyExpiration(token)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String username = jwtService.extractUsername(accessToken);

        Account account = accountRepository.findByUserLogin(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newJwtToken = jwtService.generateToken(extraClaims(account), account);
        String newRefreshToken = refreshTokenHelper.generateRefreshToken(account).getRefreshToken();

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
