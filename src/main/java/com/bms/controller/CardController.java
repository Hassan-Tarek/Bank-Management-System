package com.bms.controller;

import com.bms.dto.request.CardRequestDto;
import com.bms.dto.response.CardResponseDto;
import com.bms.enums.CardStatus;
import com.bms.security.CustomUserDetails;
import com.bms.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/cards",
        produces = "application/json")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping(path = "/create")
    public ResponseEntity<CardResponseDto> createCard(
            @Valid @RequestBody CardRequestDto cardRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        CardResponseDto card = cardService.createCard(cardRequestDto, customerId);
        return ResponseEntity.ok(card);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Page<CardResponseDto>> getAllCards(
            @RequestParam(required = false) CardStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CardResponseDto> cards = cardService.getAllCards(status, pageable);
        return ResponseEntity.ok(cards);
    }

    @GetMapping(path = "/me")
    public ResponseEntity<List<CardResponseDto>> getMyCards(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        List<CardResponseDto> cards = cardService.getCardsByCustomerId(customerId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping(path = "/id/{cardId}")
    public ResponseEntity<CardResponseDto> getCardById(
            @PathVariable Long cardId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        CardResponseDto card = cardService.getCardById(customerId, cardId);
        return ResponseEntity.ok(card);
    }

    @PatchMapping(path = "/id/{cardId}/block")
    public ResponseEntity<CardResponseDto> blockCard(
            @PathVariable Long cardId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        CardResponseDto card = cardService.blockCard(customerId, cardId);
        return ResponseEntity.ok(card);
    }
}
