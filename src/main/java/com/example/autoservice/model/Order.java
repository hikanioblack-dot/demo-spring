package com.example.autoservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public Order() {
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры (основные)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    // Для Postman
    public Long getCarId() {
        return car != null ? car.getId() : null;
    }
    public void setCarId(Long carId) {
        if (carId != null) {
            this.car = new Car();
            this.car.setId(carId);
        }
    }

    public Long getClientId() {
        return client != null ? client.getId() : null;
    }
    public void setClientId(Long clientId) {
        if (clientId != null) {
            this.client = new Client();
            this.client.setId(clientId);
        }
    }
}