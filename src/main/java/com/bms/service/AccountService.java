package com.bms.service;

import com.bms.dto.request.AccountRequestDto;
import com.bms.dto.response.AccountResponseDto;
import com.bms.entity.Account;
import com.bms.entity.User;
import com.bms.enums.AccountStatus;
import com.bms.enums.AccountType;
import com.bms.exception.BadRequestException;
import com.bms.exception.InsufficientFundsException;
import com.bms.exception.ResourceNotFoundException;
import com.bms.exception.UnauthorizedException;
import com.bms.mapper.AccountMapper;
import com.bms.repository.AccountRepository;
import com.bms.repository.LoanRepository;
import com.bms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final AccountMapper accountMapper;

    @Transactional
    public AccountResponseDto createAccount(AccountRequestDto accountRequestDto, Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Account account = accountMapper.toEntity(accountRequestDto);
        account.setNumber(generateAccountNumber());
        account.setCustomer(customer);

        accountRepository.save(account);
        return accountMapper.toDto(account);
    }

    @Transactional(readOnly = true)
    public Page<AccountResponseDto> getAllAccounts(AccountType type, AccountStatus status,
            BigDecimal min, BigDecimal max, Pageable pageable) {
        return accountRepository.findAllAccounts(type, status, min, max, pageable)
                .map(accountMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDto> getAccountsByCustomerId(Long customerId) {
        return accountRepository
                .findByCustomerId(customerId)
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponseDto getAccount(Long accountId, Long customerId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Authorization check
        if (!account.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("You are not authorized to view this account");
        }

        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountResponseDto closeAccount(Long accountId, Long customerId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Authorization check
        if (!account.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("You are not authorized to close this account");
        }

        // Balance must be zero
        BigDecimal balance = account.getBalance();
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("Cannot close account with remaining balance");
        }

        // Ensure that the owner of this account has no disbursed loans
        boolean hasActiveLoans = loanRepository.existsActiveLoanByCustomerId(customerId);
        if (hasActiveLoans) {
            throw new BadRequestException("Cannot close account with active loans");
        }

        account.setStatus(AccountStatus.CLOSED);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Transactional
    public void debit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new BadRequestException("Account is not active");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    @Transactional
    public void credit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new BadRequestException("Account is not active");
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    private String generateAccountNumber() {
        String accountNumber;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        do {
            accountNumber = "ACC-" + (100_000_000L + random.nextInt(900_000_000));
        } while (accountRepository.existsByNumber(accountNumber));
        return accountNumber;
    }
}
