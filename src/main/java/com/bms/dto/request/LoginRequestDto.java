package com.bms.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Builder;

@Builder
public record LoginRequestDto(
        @NotBlank(message = "email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password
) { }
