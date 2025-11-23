package com.bms.mapper;

import com.bms.dto.request.AccountRequestDto;
import com.bms.dto.response.AccountResponseDto;
import com.bms.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toEntity(AccountRequestDto accountRequestDto) {
        return Account.builder()
                .type(accountRequestDto.type())
                .build();
    }

    public AccountResponseDto toDto(Account account) {
        return AccountResponseDto.builder()
                .id(account.getId())
                .number(account.getNumber())
                .customerId(account.getCustomer().getId())
                .type(account.getType())
                .balance(account.getBalance())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
