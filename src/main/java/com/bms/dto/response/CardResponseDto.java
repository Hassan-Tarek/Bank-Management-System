package com.bms.dto.response;

import com.bms.enums.CardStatus;
import lombok.Builder;

import java.util.Date;

@Builder
public record CardResponseDto(
        Long id,
        String number,
        String cvv,
        Date expiryDate,
        CardStatus status,
        Long accountId,
        String accountType
) { }
