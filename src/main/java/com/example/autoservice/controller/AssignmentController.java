package com.example.autoservice.controller;

import com.example.autoservice.model.Assignment;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    private List<Assignment> assignments = new ArrayList<>();
    private AtomicLong currentId = new AtomicLong(1);

    @PostMapping
    public Assignment createAssignment(@RequestBody Assignment assignment) {
        assignment.setId(currentId.getAndIncrement());
        assignments.add(assignment);
        return assignment;
    }

    @GetMapping
    public List<Assignment> getAllAssignments() {
        return assignments;
    }

    @GetMapping("/{id}")
    public Assignment getAssignmentById(@PathVariable Long id) {
        return assignments.stream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }

    @PutMapping("/{id}")
    public Assignment updateAssignment(@PathVariable Long id, @RequestBody Assignment updatedAssignment) {
        for (int i = 0; i < assignments.size(); i++) {
            if (assignments.get(i).getId().equals(id)) {
                updatedAssignment.setId(id);
                assignments.set(i, updatedAssignment);
                return updatedAssignment;
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteAssignment(@PathVariable Long id) {
        assignments.removeIf(a -> a.getId().equals(id));
    }
}