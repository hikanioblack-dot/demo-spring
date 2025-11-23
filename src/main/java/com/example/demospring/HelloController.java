package com.example.demospring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from Spring Boot!";
    }

    @GetMapping("/api/info")
    public String getInfo() {
        return "This is a demo Spring Boot app";
    }
}