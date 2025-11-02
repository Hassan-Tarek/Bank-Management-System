package com.bms.service;

import com.bms.dto.TransactionDto;
import com.bms.enums.TransactionStatus;
import com.bms.enums.TransactionType;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    List<TransactionDto> getTransactionsByFilters(TransactionType transactionType,
                                                  TransactionStatus transactionStatus,
                                                  LocalDate transactionDate);

    List<TransactionDto> getTransactionsByUserId(Long userId);

    TransactionDto deposit(Long userId, TransactionDto transactionDto);

    TransactionDto withdraw(Long userId, TransactionDto transactionDto);

    TransactionDto transfer(Long userId, TransactionDto transactionDto);
}
