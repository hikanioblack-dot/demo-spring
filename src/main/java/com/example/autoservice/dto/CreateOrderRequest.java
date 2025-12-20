package com.example.autoservice.dto;

public class CreateOrderRequest {
    private String description;
    private Long carId;
    private Long clientId;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
}