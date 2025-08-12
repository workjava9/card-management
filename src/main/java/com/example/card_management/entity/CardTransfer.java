package com.example.card_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "card_transfers")
public class CardTransfer {

    public enum Status { SUCCESS, FAILED }

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "from_card_id")
    private CardEntity fromCard;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "to_card_id")
    private CardEntity toCard;

    @Setter
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Status status = Status.SUCCESS;

    @Setter
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

}

