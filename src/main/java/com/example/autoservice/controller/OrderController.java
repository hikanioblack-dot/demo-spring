package com.example.autoservice.controller;

import com.example.autoservice.model.Order;
import com.example.autoservice.repository.OrderRepository;
import com.example.autoservice.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepository repository;
    private final SecurityUtils securityUtils;

    public OrderController(OrderRepository repository, SecurityUtils securityUtils) {
        this.repository = repository;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/my")
    public List<Order> getMyOrders() {
        return repository.findByClient_Id(securityUtils.getCurrentUser().getClient().getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        return repository.findById(id).map(order -> {
            if (!securityUtils.isStaff()) {
                Long myId = securityUtils.getCurrentUser().getClient().getId();
                if (!order.getClient().getId().equals(myId)) return ResponseEntity.status(403).<Order>build();
            }
            return ResponseEntity.ok(order);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIC')")
    public List<Order> getAll() { return repository.findAll(); }
}