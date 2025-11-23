package com.bms.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentResponseDto(
        Long id,
        Long loanId,
        Long accountId,
        BigDecimal amount,
        LocalDateTime createdAt
) { }
