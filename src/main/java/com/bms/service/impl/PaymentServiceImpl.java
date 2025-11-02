package com.bms.service.impl;

import com.bms.dto.PaymentDto;
import com.bms.dto.TransactionDto;
import com.bms.entity.Account;
import com.bms.entity.Loan;
import com.bms.entity.Payment;
import com.bms.entity.User;
import com.bms.enums.AccountStatus;
import com.bms.enums.LoanStatus;
import com.bms.enums.PaymentStatus;
import com.bms.enums.Role;
import com.bms.exception.BadRequestException;
import com.bms.exception.ResourceNotFoundException;
import com.bms.exception.UnauthorizedException;
import com.bms.mapper.PaymentMapper;
import com.bms.repository.AccountRepository;
import com.bms.repository.LoanRepository;
import com.bms.repository.PaymentRepository;
import com.bms.repository.UserRepository;
import com.bms.service.PaymentService;
import com.bms.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final LoanRepository loanRepository;
    private final TransactionService transactionService;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public List<PaymentDto> getPaymentsByFilters(PaymentStatus paymentStatus, LocalDate paymentDate) {
        return paymentRepository.findByFilters(paymentStatus, paymentDate)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public List<PaymentDto> getPaymentsByLoanId(Long userId, Long loanId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));

        if (!user.getRole().equals(Role.ADMIN) && !loan.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to view payments for this loan");
        }

        return paymentRepository.findByLoanId(loanId)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public List<PaymentDto> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentDto payLoan(Long userId, PaymentDto paymentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user not found with id: " + userId));
        Loan loan = loanRepository.findById(paymentDto.getLoanId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loan not found with id: " + paymentDto.getLoanId()));
        Account account = accountRepository.findByAccountNumber(paymentDto.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with account number: " + paymentDto.getAccountNumber()));

        if (!user.getId().equals(loan.getUser().getId())) {
            throw new UnauthorizedException("You are not authorized to pay for this loan");
        }

        if (!account.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            throw new BadRequestException("Account must be ACTIVE to make a payment");
        }

        if (!loan.getLoanStatus().equals(LoanStatus.ACTIVE)) {
            throw new BadRequestException("Loan status must be ACTIVE");
        }

        Payment payment = Payment.builder()
                .amount(paymentDto.getAmount())
                .paymentDate(LocalDateTime.now())
                .loan(loan)
                .account(account)
                .build();
        try {
            TransactionDto transactionDto = TransactionDto.builder()
                    .senderAccountNumber(account.getAccountNumber())
                    .amount(loan.getPrincipalAmount())
                    .build();
            transactionService.withdraw(userId, transactionDto);

            loan.setRemainingInstallments(loan.getRemainingInstallments() - 1);
            if (loan.getRemainingInstallments() == 0) {
                loan.setLoanStatus(LoanStatus.PAID);
            } else {
                loan.setNextDueDate(loan.getNextDueDate().plusMonths(1));
            }
            loanRepository.save(loan);

            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            Payment savedPayment = paymentRepository.save(payment);
            return paymentMapper.toDto(savedPayment);
        } catch (RuntimeException e) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw e;
        }
    }
}
