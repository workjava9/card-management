package com.example.card_management.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    @Test
    void generate_and_validate() {
        JwtTokenProvider p = new JwtTokenProvider(
                "test_jwt_secret_key_which_is_long_enough_32_chars",
                3_600_000L
        );

        String token = p.createToken(
                "alice",
                List.of(new SimpleGrantedAuthority("ADMIN"))
        );

        assertThat(p.validate(token)).isTrue();
        assertThat(p.getUsername(token)).isEqualTo("alice");
        assertThat(p.getAuthorities(token))
                .extracting(SimpleGrantedAuthority::getAuthority)
                .containsExactly("ADMIN");
    }
}
