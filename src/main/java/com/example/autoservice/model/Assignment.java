package com.example.autoservice.model;

import jakarta.persistence.*;

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

    private boolean completed = false;

    public Assignment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Mechanic getMechanic() { return mechanic; }
    public void setMechanic(Mechanic mechanic) { this.mechanic = mechanic; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    // Хелперы для Postman (исправляют 500 ошибку)
    public void setOrderId(Long id) {
        if (this.order == null) this.order = new Order();
        this.order.setId(id);
    }
    public void setMechanicId(Long id) {
        if (this.mechanic == null) this.mechanic = new Mechanic();
        this.mechanic.setId(id);
    }
}