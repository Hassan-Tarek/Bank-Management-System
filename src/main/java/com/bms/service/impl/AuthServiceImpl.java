package com.bms.service.impl;

import com.bms.dto.auth.AuthRequest;
import com.bms.dto.auth.AuthResponse;
import com.bms.entity.User;
import com.bms.enums.Role;
import com.bms.exception.UserAlreadyExistsException;
import com.bms.repository.UserRepository;
import com.bms.security.CustomUserDetails;
import com.bms.security.JwtService;
import com.bms.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(AuthRequest authRequest) {
        if (userRepository.existsByEmail(authRequest.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = User.builder()
                .name(authRequest.getName())
                .email(authRequest.getEmail())
                .password(passwordEncoder.encode(authRequest.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String jwtToken = jwtService.generateToken(userDetails);
        return new AuthResponse(jwtToken);
    }

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String jwtToken = jwtService.generateToken(userDetails);
            return new AuthResponse(jwtToken);
        } catch (Exception e) {
            throw new BadCredentialsException("Bad credentials");
        }
    }
}
