package com.example.autoservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "mechanic_id", nullable = false)
    private Mechanic mechanic;

    private LocalDateTime assignedAt;
    private boolean completed;

    public Assignment() {
        this.assignedAt = LocalDateTime.now();
        this.completed = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Для Postman
    public Long getOrderId() {
        return order != null ? order.getId() : null;
    }
    public void setOrderId(Long orderId) {
        if (orderId != null) {
            this.order = new Order();
            this.order.setId(orderId);
        }
    }

    public Long getMechanicId() {
        return mechanic != null ? mechanic.getId() : null;
    }
    public void setMechanicId(Long mechanicId) {
        if (mechanicId != null) {
            this.mechanic = new Mechanic();
            this.mechanic.setId(mechanicId);
        }
    }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}