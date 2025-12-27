package com.example.autoservice.controller;

import com.example.autoservice.model.Order;
import com.example.autoservice.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepository repository;
    public OrderController(OrderRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Order> getAll() { return repository.findAll(); }

    @PostMapping
    public Order create(@RequestBody Order order) { return repository.save(order); }

    @PutMapping("/{id}")
    public Order update(@PathVariable Long id, @RequestBody Order updated) {
        updated.setId(id);
        return repository.save(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { repository.deleteById(id); }
}