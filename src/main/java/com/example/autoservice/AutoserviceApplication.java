package com.example.autoservice;

import com.example.autoservice.model.Role;
import com.example.autoservice.model.User;
import com.example.autoservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

@SpringBootApplication
public class AutoserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoserviceApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            if (userRepo.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("Admin123!"));
                admin.setRoles(Set.of(Role.ROLE_ADMIN));
                userRepo.save(admin);
                System.out.println(">>> СИСТЕМА: Создан начальный админ: admin / Admin123!");
            }
        };
    }
}