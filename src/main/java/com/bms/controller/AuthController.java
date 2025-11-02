package com.bms.controller;

import com.bms.dto.auth.AuthRequest;
import com.bms.dto.auth.AuthResponse;
import com.bms.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/auth",
        produces = "application/json")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthRequest authRequest) {
        return ResponseEntity.ok(authService.register(authRequest));
    }

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest) {
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }
}
