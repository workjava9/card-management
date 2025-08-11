package com.example.card_management.service;

import com.example.card_management.dto.AuthRequest;
import com.example.card_management.dto.AuthResponse;
import com.example.card_management.entity.UserEntity;
import com.example.card_management.exception.InvalidCredentialsException;
import com.example.card_management.exception.UserNotFoundException;
import com.example.card_management.repository.UserRepository;
import com.example.card_management.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse login(AuthRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String role = user.getRole().name();

        String token = jwtTokenProvider.createToken(
                user.getUsername(),
                List.of(new SimpleGrantedAuthority(role))
        );

        return new AuthResponse(token);
    }
}
