package com.example.card_management.service;

import com.example.card_management.entity.CardEntity;
import com.example.card_management.entity.CardStatus;
import com.example.card_management.exception.UserNotFoundException;
import com.example.card_management.repository.CardRepository;
import com.example.card_management.repository.UserRepository;
import com.example.card_management.util.CardMasker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Business logic for cards: creation, masking, filtering and status changes.
 */
@Service
public class CardService {

    private final CardRepository cardRepo;
    private final UserRepository userRepo;
    private final CryptoService crypto;
    private final CardMasker cardMasker;

    public CardService(CardRepository cardRepo, UserRepository userRepo, CryptoService crypto, CardMasker cardMasker) {
        this.cardRepo = cardRepo;
        this.userRepo = userRepo;
        this.crypto = crypto;
        this.cardMasker = cardMasker;
    }

    @Transactional
    public CardEntity adminCreate(Long ownerId, String plainNumber, YearMonth expiry, java.math.BigDecimal initialBalance) {
        var owner = userRepo.findById(ownerId).orElseThrow(() -> new UserNotFoundException("Owner not found"));
        var last4 = plainNumber.substring(plainNumber.length() - 4);
        var enc = crypto.encryptToB64(plainNumber);

        var card = new CardEntity();
        card.setOwner(owner);
        card.setNumberEnc(enc);
        card.setLast4(last4);
        card.setMaskedNumber(cardMasker.mask(last4));
        card.setExpiry(expiry);
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(initialBalance == null ? BigDecimal.ZERO : initialBalance);

        return cardRepo.save(card);
    }

    @Transactional(readOnly = true)
    public Page<CardEntity> myCards(Long meId, CardStatus status, String last4, Pageable pageable) {
        return cardRepo.findAllByOwnerFilter(meId, status, last4, pageable);
    }

    @Transactional
    public void adminSetStatus(Long cardId, CardStatus status) {
        var card = cardRepo.findById(cardId).orElseThrow();
        card.setStatus(status);
        cardRepo.save(card);
    }

    @Transactional
    public void adminDelete(Long cardId) {
        cardRepo.deleteById(cardId);
    }
}
