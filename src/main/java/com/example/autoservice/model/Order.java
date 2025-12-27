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
    private LocalDateTime createdAt = LocalDateTime.now();

    // Поле, которого не хватало для BusinessController
    private LocalDateTime completedAt;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public Order() {}

    // Стандартные геттеры и сеттеры
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

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    // --- Вспомогательные методы для BusinessController (исправляют ошибки компиляции) ---

    public Long getCarId() {
        return (car != null) ? car.getId() : null;
    }

    public void setCarId(Long id) {
        if (this.car == null) this.car = new Car();
        this.car.setId(id);
    }

    public Long getClientId() {
        return (client != null) ? client.getId() : null;
    }

    public void setClientId(Long id) {
        if (this.client == null) this.client = new Client();
        this.client.setId(id);
    }
}