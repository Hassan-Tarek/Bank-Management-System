package com.bms.mapper;

import com.bms.dto.response.CardResponseDto;
import com.bms.entity.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardResponseDto toDto(Card card) {
        return CardResponseDto.builder()
                .id(card.getId())
                .number(card.getNumber())
                .cvv(card.getCvv())
                .expiryDate(card.getExpiryDate())
                .status(card.getStatus())
                .accountId(card.getAccount() != null ? card.getAccount().getId() : null)
                .accountType(card.getAccount() != null ? card.getAccount().getType().toString() : null)
                .build();
    }
}
