package com.example.autoservice.dto;

import com.example.autoservice.model.Car;
import com.example.autoservice.model.Order;

public class OrderWithCar {
    private Order order;
    private Car car;

    public OrderWithCar(Order order, Car car) {
        this.order = order;
        this.car = car;
    }

    public Order getOrder() { return order; }
    public Car getCar() { return car; }
}