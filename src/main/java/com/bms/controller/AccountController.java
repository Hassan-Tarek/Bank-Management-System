package com.bms.controller;

import com.bms.dto.request.AccountRequestDto;
import com.bms.dto.response.AccountResponseDto;
import com.bms.enums.AccountStatus;
import com.bms.enums.AccountType;
import com.bms.security.CustomUserDetails;
import com.bms.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/accounts",
        produces = "application/json")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping(path = "/create")
    public ResponseEntity<AccountResponseDto> createAccount(
            @Valid @RequestBody AccountRequestDto accountRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        AccountResponseDto account = accountService.createAccount(accountRequestDto, customerId);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Page<AccountResponseDto>> getAllAccounts(
            @RequestParam(required = false) AccountType type,
            @RequestParam(required = false) AccountStatus status,
            @RequestParam(required = false) BigDecimal min,
            @RequestParam(required = false) BigDecimal max,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AccountResponseDto> accounts = accountService.getAllAccounts(type, status, min, max, pageable);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping(path = "/me")
    public ResponseEntity<List<AccountResponseDto>> getMyAccounts(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long customerId = customUserDetails.getUser().getId();
        List<AccountResponseDto> accounts = accountService.getAccountsByCustomerId(customerId);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping(path = "/id/{accountId}")
    public ResponseEntity<AccountResponseDto> getAccountById(
            @PathVariable Long accountId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        AccountResponseDto account = accountService.getAccount(accountId, customerId);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PatchMapping(path = "/id/{accountId}/close")
    public ResponseEntity<AccountResponseDto> closeAccount(
            @PathVariable Long accountId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        AccountResponseDto account = accountService.closeAccount(accountId, customerId);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }
}