package com.example.autoservice.controller;

import com.example.autoservice.model.Mechanic;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/mechanics")
public class MechanicController {

    private List<Mechanic> mechanics = new ArrayList<>();
    private AtomicLong currentId = new AtomicLong(1);

    @PostMapping
    public Mechanic createMechanic(@RequestBody Mechanic mechanic) {
        mechanic.setId(currentId.getAndIncrement());
        mechanics.add(mechanic);
        return mechanic;
    }

    @GetMapping
    public List<Mechanic> getAllMechanics() {
        return mechanics;
    }

    @GetMapping("/{id}")
    public Mechanic getMechanicById(@PathVariable Long id) {
        return mechanics.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }

    @PutMapping("/{id}")
    public Mechanic updateMechanic(@PathVariable Long id, @RequestBody Mechanic updatedMechanic) {
        for (int i = 0; i < mechanics.size(); i++) {
            if (mechanics.get(i).getId().equals(id)) {
                updatedMechanic.setId(id);
                mechanics.set(i, updatedMechanic);
                return updatedMechanic;
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteMechanic(@PathVariable Long id) {
        mechanics.removeIf(m -> m.getId().equals(id));
    }
}