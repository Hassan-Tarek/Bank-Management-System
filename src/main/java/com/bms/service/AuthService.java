package com.bms.service;

import com.bms.dto.request.LoginRequestDto;
import com.bms.dto.request.RegisterRequestDto;
import com.bms.dto.response.AuthResponseDto;
import com.bms.dto.response.UserResponseDto;
import com.bms.entity.User;
import com.bms.exception.BadRequestException;
import com.bms.exception.UserAlreadyExistsException;
import com.bms.mapper.UserMapper;
import com.bms.repository.UserRepository;
import com.bms.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("User already exists.");
        }

        User user = userMapper.toEntity(request);
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        UserResponseDto userResponseDto = userMapper.toDto(user);
        return new AuthResponseDto(accessToken, userResponseDto);
    }

    public AuthResponseDto login(LoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();
            if (!user.getActive()) {
                throw new BadRequestException("User is not active.");
            }

            String accessToken = jwtService.generateAccessToken(userDetails);
            UserResponseDto userResponseDto = userMapper.toDto(user);
            return new AuthResponseDto(accessToken, userResponseDto);
        } catch (Exception e) {
            throw new BadCredentialsException("Bad credentials.");
        }
    }
}
