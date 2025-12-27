package com.example.autoservice.controller;

import com.example.autoservice.dto.JwtResponse;
import com.example.autoservice.dto.RegistrationRequest;
import com.example.autoservice.model.*;
import com.example.autoservice.repository.*;
import com.example.autoservice.security.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepo;
    private final ClientRepository clientRepo;
    private final PasswordEncoder encoder;
    private final AuthService authService;

    public AuthController(UserRepository userRepo, ClientRepository clientRepo,
                          PasswordEncoder encoder, AuthService authService) {
        this.userRepo = userRepo;
        this.clientRepo = clientRepo;
        this.encoder = encoder;
        this.authService = authService;
    }

    // 🔥 НОВЫЙ МЕТОД ДЛЯ ПРЕПОДАВАТЕЛЯ: Просмотр всех сессий через GET
    @GetMapping("/sessions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserSession> getAllSessions() {
        return authService.getAllSessions();
    }

    @GetMapping("/csrf")
    public String getCsrfToken() {
        return "CSRF токен выдан в Cookies (XSRF-TOKEN).";
    }

    @GetMapping("/verify")
    public String verify() {
        return "<html><body><h1>HTTPS & CSRF Protected</h1><p>ID: 1БИБ23398</p></body></html>";
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<String> registerClient(@RequestBody RegistrationRequest reg) {
        String pass = reg.getPassword();
        if (pass.length() < 8 || !pass.matches(".*[0-9].*") || !pass.matches(".*[a-z].*") || !pass.matches(".*[A-Z].*") || !pass.matches(".*[!@#$%^&*()].*")) {
            return ResponseEntity.badRequest().body("Пароль слишком слабый.");
        }
        Client client = new Client();
        client.setName(reg.getUsername());
        client.setEmail(reg.getEmail());
        client.setPhone(reg.getPhone());
        client = clientRepo.save(client);

        User user = new User();
        user.setUsername(reg.getUsername());
        user.setPassword(encoder.encode(pass));
        user.setRoles(Set.of(Role.ROLE_CLIENT));
        user.setClient(client);
        userRepo.save(user);

        return ResponseEntity.ok("Регистрация успешна! Ваш Client ID: " + client.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        return ResponseEntity.ok(authService.login(credentials.get("username"), credentials.get("password")));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.refresh(body.get("refreshToken")));
    }
}