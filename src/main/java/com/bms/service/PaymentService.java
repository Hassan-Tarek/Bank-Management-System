package com.bms.service;

import com.bms.dto.PaymentDto;
import com.bms.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {

    List<PaymentDto> getPaymentsByFilters(PaymentStatus paymentStatus, LocalDate paymentDate);

    List<PaymentDto> getPaymentsByLoanId(Long userId, Long loanId);

    List<PaymentDto> getPaymentsByUserId(Long userId);

    PaymentDto payLoan(Long userId, PaymentDto paymentDto);
}
