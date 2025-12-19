package com.example.autoservice.controller;

import com.example.autoservice.model.Client;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private List<Client> clients = new ArrayList<>();
    private AtomicLong currentId = new AtomicLong(1);

    @PostMapping
    public Client createClient(@RequestBody Client client) {
        client.setId(currentId.getAndIncrement());
        clients.add(client);
        return client;
    }

    @GetMapping
    public List<Client> getAllClients() {
        return clients;
    }

    @GetMapping("/{id}")
    public Client getClientById(@PathVariable Long id) {
        return clients.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @PutMapping("/{id}")
    public Client updateClient(@PathVariable Long id, @RequestBody Client updatedClient) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getId().equals(id)) {
                updatedClient.setId(id);
                clients.set(i, updatedClient);
                return updatedClient;
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable Long id) {
        clients.removeIf(c -> c.getId().equals(id));
    }
}