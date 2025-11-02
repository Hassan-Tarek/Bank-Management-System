package com.bms.service;

import com.bms.dto.AccountDto;
import com.bms.enums.AccountStatus;
import com.bms.enums.AccountType;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    AccountDto createAccountForUser(Long userId, AccountDto accountDto);

    List<AccountDto> getAccountsByFilters(AccountType accountType, AccountStatus accountStatus,
                                          BigDecimal minBalance, BigDecimal maxBalance);

    List<AccountDto> getAccountsByUserId(Long userId);

    AccountDto getAccountByAccountNumber(Long userId, Long accountNumber);

    AccountDto activateAccount(Long accountNumber);

    AccountDto deactivateAccount(Long accountNumber);

    AccountDto closeAccount(Long userId, Long accountNumber);
}
