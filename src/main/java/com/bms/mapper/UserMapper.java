package com.bms.mapper;

import com.bms.dto.request.RegisterRequestDto;
import com.bms.dto.response.UserResponseDto;
import com.bms.entity.User;
import com.bms.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bms.dto.request.UserUpdateRequestDto;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toEntity(RegisterRequestDto requestDto) {
        return User.builder()
                .firstName(requestDto.firstName())
                .lastName(requestDto.lastName())
                .email(requestDto.email())
                .password(passwordEncoder.encode(requestDto.password()))
                .phone(requestDto.phone())
                .role(Role.CUSTOMER)
                .build();
    }

    public void partialUpdate(UserUpdateRequestDto dto, User user) {
        Optional.ofNullable(dto.firstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(dto.lastName()).ifPresent(user::setLastName);
        Optional.ofNullable(dto.phone()).ifPresent(user::setPhone);
        Optional.ofNullable(dto.password()).ifPresent(password ->
                user.setPassword(passwordEncoder.encode(password))
        );
    }

    public UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .active(user.getActive())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
