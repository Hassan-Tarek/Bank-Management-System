package com.bms.mapper;

import com.bms.dto.LoanDto;
import com.bms.entity.Loan;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public LoanMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Loan toEntity(LoanDto loanDto) {
        return modelMapper.map(loanDto, Loan.class);
    }

    public LoanDto toDto(Loan loan) {
        return modelMapper.map(loan, LoanDto.class);
    }
}
