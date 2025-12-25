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

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<String> registerClient(@RequestBody RegistrationRequest reg) {
        String pass = reg.getPassword();

        // 1. Валидация пароля
        if (pass.length() < 8) {
            return ResponseEntity.badRequest().body("Пароль короче 8 символов");
        }
        if (!pass.matches(".*[0-9].*")) {
            return ResponseEntity.badRequest().body("Нужна хотя бы одна цифра");
        }
        // Изменили текст здесь для строчных букв
        if (!pass.matches(".*[a-z].*")) {
            return ResponseEntity.badRequest().body("Нужна буква другого регистра");
        }
        // И здесь для заглавных букв
        if (!pass.matches(".*[A-Z].*")) {
            return ResponseEntity.badRequest().body("Нужна буква другого регистра");
        }
        if (!pass.matches(".*[!@#$%^&*()].*")) {
            return ResponseEntity.badRequest().body("Нужен спецсимвол !@#$%^&*()");
        }

        // 2. Проверка уникальности
        if (userRepo.findByUsername(reg.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Этот логин уже занят");
        }

        // 3. Создаем бизнес-профиль клиента
        Client client = new Client();
        client.setName(reg.getUsername());
        client.setEmail(reg.getEmail());
        client.setPhone(reg.getPhone());
        client = clientRepo.save(client);

        // 4. Создаем системного пользователя
        User user = new User();
        user.setUsername(reg.getUsername());
        user.setPassword(encoder.encode(pass));
        user.setRoles(Set.of(Role.ROLE_CLIENT));
        user.setClient(client);
        userRepo.save(user);

        return ResponseEntity.ok("Регистрация успешна! Ваш Client ID: " + client.getId());
    }

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