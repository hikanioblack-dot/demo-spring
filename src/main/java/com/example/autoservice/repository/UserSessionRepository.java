package com.example.autoservice.repository;

import com.example.autoservice.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    Optional<UserSession> findByRefreshToken(String refreshToken);
    void deleteByUsername(String username); // Для логаута или при компрометации
}