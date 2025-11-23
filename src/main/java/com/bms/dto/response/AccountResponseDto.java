package com.bms.dto.response;

import com.bms.enums.AccountStatus;
import com.bms.enums.AccountType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AccountResponseDto(
        Long id,
        String number,
        Long customerId,
        AccountType type,
        BigDecimal balance,
        AccountStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
