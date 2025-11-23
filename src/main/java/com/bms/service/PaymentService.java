package com.bms.service;

import com.bms.dto.request.PaymentRequestDto;
import com.bms.dto.response.PaymentResponseDto;
import com.bms.entity.Account;
import com.bms.entity.Loan;
import com.bms.entity.Payment;
import com.bms.enums.LoanStatus;
import com.bms.exception.BadRequestException;
import com.bms.exception.ResourceNotFoundException;
import com.bms.exception.UnauthorizedException;
import com.bms.mapper.PaymentMapper;
import com.bms.repository.AccountRepository;
import com.bms.repository.LoanRepository;
import com.bms.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final LoanRepository loanRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @Transactional
    public PaymentResponseDto makePayment(PaymentRequestDto paymentRequestDto, Long customerId) {
        Loan loan = loanRepository.findById(paymentRequestDto.loanId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        Account paymentAccount = accountRepository.findById(paymentRequestDto.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Authorization Check
        if (!loan.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("You are not authorized to make payments on this loan.");
        }
        if (!paymentAccount.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("You must use your own account to pay your loan.");
        }

        // Validation checks
        if (loan.getStatus() != LoanStatus.DISBURSED) {
            throw new BadRequestException("Cannot make payment on a loan that is not DISBURSED.");
        }

        BigDecimal paymentAmount = paymentRequestDto.amount();
        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be positive.");
        }

        BigDecimal remainingAmount = loan.getRemainingAmount();
        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(LoanStatus.PAID);
            loanRepository.save(loan);
            throw new BadRequestException("Loan is already fully paid.");
        }

        // Ensure payment doesn't overpay the remaining balance in one go
        BigDecimal amountToPay = paymentAmount.min(remainingAmount);

        // Debit payment account
        accountService.debit(paymentAccount.getId(), amountToPay);

        // Update Loan Status and Balance
        BigDecimal newRemainingAmount = remainingAmount.subtract(amountToPay);
        loan.setRemainingAmount(newRemainingAmount);

        if (newRemainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(LoanStatus.PAID);
        }
        loanRepository.save(loan);

        Payment payment = paymentMapper.toEntity(paymentRequestDto);
        payment.setLoan(loan);
        payment.setAccount(paymentAccount);
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    public Page<PaymentResponseDto> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(paymentMapper::toDto);
    }

    public List<PaymentResponseDto> getPaymentsByLoanId(Long loanId, Long customerId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        // Authorization check
        if (!loan.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("You are not authorized to view payments for this loan.");
        }

        return paymentRepository.findByLoanId(loanId)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public List<PaymentResponseDto> getPaymentsByAccountNumber(String accountNumber, Long customerId) {
        Account account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found."));

        // Authorization check
        if (!account.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("You are not authorized to view payments for this account.");
        }

        return paymentRepository.findByAccountNumber(accountNumber)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public List<PaymentResponseDto> getPaymentsByCustomerId(Long customerId) {
        return paymentRepository.findByCustomerId(customerId)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }
}
