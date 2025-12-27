package com.example.autoservice.security;

import com.example.autoservice.dto.JwtResponse;
import com.example.autoservice.model.User;
import com.example.autoservice.model.UserSession; // Правильный импорт
import com.example.autoservice.model.SessionStatus; // Правильный импорт
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

    public AuthService(UserRepository userRepository, UserSessionRepository sessionRepository,
                       JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public JwtResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return createSession(user);
    }

    @Transactional
    public JwtResponse refresh(String refreshToken) {
        UserSession session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (session.getStatus() != SessionStatus.ACTIVE) {
            session.setStatus(SessionStatus.REVOKED);
            sessionRepository.save(session);
            throw new RuntimeException("Token already used!");
        }

        if (session.getRefreshTokenExpiry().isBefore(Instant.now())) {
            throw new RuntimeException("Token expired");
        }

        session.setStatus(SessionStatus.USED);
        sessionRepository.save(session);

        User user = userRepository.findByUsername(session.getUsername()).orElseThrow();
        return createSession(user);
    }

    private JwtResponse createSession(User user) {
        List<String> roles = user.getRoles().stream().map(Enum::name).toList();
        String access = tokenProvider.generateAccessToken(user.getUsername(), roles);
        String refresh = tokenProvider.generateRefreshToken(user.getUsername());

        // Создание через конструктор
        UserSession session = new UserSession(
                user.getUsername(),
                refresh,
                Instant.now().plus(7, ChronoUnit.DAYS),
                SessionStatus.ACTIVE
        );

        sessionRepository.save(session);
        return new JwtResponse(access, refresh);
    }
    public List<UserSession> getAllSessions() {
        return sessionRepository.findAll();
    }
}