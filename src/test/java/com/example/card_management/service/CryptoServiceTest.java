package com.example.card_management.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class CryptoServiceTest {
    @Autowired
    CryptoService crypto;

    @Test
    void roundtrip() {
        String enc = crypto.encryptToB64("4111111111111111");
        assertThat(enc).isNotBlank();
        assertThat(crypto.decryptFromB64(enc)).isEqualTo("4111111111111111");
    }
}
