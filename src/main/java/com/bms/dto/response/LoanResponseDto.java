package com.bms.dto.response;

import com.bms.enums.LoanStatus;
import com.bms.enums.LoanType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record LoanResponseDto(
        Long id,
        Long customerId,
        String disbursementAccountNumber,
        LoanType type,
        BigDecimal principalAmount,
        BigDecimal remainingAmount,
        Integer durationMonths,
        LoanStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
