package com.bms.dto.response;

public record ErrorResponseDto(
        String message,
        int status,
        Long timestamp
) { }
