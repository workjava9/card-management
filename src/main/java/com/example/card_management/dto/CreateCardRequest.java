package com.example.card_management.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for creating a new card.
 * Contains required info about owner, card number, expiry date and initial balance.
 */
@Setter
@Getter
public class CreateCardRequest {

    /**
     * ID of the card owner (user).
     */
    @NotNull @Positive
    private Long ownerId;

    /**
     * Card number (PAN), must be exactly 16 digits.
     */
    @NotBlank
    @Pattern(regexp="\\d{16}", message="card number must be 16 digits")
    private String number;

    /**
     * Expiry month, range 1-12.
     */
    @Min(1) @Max(12)
    private int expMonth;

    /**
     * Expiry year, must be >= 2024.
     */

    @Min(2024)
    private int expYear;

    /**
     * Initial balance, must be non-negative.
     */
    @NotNull @PositiveOrZero
    private BigDecimal initialBalance;

}
