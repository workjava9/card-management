package com.example.card_management.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MaskerTest {
    @Test
    void mask_last4(){
        assertThat(CardMasker.mask("1234")).isEqualTo("**** **** **** 1234");
    }
}
