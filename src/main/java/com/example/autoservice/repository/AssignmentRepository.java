package com.example.autoservice.repository;

import com.example.autoservice.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByOrder_Id(Long orderId);
    long countByMechanic_IdAndCompletedFalse(Long mechanicId);
}