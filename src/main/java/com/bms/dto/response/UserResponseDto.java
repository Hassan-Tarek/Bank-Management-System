package com.bms.dto.response;

import com.bms.enums.Role;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        Boolean active,
        Role role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
