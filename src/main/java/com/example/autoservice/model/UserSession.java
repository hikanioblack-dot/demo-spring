package com.example.autoservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_sessions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;

    @Column(length = 512)
    private String refreshToken;

    private Instant refreshTokenExpiry;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;
}