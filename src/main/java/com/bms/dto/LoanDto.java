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
public class LoanDto {

    private Long id;

    private Long userId;

    private Long accountNumber;

    private BigDecimal principalAmount;

    private BigDecimal installmentAmount;

    private Integer installmentCount;

    private Integer remainingInstallments;

    private String loanStatus;

    private LocalDateTime nextDueDate;

    private LocalDateTime createdAt;
}
