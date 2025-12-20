package com.example.autoservice.controller;

import com.example.autoservice.dto.*;
import com.example.autoservice.model.*;
import com.example.autoservice.repository.*;
import com.example.autoservice.dto.CreateOrderRequest;
import com.example.autoservice.dto.AssignRequest;
import com.example.autoservice.dto.OrderWithCar;
import com.example.autoservice.dto.MechanicWorkload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/business")
public class BusinessController {

    @Autowired private OrderRepository orderRepo;
    @Autowired private CarRepository carRepo;
    @Autowired private ClientRepository clientRepo;
    @Autowired private MechanicRepository mechanicRepo;
    @Autowired private AssignmentRepository assignmentRepo;

    @PostMapping("/orders")
    @Transactional
    public Order createOrder(@RequestBody CreateOrderRequest req) {
        if (!carRepo.existsById(req.getCarId())) throw new RuntimeException("Car not found");
        if (!clientRepo.existsById(req.getClientId())) throw new RuntimeException("Client not found");

        Order order = new Order();
        order.setDescription(req.getDescription());
        order.setStatus("принято");
        order.setCarId(req.getCarId());
        order.setClientId(req.getClientId());
        return orderRepo.save(order);
    }

    @PostMapping("/assign")
    @Transactional
    public Assignment assign(@RequestBody AssignRequest req) {
        if (!orderRepo.existsById(req.getOrderId())) throw new RuntimeException("Order not found");
        if (!mechanicRepo.existsById(req.getMechanicId())) throw new RuntimeException("Mechanic not found");

        Assignment a = new Assignment();
        a.setOrderId(req.getOrderId());
        a.setMechanicId(req.getMechanicId());
        return assignmentRepo.save(a);
    }

    @PostMapping("/orders/{id}/complete")
    @Transactional
    public Order completeOrder(@PathVariable Long id) {
        Order order = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("готово");
        order.setCompletedAt(LocalDateTime.now());

        assignmentRepo.findByOrder_Id(id).forEach(a -> {
            a.setCompleted(true);
            assignmentRepo.save(a);
        });

        return orderRepo.save(order);
    }

    @GetMapping("/clients/{clientId}/orders")
    public List<OrderWithCar> getClientOrders(@PathVariable Long clientId) {
        return orderRepo.findByClient_Id(clientId).stream()
                .map(order -> new OrderWithCar(order, carRepo.findById(order.getCarId()).orElse(null)))
                .collect(Collectors.toList());
    }

    @GetMapping("/mechanics/{id}/workload")
    public MechanicWorkload getMechanicWorkload(@PathVariable Long id) {
        long count = assignmentRepo.countByMechanic_IdAndCompletedFalse(id);
        return new MechanicWorkload(id, count);
    }
}