package com.example.card_management.dto;

import com.example.card_management.entity.CardStatus;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {

    private Long id;
    private String maskedNumber;
    private int expMonth;
    private int expYear;
    private CardStatus status;
    private BigDecimal balance;
    private Long ownerId;
}
