package com.bms.service.impl;

import com.bms.dto.LoanDto;
import com.bms.dto.TransactionDto;
import com.bms.entity.Loan;
import com.bms.entity.User;
import com.bms.enums.LoanStatus;
import com.bms.exception.BadRequestException;
import com.bms.exception.ResourceNotFoundException;
import com.bms.mapper.LoanMapper;
import com.bms.repository.LoanRepository;
import com.bms.repository.UserRepository;
import com.bms.service.LoanService;
import com.bms.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;

    @Override
    public List<LoanDto> getLoansByFilters(LoanStatus loanStatus) {
        return loanRepository.findByFilters(loanStatus)
                .stream()
                .map(loanMapper::toDto)
                .toList();
    }

    @Override
    public List<LoanDto> getLoansByUserId(Long userId) {
        return loanRepository.findByUserId(userId)
                .stream()
                .map(loanMapper::toDto)
                .toList();
    }

    @Override
    public LoanDto applyForLoan(Long userId, LoanDto loanDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found with id: " + userId));
        Loan loan = Loan.builder()
                .principalAmount(loanDto.getPrincipalAmount())
                .installmentCount(loanDto.getInstallmentCount())
                .user(user)
                .build();
        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }

    @Override
    public LoanDto approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));
        loan.setLoanStatus(LoanStatus.APPROVED);
        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }

    @Override
    public LoanDto rejectLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));
        loan.setLoanStatus(LoanStatus.REJECTED);
        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }

    @Override
    public LoanDto disburseLoan(Long userId, Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));

        if (loan.getLoanStatus() != LoanStatus.APPROVED) {
            throw new BadRequestException("Only approved loans can be disburse");
        }

        TransactionDto transactionDto = TransactionDto.builder()
                .receiverAccountNumber(loan.getAccount().getAccountNumber())
                .amount(loan.getPrincipalAmount())
                .build();
        transactionService.deposit(userId, transactionDto);

        loan.setLoanStatus(LoanStatus.ACTIVE);
        loan.setNextDueDate(LocalDateTime.now().plusMonths(1));
        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }
}
