package com.bms.dto.request;

import jakarta.validation.constraints.NotNull;

public record CardRequestDto(
        @NotNull(message = "Account ID is required")
        Long accountId
) { }
