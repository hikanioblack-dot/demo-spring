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

    // Эндпоинт для проверки в браузере (Задание 6)
    @GetMapping("/verify")
    public String verifySsl() {
        return "<html><body style='font-family: sans-serif; text-align: center; padding-top: 100px;'>" +
                "<h1 style='color: #2c3e50;'>🛡️ HTTPS Соединение Установлено</h1>" +
                "<div style='border: 2px solid #34495e; display: inline-block; padding: 20px; border-radius: 15px;'>" +
                "<p><b>Идентификатор студента:</b> 1БИБ23398</p>" +
                "<p><b>Статус:</b> Цепочка сертификатов (Root -> Inter -> Server) проверена браузером.</p>" +
                "</div></body></html>";
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<String> registerClient(@RequestBody RegistrationRequest reg) {
        String pass = reg.getPassword();
        if (pass.length() < 8) return ResponseEntity.badRequest().body("Пароль короче 8 символов");
        if (!pass.matches(".*[0-9].*")) return ResponseEntity.badRequest().body("Нужна хотя бы одна цифра");
        if (!pass.matches(".*[a-z].*") || !pass.matches(".*[A-Z].*")) return ResponseEntity.badRequest().body("Нужна буква другого регистра");
        if (!pass.matches(".*[!@#$%^&*()].*")) return ResponseEntity.badRequest().body("Нужен спецсимвол !@#$%^&*()");

        if (userRepo.findByUsername(reg.getUsername()).isPresent()) return ResponseEntity.badRequest().body("Логин занят");

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
        try {
            return ResponseEntity.ok(authService.login(credentials.get("username"), credentials.get("password")));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(authService.refresh(body.get("refreshToken")));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}