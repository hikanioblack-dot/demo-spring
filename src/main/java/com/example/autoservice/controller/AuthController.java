package com.example.autoservice.controller;

import com.example.autoservice.dto.RegistrationRequest;
import com.example.autoservice.model.*;
import com.example.autoservice.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepo;
    private final ClientRepository clientRepo;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository userRepo, ClientRepository clientRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.clientRepo = clientRepo;
        this.encoder = encoder;
    }

    // 1. Метод для получения CSRF токена (для демонстрации)
    @GetMapping("/csrf")
    public String getCsrfToken() {
        return "CSRF токен выдан в Cookies (XSRF-TOKEN).";
    }

    // 2. Регистрация обычного КЛИЕНТА (публичная)
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<String> registerClient(@RequestBody RegistrationRequest reg) {
        String pass = reg.getPassword();

        // Валидация пароля (Задание 4)
        if (pass.length() < 8 || !pass.matches(".*[0-9].*") || !pass.matches(".*[a-z].*") ||
                !pass.matches(".*[A-Z].*") || !pass.matches(".*[!@#$%^&*()].*")) {
            return ResponseEntity.badRequest().body("Пароль слишком слабый.");
        }

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

    // 3. Создание ПЕРСОНАЛА (только для ADMIN) - ЭТОГО МЕТОДА НЕ ХВАТАЛО
    @PostMapping("/create-staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createStaff(@RequestBody RegistrationRequest reg, @RequestParam Role role) {
        if (role == Role.ROLE_CLIENT) {
            return ResponseEntity.badRequest().body("Используйте /register для клиентов");
        }

        if (userRepo.findByUsername(reg.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Пользователь уже существует");
        }

        User user = new User();
        user.setUsername(reg.getUsername());
        user.setPassword(encoder.encode(reg.getPassword()));
        user.setRoles(Set.of(role));

        userRepo.save(user);
        return ResponseEntity.ok("Сотрудник создан с ролью: " + role);
    }
}