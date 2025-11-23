package com.bms.dto.request;

import com.bms.enums.LoanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record LoanRequestDto(
        @NotBlank(message = "Loan type is required")
        LoanType loanType,

        @NotNull(message = "Principal amount is required")
        @Positive(message = "Principal amount must be greater than zero")
        BigDecimal principalAmount,

        @NotNull(message = "Duration months is required")
        @Positive(message = "Duration months must be greater than zero")
        Integer durationMonths,

        @NotBlank(message = "Disbursement account number is required")
        String disbursementAccountNumber
) { }
