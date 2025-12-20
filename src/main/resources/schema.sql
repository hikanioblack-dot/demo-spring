-- Очищаем существующие таблицы (в обратном порядке зависимостей)
DROP TABLE IF EXISTS assignments;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cars;
DROP TABLE IF EXISTS mechanics;
DROP TABLE IF EXISTS clients;

-- 1. Клиенты (базовая таблица — без внешних ключей)
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE
);

-- 2. Механики (тоже независимая таблица)
CREATE TABLE mechanics (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE
);

-- 3. Автомобили (зависит от клиентов)
CREATE TABLE cars (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL CHECK (year > 1900 AND year <= 2030),
    license_plate VARCHAR(20) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

-- 4. Заказы (зависит от клиентов и автомобилей)
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('принято', 'в работе', 'готово', 'выдано')),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP,
    car_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE RESTRICT,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE RESTRICT
);

-- 5. Назначения (зависит от заказов и механиков)
CREATE TABLE assignments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    mechanic_id BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT NOW(),
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (mechanic_id) REFERENCES mechanics(id) ON DELETE RESTRICT,
    UNIQUE (order_id, mechanic_id)
);