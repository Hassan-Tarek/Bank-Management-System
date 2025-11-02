package com.bms.service;

import com.bms.dto.auth.AuthRequest;
import com.bms.dto.auth.AuthResponse;

public interface AuthService {

    AuthResponse register(AuthRequest authRequest);

    AuthResponse authenticate(AuthRequest authRequest);
}
