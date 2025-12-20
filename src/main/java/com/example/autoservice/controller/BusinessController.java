package com.example.autoservice.controller;

import com.example.autoservice.dto.*;
import com.example.autoservice.model.*;
import com.example.autoservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/business")
public class BusinessController {

    @Autowired private ClientRepository clientRepo;
    @Autowired private CarRepository carRepo;
    @Autowired private OrderRepository orderRepo;
    @Autowired private MechanicRepository mechanicRepo;
    @Autowired private AssignmentRepository assignmentRepo;

    // 1. Регистрация клиента + автомобиля
    @PostMapping("/client-with-car")
    @Transactional
    public ClientWithCarResponse createClientWithCar(@RequestBody ClientWithCarRequest req) {
        Client client = new Client();
        client.setName(req.getClient().getName());
        client.setPhone(req.getClient().getPhone());
        client.setEmail(req.getClient().getEmail());
        client = clientRepo.save(client);

        Car car = new Car();
        car.setBrand(req.getCar().getBrand());
        car.setModel(req.getCar().getModel());
        car.setYear(req.getCar().getYear());
        car.setLicensePlate(req.getCar().getLicensePlate());
        car.setClient(client);
        car = carRepo.save(car);

        ClientWithCarResponse res = new ClientWithCarResponse();
        res.setClientId(client.getId());
        res.setCarId(car.getId());
        return res;
    }

    // 2. Оформить ремонт + назначить механика
    @PostMapping("/repair-job")
    @Transactional
    public Order createRepairJob(@RequestBody RepairJobRequest req) {
        if (!carRepo.existsById(req.getCarId())) throw new RuntimeException("Car not found");
        if (!clientRepo.existsById(req.getClientId())) throw new RuntimeException("Client not found");
        if (!mechanicRepo.existsById(req.getMechanicId())) throw new RuntimeException("Mechanic not found");

        Order order = new Order();
        order.setDescription(req.getDescription());
        order.setStatus("принято");
        order.setCarId(req.getCarId());
        order.setClientId(req.getClientId());
        order = orderRepo.save(order);

        Assignment assignment = new Assignment();
        assignment.setOrder(order);

        // Получаем объект Mechanic и устанавливаем его
        Mechanic mechanic = mechanicRepo.findById(req.getMechanicId())
                .orElseThrow(() -> new RuntimeException("Mechanic not found"));
        assignment.setMechanic(mechanic);

        assignmentRepo.save(assignment);
        return order;
    }

    // 3. Выдать заказ клиенту (автоматически завершает, если нужно)
    @PostMapping("/deliver-order")
    @Transactional
    public Order deliverOrder(@RequestBody DeliverOrderRequest req) {
        Order order = orderRepo.findById(req.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔥 Если заказ ещё не "готово" — автоматически завершаем его
        if (!"готово".equals(order.getStatus()) && !"выдано".equals(order.getStatus())) {
            order.setStatus("готово");
            order.setCompletedAt(LocalDateTime.now());
            order = orderRepo.save(order); // Сохраняем промежуточное состояние
        }

        // Если уже "выдано" — не делаем ничего лишнего
        if ("выдано".equals(order.getStatus())) {
            return order;
        }

        // Помечаем все назначения как выполненные
        List<Assignment> assignments = assignmentRepo.findByOrder_Id(req.getOrderId());
        for (Assignment a : assignments) {
            a.setCompleted(true);
            assignmentRepo.save(a);
        }

        // Меняем статус на "выдано"
        order.setStatus("выдано");
        return orderRepo.save(order);
    }

    // 4. Удалить клиента со всеми данными
    @DeleteMapping("/client/{id}")
    @Transactional
    public ResponseEntity<Void> deleteClientWithAllData(@PathVariable Long id) {
        if (!clientRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<Car> cars = carRepo.findByClient_Id(id);
        for (Car car : cars) {
            List<Order> orders = orderRepo.findByCar_Id(car.getId());
            for (Order order : orders) {
                assignmentRepo.deleteByOrder_Id(order.getId());
                orderRepo.deleteById(order.getId());
            }
            carRepo.deleteById(car.getId());
        }

        clientRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 5. Получить активные заказы
    @GetMapping("/active-repairs")
    public List<ActiveRepairDto> getActiveRepairs() {
        return orderRepo.findByStatus("в работе").stream().map(order -> {
            ActiveRepairDto dto = new ActiveRepairDto();
            dto.setOrderId(order.getId());
            dto.setDescription(order.getDescription());
            dto.setStatus(order.getStatus());

            Client client = clientRepo.findById(order.getClientId()).orElse(null);
            dto.setClientName(client != null ? client.getName() : "Unknown");

            Car car = carRepo.findById(order.getCarId()).orElse(null);
            dto.setCarInfo(car != null ? car.getBrand() + " " + car.getModel() + " " + car.getLicensePlate() : "Unknown");

            List<Assignment> assignments = assignmentRepo.findByOrder_Id(order.getId());
            if (!assignments.isEmpty()) {
                // 🔥 Правильно получаем ID механика через связанный объект
                Long mechanicId = assignments.get(0).getMechanic().getId();
                Mechanic mechanic = mechanicRepo.findById(mechanicId).orElse(null);
                dto.setMechanicName(mechanic != null ? mechanic.getName() : "Not assigned");
            } else {
                dto.setMechanicName("Not assigned");
            }

            return dto;
        }).collect(Collectors.toList());
    }
}