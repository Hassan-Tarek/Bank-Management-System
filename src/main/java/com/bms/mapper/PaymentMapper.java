package com.bms.mapper;

import com.bms.dto.request.PaymentRequestDto;
import com.bms.dto.response.PaymentResponseDto;
import com.bms.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequestDto paymentRequestDto) {
        return Payment.builder()
                .amount(paymentRequestDto.amount())
                .build();
    }

    public PaymentResponseDto toDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .loanId(payment.getLoan().getId())
                .accountId(payment.getAccount().getId())
                .amount(payment.getAmount())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
