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

    // 1. Регистрация клиента (из Задания 4)
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<String> registerClient(@RequestBody RegistrationRequest reg) {
        String pass = reg.getPassword();

        // Валидация пароля
        if (pass.length() < 8) return ResponseEntity.badRequest().body("Пароль короче 8 символов");
        if (!pass.matches(".*[0-9].*")) return ResponseEntity.badRequest().body("Нужна хотя бы одна цифра");
        if (!pass.matches(".*[a-z].*") || !pass.matches(".*[A-Z].*"))
            return ResponseEntity.badRequest().body("Нужна буква другого регистра");
        if (!pass.matches(".*[!@#$%^&*()].*")) return ResponseEntity.badRequest().body("Нужен спецсимвол !@#$%^&*()");

        if (userRepo.findByUsername(reg.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Этот логин уже занят");
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

    // 2. Вход (Логин) -> Выдача пары Access/Refresh
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            JwtResponse response = authService.login(username, password);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Ошибка входа: " + e.getMessage());
        }
    }

    // 3. Обновление токена (Refresh)
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        try {
            String refreshToken = body.get("refreshToken");
            JwtResponse response = authService.refresh(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Ошибка обновления: " + e.getMessage());
        }
    }

    // 4. Создание персонала (только ADMIN)
    @PostMapping("/create-staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createStaff(@RequestBody User userRequest, @RequestParam Role role) {
        if (role == Role.ROLE_CLIENT) return ResponseEntity.badRequest().body("Используйте /register");

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(encoder.encode(userRequest.getPassword()));
        user.setRoles(Set.of(role));

        userRepo.save(user);
        return ResponseEntity.ok("Сотрудник создан: " + role);
    }
}