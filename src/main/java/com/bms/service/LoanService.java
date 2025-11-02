package com.bms.service;

import com.bms.dto.LoanDto;
import com.bms.enums.LoanStatus;

import java.util.List;

public interface LoanService {
    List<LoanDto> getLoansByFilters(LoanStatus loanStatus);

    List<LoanDto> getLoansByUserId(Long userId);

    LoanDto applyForLoan(Long userId, LoanDto loanDto);

    LoanDto approveLoan(Long loanId);

    LoanDto rejectLoan(Long loanId);

    LoanDto disburseLoan(Long userId, Long loanId);
}
