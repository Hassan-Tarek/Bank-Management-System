package com.bms.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRequest {

    private String name;

    @NotNull
    private String email;

    @NotNull
    private String password;
}
