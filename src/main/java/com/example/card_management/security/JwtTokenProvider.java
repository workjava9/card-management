package com.example.card_management.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtTokenProvider {

    private static final String ROLES_CLAIM = "roles";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long CLOCK_SKEW_SECONDS = 30;

    private final SecretKey secretKey;
    private final long validityMs;

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms:3600000}") long validityMs
    ) {
        this.secretKey = buildKey(secret);
        this.validityMs = validityMs > 0 ? validityMs : 3600000L;
    }

    public String createToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(validityMs);

        String roles = (authorities == null) ? "" :
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(username)
                .claim(ROLES_CLAIM, roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public List<SimpleGrantedAuthority> getAuthorities(String token) {
        String roles = parseClaims(token).get(ROLES_CLAIM, String.class);
        if (roles == null || roles.isBlank()) return List.of();
        return Stream.of(roles.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Claims parseClaims(String maybeBearerToken) {
        String token = normalize(maybeBearerToken);
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .setAllowedClockSkewSeconds(CLOCK_SKEW_SECONDS)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private static String normalize(String token) {
        if (token == null) throw new IllegalArgumentException("JWT token is null");
        String trimmed = token.trim();
        if (trimmed.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return trimmed.substring(BEARER_PREFIX.length()).trim();
        }
        return trimmed;
    }

    private static SecretKey buildKey(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("security.jwt.secret must not be empty");
        }

        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(secret);
            if (keyBytes.length < 32) {
                keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            }
        } catch (IllegalArgumentException ignored) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        try {
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (WeakKeyException e) {
            throw new IllegalArgumentException(
                    "security.jwt.secret is too short. Provide at least 32 bytes (HS256).", e
            );
        }
    }
}
