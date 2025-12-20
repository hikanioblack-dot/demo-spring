package com.example.autoservice.dto;

public class ClientWithCarResponse {
    private Long clientId;
    private Long carId;

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
}