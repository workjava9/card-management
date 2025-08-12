package com.example.card_management.util;

import org.springframework.stereotype.Component;

@Component
public class CardMasker {

    public static String mask(String plainNumber) {
        if (plainNumber == null || plainNumber.length() < 4) return "****";
        String last4 = last4(plainNumber);
        return "**** **** **** " + last4;
    }

    public static String last4(String plainNumber) {
        if (plainNumber == null || plainNumber.length() < 4) return plainNumber == null ? "" : plainNumber;
        return plainNumber.substring(plainNumber.length() - 4);
    }
}

