package com.example.autoservice.controller;

import com.example.autoservice.model.Car;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/cars")
public class CarController {

    private List<Car> cars = new ArrayList<>();
    private AtomicLong currentId = new AtomicLong(1);

    @PostMapping
    public Car createCar(@RequestBody Car car) {
        car.setId(currentId.getAndIncrement());
        cars.add(car);
        return car;
    }

    @GetMapping
    public List<Car> getAllCars() {
        return cars;
    }

    @GetMapping("/{id}")
    public Car getCarById(@PathVariable Long id) {
        return cars.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    @PutMapping("/{id}")
    public Car updateCar(@PathVariable Long id, @RequestBody Car updatedCar) {
        for (int i = 0; i < cars.size(); i++) {
            if (cars.get(i).getId().equals(id)) {
                updatedCar.setId(id);
                cars.set(i, updatedCar);
                return updatedCar;
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable Long id) {
        cars.removeIf(c -> c.getId().equals(id));
    }
}