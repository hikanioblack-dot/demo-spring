package com.example.autoservice.model;

import java.time.LocalDateTime;

public class Assignment {
    private Long id;
    private Long orderId;
    private Long mechanicId;
    private LocalDateTime assignedAt;
    private boolean completed;

    public Assignment() {
        this.assignedAt = LocalDateTime.now();
        this.completed = false;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getMechanicId() { return mechanicId; }
    public void setMechanicId(Long mechanicId) { this.mechanicId = mechanicId; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}