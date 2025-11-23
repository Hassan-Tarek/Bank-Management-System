package com.bms.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record TransactionRequestDto(
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        String senderAccountNumber,

        String receiverAccountNumber
) { }
