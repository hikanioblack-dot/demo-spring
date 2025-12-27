package com.example.autoservice.controller;

import com.example.autoservice.model.Assignment;
import com.example.autoservice.repository.AssignmentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {
    private final AssignmentRepository repository;
    public AssignmentController(AssignmentRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Assignment> getAll() { return repository.findAll(); }

    @PostMapping
    public Assignment create(@RequestBody Assignment assignment) { return repository.save(assignment); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repository.deleteById(id); }
}