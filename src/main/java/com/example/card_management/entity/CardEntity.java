package com.example.card_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "cards")
public class CardEntity {

    @Getter
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "number_enc", nullable = false, length = 512)
    private String numberEnc;

    @Getter
    @Setter
    @Column(name = "masked_number", nullable = false, length = 32)
    private String maskedNumber;

    @Getter
    @Setter
    @Column(name = "last4", nullable = false, length = 4)
    private String last4;

    @Getter
    @Setter
    @Convert(converter = YearMonthAttributeConverter.class)
    @Column(name = "expiry", nullable = false, length = 7)
    private YearMonth expiry;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private CardStatus status;

    @Getter
    @Setter
    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

}

