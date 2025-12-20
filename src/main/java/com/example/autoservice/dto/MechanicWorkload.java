package com.example.autoservice.dto;

public class MechanicWorkload {
    private Long mechanicId;
    private long activeAssignments;

    public MechanicWorkload(Long mechanicId, long activeAssignments) {
        this.mechanicId = mechanicId;
        this.activeAssignments = activeAssignments;
    }

    public Long getMechanicId() { return mechanicId; }
    public long getActiveAssignments() { return activeAssignments; }
}