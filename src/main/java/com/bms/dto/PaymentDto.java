package com.bms.dto;

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
public class PaymentDto {

    private Long id;

    private Long loanId;

    private Long accountNumber;

    private BigDecimal amount;

    private String paymentStatus;

    private LocalDateTime paymentDate;
}
