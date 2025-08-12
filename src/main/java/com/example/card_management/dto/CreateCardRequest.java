package com.example.card_management.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CreateCardRequest {
    @NotNull @Positive
    private Long ownerId;

    @NotBlank
    @Pattern(regexp="\\d{16}", message="card number must be 16 digits")
    private String number;

    @Min(1) @Max(12)
    private int expMonth;

    @Min(2024)
    private int expYear;

    @NotNull @PositiveOrZero
    private BigDecimal initialBalance;

}
