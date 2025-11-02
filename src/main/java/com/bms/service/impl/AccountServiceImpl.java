package com.bms.service.impl;

import com.bms.dto.AccountDto;
import com.bms.entity.Account;
import com.bms.entity.User;
import com.bms.enums.AccountStatus;
import com.bms.enums.AccountType;
import com.bms.enums.Role;
import com.bms.exception.ResourceNotFoundException;
import com.bms.exception.UnauthorizedException;
import com.bms.mapper.AccountMapper;
import com.bms.repository.AccountRepository;
import com.bms.repository.UserRepository;
import com.bms.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountDto createAccountForUser(Long userId, AccountDto accountDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + accountDto.getUserId()));

        Long uniqueAccountNumber = Long.parseLong(System.currentTimeMillis() + "" +
                (int) (Math.random() * 1000));
        Account account = Account.builder()
                .accountNumber(uniqueAccountNumber)
                .balance(BigDecimal.ZERO)
                .accountType(AccountType.valueOf(accountDto.getAccountType()))
                .accountStatus(AccountStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAccountsByFilters(AccountType accountType, AccountStatus accountStatus,
                                                 BigDecimal minBalance, BigDecimal maxBalance) {
        return accountRepository.findByFilters(accountType, accountStatus, minBalance, maxBalance)
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Override
    public List<AccountDto> getAccountsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
        return accountRepository
                .findByUser(user)
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Override
    public AccountDto getAccountByAccountNumber(Long userId, Long accountNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with account number: " + accountNumber));

        if (!user.getRole().equals(Role.ADMIN)
                && !account.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only view your own accounts");
        }
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public AccountDto activateAccount(Long accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with accountNumber: " + accountNumber));

        account.setAccountStatus(AccountStatus.ACTIVE);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional
    public AccountDto deactivateAccount(Long accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with accountNumber: " + accountNumber));

        account.setAccountStatus(AccountStatus.INACTIVE);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional
    public AccountDto closeAccount(Long userId, Long accountNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with account number: " + accountNumber));

        if (!user.getRole().equals(Role.ADMIN) &&
                !account.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only close your own accounts");
        }

        account.setAccountStatus(AccountStatus.CLOSED);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }
}
