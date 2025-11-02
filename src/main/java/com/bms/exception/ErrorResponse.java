package com.bms.exception;

public record ErrorResponse(
        String message,
        int status,
        Long timestamp
) { }
