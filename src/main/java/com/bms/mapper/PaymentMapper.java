package com.bms.mapper;

import com.bms.dto.PaymentDto;
import com.bms.entity.Payment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public PaymentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Payment toEntity(PaymentDto paymentDto) {
        return modelMapper.map(paymentDto, Payment.class);
    }

    public PaymentDto toDto(Payment payment) {
        return modelMapper.map(payment, PaymentDto.class);
    }
}
