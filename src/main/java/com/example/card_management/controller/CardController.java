package com.example.card_management.controller;

import com.example.card_management.dto.CardDto;
import com.example.card_management.dto.CreateCardRequest;
import com.example.card_management.entity.CardStatus;
import com.example.card_management.service.CardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.YearMonth;

/**
 * REST endpoints for managing cards (create, list, status, delete).
 */
@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/admin/{ownerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CardDto createForOwner(@PathVariable Long ownerId,
                                  @Valid @RequestBody CreateCardRequest req) {
        var expiry = YearMonth.of(req.getExpYear(), req.getExpMonth());
        var entity = cardService.adminCreate(ownerId, req.getNumber(), expiry, req.getInitialBalance());
        return CardDto.from(entity);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<CardDto> myCards(@AuthenticationPrincipal(expression = "id") Long meId,
                                 @RequestParam(required = false) CardStatus status,
                                 @RequestParam(required = false) String last4,
                                 Pageable pageable) {
        return cardService.myCards(meId, status, last4, pageable).map(CardDto::from);
    }

    @PatchMapping("/admin/{cardId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public void setStatus(@PathVariable Long cardId, @RequestParam CardStatus status) {
        cardService.adminSetStatus(cardId, status);
    }

    @DeleteMapping("/admin/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long cardId) {
        cardService.adminDelete(cardId);
    }
}
