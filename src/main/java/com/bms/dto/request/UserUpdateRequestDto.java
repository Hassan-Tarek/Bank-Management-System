package com.bms.dto.request;

import jakarta.validation.constraints.Size;

public record UserUpdateRequestDto(
        @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
        String firstName,

        @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
        String lastName,

        @Size(min = 11, message = "Phone must be at least 11 characters")
        String phone,

        @Size(min = 8, message = "password must be at least 8 characters")
        String password
) { }
