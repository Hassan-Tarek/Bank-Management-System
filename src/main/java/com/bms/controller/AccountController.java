package com.bms.controller;

import com.bms.dto.AccountDto;
import com.bms.enums.AccountStatus;
import com.bms.enums.AccountType;
import com.bms.security.CustomUserDetails;
import com.bms.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountDto>> getAccountsByFilters(
            @RequestParam(required = false) AccountType accountType,
            @RequestParam(required = false) AccountStatus accountStatus,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance) {
        List<AccountDto> accounts = accountService.getAccountsByFilters(accountType, accountStatus, minBalance, maxBalance);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping(path = "/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountDto>> getAccountsByUserId(@PathVariable Long userId) {
        List<AccountDto> accounts = accountService.getAccountsByUserId(userId);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping(path = "/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AccountDto>> getMyAccounts(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getId();
        List<AccountDto> accounts = accountService.getAccountsByUserId(userId);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping(path = "/{accountNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AccountDto> getAccount(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long accountNumber) {
        Long userId = customUserDetails.getUser().getId();
        AccountDto account = accountService.getAccountByAccountNumber(userId, accountNumber);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PostMapping(path = "/create")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AccountDto> createAccount(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid AccountDto accountDto) {
        Long userId = customUserDetails.getUser().getId();
        AccountDto account = accountService.createAccountForUser(userId, accountDto);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{accountNumber}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountDto> activateAccount(@PathVariable Long accountNumber) {
        AccountDto account = accountService.activateAccount(accountNumber);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PatchMapping(path = "/{accountNumber}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountDto> deactivateAccount(@PathVariable Long accountNumber) {
        AccountDto account = accountService.deactivateAccount(accountNumber);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PatchMapping(path = "/{accountNumber}/close")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AccountDto> closeAccount(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long accountNumber) {
        Long userId = customUserDetails.getUser().getId();
        AccountDto closedAccount = accountService.closeAccount(userId, accountNumber);
        return new ResponseEntity<>(closedAccount, HttpStatus.OK);
    }
}
