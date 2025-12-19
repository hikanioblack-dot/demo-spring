package com.example.autoservice.controller;

import com.example.autoservice.model.Order;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private List<Order> orders = new ArrayList<>();
    private AtomicLong currentId = new AtomicLong(1);

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        order.setId(currentId.getAndIncrement());
        orders.add(order);
        return order;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orders;
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orders.stream().filter(o -> o.getId().equals(id)).findFirst().orElse(null);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId().equals(id)) {
                updatedOrder.setId(id);
                orders.set(i, updatedOrder);
                return updatedOrder;
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orders.removeIf(o -> o.getId().equals(id));
    }
}