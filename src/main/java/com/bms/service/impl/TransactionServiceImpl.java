package com.bms.service.impl;

import com.bms.dto.TransactionDto;
import com.bms.entity.Account;
import com.bms.entity.Transaction;
import com.bms.enums.AccountStatus;
import com.bms.enums.TransactionStatus;
import com.bms.enums.TransactionType;
import com.bms.exception.BadRequestException;
import com.bms.exception.InsufficientFundsException;
import com.bms.exception.ResourceNotFoundException;
import com.bms.exception.UnauthorizedException;
import com.bms.mapper.TransactionMapper;
import com.bms.repository.AccountRepository;
import com.bms.repository.TransactionRepository;
import com.bms.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public List<TransactionDto> getTransactionsByFilters(TransactionType transactionType,
                                                         TransactionStatus transactionStatus,
                                                         LocalDate transactionDate) {
        return transactionRepository.findByFilters(transactionType, transactionStatus, transactionDate)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @Override
    public List<TransactionDto> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public TransactionDto deposit(Long userId, TransactionDto transactionDto) {
        Account account = accountRepository.findByAccountNumber(transactionDto.getReceiverAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + transactionDto.getReceiverAccountNumber()));

        if (!account.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to deposit this transaction");
        }

        if (!account.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            throw new BadRequestException("Account must be ACTIVE to perform a deposit");
        }

        Transaction transaction = Transaction.builder()
                .amount(transactionDto.getAmount())
                .transactionType(TransactionType.DEPOSIT)
                .transactionDate(LocalDateTime.now())
                .receiver(account)
                .build();

        try {
            if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Amount must be greater than zero");
            }

            account.setBalance(account.getBalance().add(transaction.getAmount()));
            accountRepository.save(account);

            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
            Transaction savedTransaction = transactionRepository.save(transaction);
            return transactionMapper.toDto(savedTransaction);
        } catch (BadRequestException e) {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw e;
        }
    }

    @Override
    @Transactional
    public TransactionDto withdraw(Long userId, TransactionDto transactionDto) {
        Account account = accountRepository.findByAccountNumber(transactionDto.getReceiverAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + transactionDto.getReceiverAccountNumber()));

        if (!account.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to deposit this transaction");
        }

        if (!account.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            throw new BadRequestException("Account must be ACTIVE to perform a withdrawal");
        }

        Transaction transaction = Transaction.builder()
                .amount(transactionDto.getAmount())
                .transactionType(TransactionType.WITHDRAW)
                .transactionDate(LocalDateTime.now())
                .receiver(account)
                .build();

        try {
            if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Amount must be greater than zero");
            }

            if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new InsufficientFundsException("Insufficient funds");
            }

            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            accountRepository.save(account);

            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
            Transaction savedTransaction = transactionRepository.save(transaction);
            return transactionMapper.toDto(savedTransaction);
        } catch (BadRequestException | InsufficientFundsException e) {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw e;
        }
    }

    @Override
    @Transactional
    public TransactionDto transfer(Long userId, TransactionDto transactionDto) {
        Account senderAccount = accountRepository.findByAccountNumber(transactionDto.getSenderAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sender account not found with account number: " + transactionDto.getSenderAccountNumber()));
        Account receiverAccount = accountRepository.findByAccountNumber(transactionDto.getReceiverAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receiver account not found with account number: " + transactionDto.getReceiverAccountNumber()));

        if (!senderAccount.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to transfer this transaction");
        }

        if (!senderAccount.getAccountStatus().equals(AccountStatus.ACTIVE)
                || !receiverAccount.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            throw new BadRequestException("Both sender and receiver accounts must be ACTIVE to perform a transfer");
        }

        if (senderAccount.getAccountNumber().equals(receiverAccount.getAccountNumber())){
            throw new BadRequestException("Cannot transfer to the same account");
        }

        Transaction transaction = Transaction.builder()
                .amount(transactionDto.getAmount())
                .transactionType(TransactionType.TRANSFER)
                .transactionDate(LocalDateTime.now())
                .sender(senderAccount)
                .receiver(receiverAccount)
                .build();

        try {
            if (transaction.getAmount() == null ||
                    transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Amount must be greater than zero");
            }

            if (senderAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new InsufficientFundsException("Insufficient funds in sender account");
            }

            // Debit sender, credit receiver
            senderAccount.setBalance(senderAccount.getBalance().subtract(transaction.getAmount()));
            receiverAccount.setBalance(receiverAccount.getBalance().add(transaction.getAmount()));

            accountRepository.save(senderAccount);
            accountRepository.save(receiverAccount);

            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
            Transaction savedTransaction = transactionRepository.save(transaction);
            return transactionMapper.toDto(savedTransaction);
        } catch (BadRequestException | InsufficientFundsException e) {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw e;
        }
    }
}
