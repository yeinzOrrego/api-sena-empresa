package com.yapps.senaempresa.service;

import com.yapps.senaempresa.model.dto.AuthRequestDto;
import com.yapps.senaempresa.model.dto.AuthResponseDto;

public interface AuthService {
    AuthResponseDto authenticate(AuthRequestDto request);
    AuthResponseDto refreshToken(String refreshToken);
}
