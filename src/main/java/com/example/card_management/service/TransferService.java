package com.example.card_management.service;

import com.example.card_management.entity.CardEntity;
import com.example.card_management.entity.CardStatus;
import com.example.card_management.entity.CardTransfer;
import com.example.card_management.repository.CardRepository;
import com.example.card_management.repository.CardTransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final CardRepository cardRepo;
    private final CardTransferRepository txRepo;

    public TransferService(CardRepository cardRepo, CardTransferRepository txRepo) {
        this.cardRepo = cardRepo;
        this.txRepo = txRepo;
    }

    @Transactional
    public Long transferBetweenMyCards(Long meId, Long fromId, Long toId, BigDecimal amount) {
        var from = cardRepo.findWithLockById(fromId).orElseThrow();
        var to = cardRepo.findWithLockById(toId).orElseThrow();

        ensureBelongsTo(meId, from);
        ensureBelongsTo(meId, to);
        ensureActive(from);
        ensureActive(to);

        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        cardRepo.save(from);
        cardRepo.save(to);

        var tx = new CardTransfer();
        tx.setFromCard(from);
        tx.setToCard(to);
        tx.setAmount(amount);
        tx.setStatus(CardTransfer.Status.SUCCESS);
        return txRepo.save(tx).getId();
    }

    private static void ensureActive(CardEntity c) {
        if (c.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Card not ACTIVE");
        }
    }

    private static void ensureBelongsTo(Long meId, CardEntity c) {
        if (!c.getOwner().getId().equals(meId)) {
            throw new SecurityException("Cards must belong to current user");
        }
    }
}
