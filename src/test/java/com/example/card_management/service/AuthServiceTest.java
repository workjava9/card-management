package com.example.card_management.service;

import com.example.card_management.dto.AuthRequest;
import com.example.card_management.dto.AuthResponse;
import com.example.card_management.entity.UserEntity;
import com.example.card_management.exception.InvalidCredentialsException;
import com.example.card_management.exception.UserNotFoundException;
import com.example.card_management.repository.UserRepository;
import com.example.card_management.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;

    @InjectMocks private AuthService authService;

    @Test
    @DisplayName("Успешный логин возвращает токен")
    void login_ok() {
        var user = UserEntity.builder()
                .id(1L).username("john").password("hash").role(UserEntity.Role.USER).build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pwd", "hash")).thenReturn(true);
        when(jwtTokenProvider.createToken(
                eq("john"),
                eq(List.of(new SimpleGrantedAuthority("USER")))
        )).thenReturn("jwt");

        AuthResponse resp = authService.login(new AuthRequest("john", "pwd"));

        assertThat(resp.getToken()).isEqualTo("jwt");
        verify(userRepository).findByUsername("john");
        verify(jwtTokenProvider).createToken(
                eq("john"),
                eq(List.of(new SimpleGrantedAuthority("USER")))
        );
    }

    @Test
    @DisplayName("Бросает UserNotFoundException если пользователя нет")
    void login_userNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> authService.login(new AuthRequest("missing", "pwd")));
    }

    @Test
    @DisplayName("Бросает InvalidCredentialsException при неверном пароле")
    void login_invalidPassword() {
        var user = UserEntity.builder()
                .id(1L).username("john").password("hash").role(UserEntity.Role.USER).build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad", "hash")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new AuthRequest("john", "bad")));
    }
}
