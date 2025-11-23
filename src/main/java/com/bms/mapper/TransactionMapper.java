package com.bms.mapper;

import com.bms.dto.request.TransactionRequestDto;
import com.bms.dto.response.TransactionResponseDto;
import com.bms.entity.Transaction;

import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toEntity(TransactionRequestDto transactionDto) {
        return Transaction.builder()
                .amount(transactionDto.amount())
                .build();
    }

    public TransactionResponseDto toDto(Transaction transaction) {
        return TransactionResponseDto.builder()
                .id(transaction.getId())
                .reference(transaction.getReference())
                .senderAccountNumber(transaction.getSender() != null ? transaction.getSender().getNumber() : null)
                .receiverAccountNumber(transaction.getReceiver() != null ? transaction.getReceiver().getNumber() : null)
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .fee(transaction.getFee())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreated_at())
                .build();
    }
}
