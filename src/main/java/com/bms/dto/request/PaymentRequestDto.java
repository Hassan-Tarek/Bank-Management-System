package com.bms.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequestDto(
        @NotNull(message = "Loan ID is required")
        Long loanId,

        @NotNull(message = "Account ID is required")
        Long accountId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount
) { }
