INSERT INTO clients (name, phone, email) VALUES
('Игорь Смирнов', '+79001234567', 'igor@example.com'),
('Анна Петрова', '+79101112233', NULL);

INSERT INTO cars (brand, model, year, license_plate, client_id) VALUES
('Toyota', 'Camry', 2021, 'A123BC', 1),
('BMW', 'X5', 2022, 'B999XY', 2);

INSERT INTO mechanics (name, specialization, phone) VALUES
('Алексей Петров', 'тормоза', '+79205556677'),
('Сергей Иванов', 'двигатель', '+79307778899');

INSERT INTO orders (description, status, car_id, client_id) VALUES
('Замена масла', 'принято', 1, 1),
('Ремонт тормозов', 'в работе', 2, 2);

INSERT INTO assignments (order_id, mechanic_id) VALUES
(1, 1),
(2, 2);