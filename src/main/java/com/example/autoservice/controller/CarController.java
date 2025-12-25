package com.example.autoservice.controller;

import com.example.autoservice.model.Car;
import com.example.autoservice.repository.CarRepository;
import com.example.autoservice.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarRepository repository;
    private final SecurityUtils securityUtils;

    public CarController(CarRepository repository, SecurityUtils securityUtils) {
        this.repository = repository;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/my")
    public List<Car> getMyCars() {
        return repository.findByClient_Id(securityUtils.getCurrentUser().getClient().getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getById(@PathVariable Long id) {
        return repository.findById(id).map(car -> {
            if (!securityUtils.isStaff()) {
                Long myId = securityUtils.getCurrentUser().getClient().getId();
                if (!car.getClient().getId().equals(myId)) return ResponseEntity.status(403).<Car>build();
            }
            return ResponseEntity.ok(car);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIC')")
    public List<Car> getAll() { return repository.findAll(); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIC')")
    public Car create(@RequestBody Car car) { return repository.save(car); }
}