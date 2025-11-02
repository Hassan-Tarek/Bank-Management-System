package com.bms.mapper;

import com.bms.dto.TransactionDto;
import com.bms.entity.Transaction;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public TransactionMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Transaction toEntity(TransactionDto transactionDto) {
        return modelMapper.map(transactionDto, Transaction.class);
    }

    public TransactionDto toDto(Transaction transaction) {
        return modelMapper.map(transaction, TransactionDto.class);
    }
}
