package com.example.card_management.dto;

import java.math.BigDecimal;

public record TransferRequest(
        Long fromId,
        Long toId,
        BigDecimal amount
) {}
