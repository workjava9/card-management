package com.example.card_management.dto;

import com.example.card_management.entity.CardEntity;
import com.example.card_management.entity.CardStatus;
import java.math.BigDecimal;

public record CardDto(
        Long id,
        String maskedNumber,
        String last4,
        String expiry,
        CardStatus status,
        BigDecimal balance
) {
    public static CardDto from(CardEntity c) {
        return new CardDto(
                c.getId(),
                c.getMaskedNumber(),
                c.getLast4(),
                c.getExpiry() == null ? null : c.getExpiry().toString(),
                c.getStatus(),
                c.getBalance()
        );
    }
}
