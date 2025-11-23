package com.bms.controller;

import com.bms.dto.request.TransactionRequestDto;
import com.bms.dto.response.TransactionResponseDto;
import com.bms.enums.TransactionStatus;
import com.bms.enums.TransactionType;
import com.bms.security.CustomUserDetails;
import com.bms.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/transactions",
        produces = "application/json")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping(path = "/all")
    public ResponseEntity<Page<TransactionResponseDto>> getAllTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TransactionResponseDto> transactions = transactionService.getAllTransactions(type, status, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping(path = "/me")
    public ResponseEntity<List<TransactionResponseDto>> getMyTransactions(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        List<TransactionResponseDto> transactions = transactionService.getTransactionsByCustomerId(customerId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping(path = "/ref/{transactionReference}")
    public ResponseEntity<TransactionResponseDto> getTransactionByReference(
            @PathVariable String transactionReference,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        TransactionResponseDto transaction = transactionService
                .getTransactionByReference(transactionReference, customerId);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping(path = "/deposit")
    public ResponseEntity<TransactionResponseDto> deposit(
            @Valid @RequestBody TransactionRequestDto transactionRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        TransactionResponseDto transaction = transactionService.deposit(transactionRequestDto, customerId);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping(path = "/withdraw")
    public ResponseEntity<TransactionResponseDto> withdraw(
            @Valid @RequestBody TransactionRequestDto transactionRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        TransactionResponseDto transaction = transactionService.withdraw(transactionRequestDto, customerId);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping(path = "/transfer")
    public ResponseEntity<TransactionResponseDto> transfer(
            @Valid @RequestBody TransactionRequestDto transactionRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        TransactionResponseDto transaction = transactionService.transfer(transactionRequestDto, customerId);
        return ResponseEntity.ok(transaction);
    }
}
