package com.bms.dto.request;

import com.bms.enums.AccountType;
import jakarta.validation.constraints.NotBlank;

public record AccountRequestDto(
        @NotBlank(message = "Account type is required")
        AccountType type
) { }
