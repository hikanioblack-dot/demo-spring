package com.example.autoservice.controller;

import com.example.autoservice.model.Car;
import com.example.autoservice.repository.CarRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarRepository repository;

    public CarController(CarRepository repository) {
        this.repository = repository;
    }

    // ✅ GET /cars — получить все автомобили
    @GetMapping
    public List<Car> getAllCars() {
        return repository.findAll();
    }

    // GET /cars/{id} — получить один автомобиль
    @GetMapping("/{id}")
    public ResponseEntity<Car> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /cars — создать
    @PostMapping
    public Car create(@RequestBody Car car) {
        return repository.save(car);
    }

    // PUT /cars/{id} — обновить
    @PutMapping("/{id}")
    public ResponseEntity<Car> update(@PathVariable Long id, @RequestBody Car updated) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        updated.setId(id);
        return ResponseEntity.ok(repository.save(updated));
    }

    // DELETE /cars/{id} — удалить
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}