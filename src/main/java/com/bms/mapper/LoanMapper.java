package com.bms.mapper;

import com.bms.dto.request.LoanRequestDto;
import com.bms.dto.response.LoanResponseDto;
import com.bms.entity.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    public Loan toEntity(LoanRequestDto loanRequestDto) {
        return Loan.builder()
                .type(loanRequestDto.loanType())
                .principalAmount(loanRequestDto.principalAmount())
                .remainingAmount(loanRequestDto.principalAmount())
                .durationMonths(loanRequestDto.durationMonths())
                .build();
    }

    public LoanResponseDto toDto(Loan loan) {
        return LoanResponseDto.builder()
                .id(loan.getId())
                .customerId(loan.getCustomer().getId())
                .disbursementAccountNumber(loan.getDisbursementAccount().getNumber())
                .type(loan.getType())
                .principalAmount(loan.getPrincipalAmount())
                .remainingAmount(loan.getRemainingAmount())
                .durationMonths(loan.getDurationMonths())
                .status(loan.getStatus())
                .createdAt(loan.getCreatedAt())
                .updatedAt(loan.getUpdatedAt())
                .build();
    }
}
