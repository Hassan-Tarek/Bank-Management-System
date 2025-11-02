package com.bms.mapper;

import com.bms.dto.AccountDto;
import com.bms.entity.Account;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public AccountMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Account toEntity(AccountDto accountDto) {
        if (accountDto == null) return null;
        return modelMapper.map(accountDto, Account.class);
    }

    public AccountDto toDto(Account account) {
        if (account == null) return null;
        return modelMapper.map(account, AccountDto.class);
    }
}
