package com.bms.service;

import com.bms.dto.request.LoanRequestDto;
import com.bms.dto.response.LoanResponseDto;
import com.bms.entity.Account;
import com.bms.entity.Loan;
import com.bms.entity.User;
import com.bms.enums.LoanStatus;
import com.bms.exception.BadRequestException;
import com.bms.exception.ResourceNotFoundException;
import com.bms.exception.UnauthorizedException;
import com.bms.mapper.LoanMapper;
import com.bms.repository.AccountRepository;
import com.bms.repository.LoanRepository;
import com.bms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final LoanMapper loanMapper;

    public Page<LoanResponseDto> getAllLoans(LoanStatus status, Pageable pageable) {
        return loanRepository.findAllLoans(status, pageable)
                .map(loanMapper::toDto);
    }

    public List<LoanResponseDto> getLoansByCustomerId(Long customerId) {
        return loanRepository.findByCustomerId(customerId)
                .stream()
                .map(loanMapper::toDto)
                .toList();
    }

    public LoanResponseDto applyForLoan(LoanRequestDto loanRequestDto, Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found."));
        Account disbursementAccount = accountRepository.findByNumber(loanRequestDto.disbursementAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found."));

        if (!disbursementAccount.getCustomer().getId().equals(customer.getId())) {
            throw new UnauthorizedException("You are not allowed to apply for this loan.");
        }

        Loan loan = loanMapper.toEntity(loanRequestDto);
        loan.setCustomer(customer);
        loan.setDisbursementAccount(disbursementAccount);
        loanRepository.save(loan);
        return loanMapper.toDto(loan);
    }

    public LoanResponseDto approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found."));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new BadRequestException("Loan is not Pending.");
        }

        loan.setStatus(LoanStatus.APPROVED);
        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }

    public LoanResponseDto rejectLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found."));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new BadRequestException("Loan is not Pending.");
        }

        loan.setStatus(LoanStatus.REJECTED);
        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }

    public LoanResponseDto disburseLoan(Long loanId, String accountNumber, Long customerId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found."));
        Account disbursementAccount = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found."));

        if (!disbursementAccount.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("Disbursement account is not belong to you.");
        }

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new BadRequestException("Loan is not Approved.");
        }

        accountService.credit(disbursementAccount.getId(), loan.getPrincipalAmount());
        loan.setStatus(LoanStatus.DISBURSED);
        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }
}
