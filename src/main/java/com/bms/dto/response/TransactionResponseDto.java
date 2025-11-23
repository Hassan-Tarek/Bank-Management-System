package com.bms.dto.response;

import com.bms.enums.TransactionStatus;
import com.bms.enums.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionResponseDto(
        Long id,
        String reference,
        String senderAccountNumber,
        String receiverAccountNumber,
        TransactionType type,
        BigDecimal amount,
        BigDecimal fee,
        TransactionStatus status,
        LocalDateTime createdAt
) { }
