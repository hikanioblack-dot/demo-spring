package com.example.autoservice.controller;

import com.example.autoservice.model.Mechanic;
import com.example.autoservice.repository.MechanicRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mechanics")
public class MechanicController {

    private final MechanicRepository repository;

    public MechanicController(MechanicRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Mechanic create(@RequestBody Mechanic mechanic) {
        return repository.save(mechanic);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mechanic> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mechanic> update(@PathVariable Long id, @RequestBody Mechanic updated) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        updated.setId(id);
        return ResponseEntity.ok(repository.save(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}