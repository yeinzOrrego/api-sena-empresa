package com.yapps.senaempresa.controller;

import com.yapps.senaempresa.model.dto.AuthRequestDto;
import com.yapps.senaempresa.model.dto.AuthResponseDto;
import com.yapps.senaempresa.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> authenticate(@RequestBody AuthRequestDto request) {
        return new ResponseEntity<>(authService.authenticate(request), HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(
            @RequestHeader(required = true) String refreshToken) {

        return new ResponseEntity<>(authService.refreshToken(refreshToken), HttpStatus.OK);
    }
}
