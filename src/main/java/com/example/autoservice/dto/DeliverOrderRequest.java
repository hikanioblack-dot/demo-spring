package com.example.autoservice.dto;

public class DeliverOrderRequest {
    private Long orderId;
    private String clientSignature;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getClientSignature() { return clientSignature; }
    public void setClientSignature(String clientSignature) { this.clientSignature = clientSignature; }
}