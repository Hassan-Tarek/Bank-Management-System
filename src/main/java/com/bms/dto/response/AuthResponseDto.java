package com.bms.dto.response;

public record AuthResponseDto(
        String accessToken,
        String tokenType,
        UserResponseDto userResponseDto
) {
    public AuthResponseDto(String accessToken, UserResponseDto userResponseDto) {
        this(accessToken, "Bearer", userResponseDto);
    }
}
