package com.bms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    @NotNull
    private String email;

    @NotNull
    private String password;

    private String role;

    private String phone;

    private String address;

    private LocalDateTime createdAt;
}
