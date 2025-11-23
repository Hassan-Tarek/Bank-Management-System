package com.bms.service;

import com.bms.dto.request.TransactionRequestDto;
import com.bms.dto.response.TransactionResponseDto;
import com.bms.entity.Account;
import com.bms.entity.Transaction;
import com.bms.enums.TransactionStatus;
import com.bms.enums.TransactionType;
import com.bms.exception.BadRequestException;
import com.bms.exception.InsufficientFundsException;
import com.bms.exception.ResourceNotFoundException;
import com.bms.exception.UnauthorizedException;
import com.bms.mapper.TransactionMapper;
import com.bms.repository.AccountRepository;
import com.bms.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;

    @Value("${transaction.fees.withdrawal}")
    private BigDecimal withdrawalFees;

    @Value("${transaction.fees.transfer}")
    private BigDecimal transferFees;

    public Page<TransactionResponseDto> getAllTransactions(TransactionType type,
                                                           TransactionStatus status,
                                                           Pageable pageable) {
        return transactionRepository.findAllTransactions(type, status, pageable)
                .map(transactionMapper::toDto);
    }

    public List<TransactionResponseDto> getTransactionsByCustomerId(Long customerId) {
        return transactionRepository.findByCustomerId(customerId)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    public TransactionResponseDto getTransactionByReference(String transactionReference, Long customerId) {
        Transaction transaction = transactionRepository.findByReference(transactionReference)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found."));

        boolean isSenderOwner = transaction.getSender() != null &&
                transaction.getSender().getCustomer().getId().equals(customerId);
        boolean isReceiverOwner = transaction.getReceiver() != null &&
                transaction.getReceiver().getCustomer().getId().equals(customerId);
        if (!isSenderOwner && !isReceiverOwner) {
            throw new UnauthorizedException("Not authorized to view this transaction");
        }

        return transactionMapper.toDto(transaction);
    }

    @Transactional
    public TransactionResponseDto deposit(TransactionRequestDto transactionRequestDto, Long customerId) {
        Account receiverAccount = accountRepository.findByNumber(transactionRequestDto.receiverAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found."));

        if (!receiverAccount.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("You are not authorized to deposit to this account.");
        }

        Transaction transaction = transactionMapper.toEntity(transactionRequestDto);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setReceiver(receiverAccount);

        try {
            accountService.credit(receiverAccount.getId(), transactionRequestDto.amount());
            transaction.setStatus(TransactionStatus.SUCCESS);
        } catch (BadRequestException e) {
            transaction.setStatus(TransactionStatus.FAILED);
            saveFailedTransaction(transaction);
            throw e;
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);
    }

    @Transactional
    public TransactionResponseDto withdraw(TransactionRequestDto transactionRequestDto, Long customerId) {
        Account senderAccount = accountRepository.findByNumber(transactionRequestDto.senderAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found."));

        if (!senderAccount.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("You are not authorized to withdraw from this account.");
        }

        Transaction transaction = transactionMapper.toEntity(transactionRequestDto);
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setFee(withdrawalFees);
        transaction.setSender(senderAccount);

        try {
            BigDecimal totalAmount = transaction.getAmount().add(withdrawalFees);
            accountService.debit(senderAccount.getId(), totalAmount);
            transaction.setStatus(TransactionStatus.SUCCESS);
        } catch (BadRequestException | InsufficientFundsException e) {
            transaction.setStatus(TransactionStatus.FAILED);
            saveFailedTransaction(transaction);
            throw e;
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);
    }

    @Transactional
    public TransactionResponseDto transfer(TransactionRequestDto transactionRequestDto, Long customerId) {
        Account senderAccount = accountRepository.findByNumber(transactionRequestDto.senderAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found."));
        Account receiverAccount = accountRepository.findByNumber(transactionRequestDto.receiverAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver account not found."));

        if (!senderAccount.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("You are not authorized to transfer from this account.");
        }

        if (senderAccount.getNumber().equals(receiverAccount.getNumber())) {
            throw new BadRequestException("Cannot transfer to the same account.");
        }

        Transaction transaction = transactionMapper.toEntity(transactionRequestDto);
        transaction.setType(TransactionType.TRANSFER);
        transaction.setFee(transferFees);
        transaction.setSender(senderAccount);
        transaction.setReceiver(receiverAccount);

        try {
            // Debit sender, credit receiver
            BigDecimal totalAmount = transaction.getAmount().add(transferFees);
            accountService.debit(senderAccount.getId(), totalAmount);
            accountService.credit(receiverAccount.getId(), transactionRequestDto.amount());
            transaction.setStatus(TransactionStatus.SUCCESS);
        } catch (BadRequestException | InsufficientFundsException e) {
            transaction.setStatus(TransactionStatus.FAILED);
            saveFailedTransaction(transaction);
            throw e;
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void saveFailedTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
