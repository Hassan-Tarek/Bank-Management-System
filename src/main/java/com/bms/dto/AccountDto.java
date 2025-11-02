package com.bms.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Long id;

    private Long userId;

    private Long accountNumber;

    @DecimalMin("0.0")
    private BigDecimal balance;

    @NotNull
    private String accountType;

    private String accountStatus;

    private LocalDateTime createdAt;
}
