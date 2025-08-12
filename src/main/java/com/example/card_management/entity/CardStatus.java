package com.example.card_management.entity;

import lombok.Getter;

@Getter
public enum CardStatus {
    ACTIVE(true),
    BLOCKED(false),
    EXPIRED(false);

    private final boolean active;
    CardStatus(boolean active) {
        this.active = active;
    }
}
