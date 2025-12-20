package com.example.autoservice.dto;

public class ClientWithCarRequest {
    private ClientDto client;
    private CarDto car;

    public ClientDto getClient() { return client; }
    public void setClient(ClientDto client) { this.client = client; }
    public CarDto getCar() { return car; }
    public void setCar(CarDto car) { this.car = car; }

    public static class ClientDto {
        private String name;
        private String phone;
        private String email;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class CarDto {
        private String brand;
        private String model;
        private int year;
        private String licensePlate;

        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }
        public String getLicensePlate() { return licensePlate; }
        public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    }
}