package com.bms.controller;

import com.bms.dto.request.LoginRequestDto;
import com.bms.dto.request.RegisterRequestDto;
import com.bms.dto.response.AuthResponseDto;
import com.bms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/auth",
        produces = "application/json")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/register")
    public ResponseEntity<AuthResponseDto> register(
            @Valid @RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
