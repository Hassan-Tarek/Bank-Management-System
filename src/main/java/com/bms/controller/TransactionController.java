package com.bms.controller;

import com.bms.dto.TransactionDto;
import com.bms.enums.TransactionStatus;
import com.bms.enums.TransactionType;
import com.bms.security.CustomUserDetails;
import com.bms.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/transactions",
        produces = "application/json")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionDto>> getTransactionsByFilters(
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) TransactionStatus transactionStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate transactionDate) {
        List<TransactionDto> transactions = transactionService
                .getTransactionsByFilters(transactionType, transactionStatus, transactionDate);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping(path = "/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionDto>> getTransactionsByUserId(@PathVariable Long userId) {
        List<TransactionDto> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping(path = "/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TransactionDto>> getMyTransactions(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getId();
        List<TransactionDto> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping(path = "/deposit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionDto> deposit(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid TransactionDto transactionDto) {
        Long userId = customUserDetails.getUser().getId();
        TransactionDto transaction = transactionService.deposit(userId, transactionDto);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping(path = "/withdraw")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionDto> withdraw(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid TransactionDto transactionDto) {
        Long userId = customUserDetails.getUser().getId();
        TransactionDto transaction = transactionService.withdraw(userId, transactionDto);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping(path = "/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionDto> transfer(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid TransactionDto transactionDto) {
        Long userId = customUserDetails.getUser().getId();
        TransactionDto transaction = transactionService.transfer(userId, transactionDto);
        return ResponseEntity.ok(transaction);
    }
}
