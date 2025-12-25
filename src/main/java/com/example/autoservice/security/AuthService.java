package com.example.autoservice.security;

import com.example.autoservice.dto.JwtResponse;
import com.example.autoservice.model.*;
import com.example.autoservice.repository.UserRepository;
import com.example.autoservice.repository.UserSessionRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, UserSessionRepository sessionRepository, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public JwtResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return createSession(user);
    }

    @Transactional
    public JwtResponse refresh(String refreshToken) {
        UserSession session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        // ПРОВЕРКА REUSE DETECTION (Повторное использование)
        if (session.getStatus() != SessionStatus.ACTIVE) {
            session.setStatus(SessionStatus.REVOKED); // Отзываем всё
            sessionRepository.save(session);
            throw new RuntimeException("Token was already used! Potential attack detected.");
        }

        if (session.getRefreshTokenExpiry().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        // Помечаем как использованный
        session.setStatus(SessionStatus.USED);
        sessionRepository.save(session);

        User user = userRepository.findByUsername(session.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return createSession(user);
    }

    private JwtResponse createSession(User user) {
        List<String> roles = user.getRoles().stream().map(Enum::name).toList();
        String accessToken = tokenProvider.generateAccessToken(user.getUsername(), roles);
        String refreshToken = tokenProvider.generateRefreshToken(user.getUsername());

        UserSession session = UserSession.builder()
                .username(user.getUsername())
                .refreshToken(refreshToken)
                .refreshTokenExpiry(Instant.now().plus(7, java.time.temporal.ChronoUnit.DAYS))
                .status(SessionStatus.ACTIVE)
                .build();

        sessionRepository.save(session);
        return new JwtResponse(accessToken, refreshToken);
    }
}