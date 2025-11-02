package com.bms.dto;

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
public class TransactionDto {

    private Long id;

    private Long senderAccountNumber;

    private Long receiverAccountNumber;

    @NotNull
    private BigDecimal amount;

    private String transactionType;

    private String transactionStatus;

    private String transactionReference;

    private LocalDateTime transactionDate;
}
