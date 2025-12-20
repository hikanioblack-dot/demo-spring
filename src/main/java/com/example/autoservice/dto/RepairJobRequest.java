package com.example.autoservice.dto;

public class RepairJobRequest {
    private String description;
    private Long carId;
    private Long clientId;
    private Long mechanicId;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public Long getMechanicId() { return mechanicId; }
    public void setMechanicId(Long mechanicId) { this.mechanicId = mechanicId; }
}