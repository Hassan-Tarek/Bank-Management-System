package com.bms.service;

import com.bms.dto.request.CardRequestDto;
import com.bms.dto.response.CardResponseDto;
import com.bms.entity.Account;
import com.bms.entity.Card;
import com.bms.enums.CardStatus;
import com.bms.exception.BadRequestException;
import com.bms.exception.ResourceNotFoundException;
import com.bms.exception.UnauthorizedException;
import com.bms.mapper.CardMapper;
import com.bms.repository.AccountRepository;
import com.bms.repository.CardRepository;
import com.bms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    @Transactional
    public CardResponseDto createCard(CardRequestDto cardRequestDto, Long customerId) {
        Account account = accountRepository.findById(cardRequestDto.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("Account does not belong to you");
        }

        if (cardRepository.existsByAccountId(account.getId())) {
            throw new BadRequestException("Account already has a card");
        }

        Card card = Card.builder()
                .number(generateUniqueCardNumber())
                .cvv(generateCvv())
                .expiryDate(generateExpiryDate())
                .status(CardStatus.ACTIVE)
                .account(account)
                .build();

        Card savedCard = cardRepository.save(card);
        return cardMapper.toDto(savedCard);
    }

    @Transactional(readOnly = true)
    public Page<CardResponseDto> getAllCards(CardStatus status, Pageable pageable) {
        return cardRepository.findAllCards(status, pageable)
                .map(cardMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getCardsByCustomerId(Long customerId) {
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found");
        }

        return cardRepository.findByAccountCustomerId(customerId)
                .stream()
                .map(cardMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CardResponseDto getCardById(Long cardId, Long customerId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (!card.getAccount().getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("Card does not belong to you");
        }

        return cardMapper.toDto(card);
    }

    @Transactional
    public CardResponseDto blockCard(Long cardId, Long customerId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (!card.getAccount().getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("Card does not belong to you");
        }

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new BadRequestException("Card is already blocked");
        }

        card.setStatus(CardStatus.BLOCKED);
        Card savedCard = cardRepository.save(card);
        return cardMapper.toDto(savedCard);
    }

    private String generateUniqueCardNumber() {
        String cardNumber;
        do {
            cardNumber = generateCardNumber();
        } while (cardRepository.existsByNumber(cardNumber));
        return cardNumber;
    }

    private String generateCardNumber() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateCvv() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int cvv = 100 + random.nextInt(900);
        return String.valueOf(cvv);
    }

    private Date generateExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 5);
        return calendar.getTime();
    }
}
