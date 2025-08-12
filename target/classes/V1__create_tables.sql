-- DEBUG_SETUP_ptfms.sql

-- 1) Drop the entire schema if it exists
DROP DATABASE IF EXISTS ptfms;

-- 2) Create a fresh schema
CREATE DATABASE ptfms
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- 3) Switch into it
USE ptfms;

-- 4) Create tables
CREATE TABLE users (
  user_id       INT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(100)   NOT NULL,
  email         VARCHAR(150)   NOT NULL UNIQUE,
  username      VARCHAR(50)    NOT NULL UNIQUE,
  password_hash VARCHAR(255)   NOT NULL,
  role          ENUM('Manager', 'Operator') NOT NULL,
  status        ENUM('On Duty', 'Off Duty', 'Break') DEFAULT 'On Duty',
  created_at    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vehicles (
  vehicle_id         INT AUTO_INCREMENT PRIMARY KEY,
  vin                VARCHAR(17)    NOT NULL UNIQUE,
  vehicle_number     VARCHAR(20)    NOT NULL UNIQUE,
  vehicle_type       ENUM('Diesel Bus', 'Electric Light Rail', 'Diesel-Electric Train', 'CNG Bus', 'Electric Bus') NOT NULL,
  make               VARCHAR(50),
  model              VARCHAR(50),
  year               INT,
  fuel_type          ENUM('Diesel', 'Electric', 'CNG', 'Hybrid') NOT NULL,
  consumption_rate   DECIMAL(8,3),  -- L/100km or kWh/km
  max_passengers     INT,
  current_route      VARCHAR(50),
  status             ENUM('Active', 'Maintenance', 'Available') DEFAULT 'Available',
  created_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE maintenance (
  maintenance_id     INT AUTO_INCREMENT PRIMARY KEY,
  vehicle_id         INT            NOT NULL,
  service_date       DATE           NOT NULL,
  description        TEXT,
  cost               DECIMAL(10,2),
  status             ENUM('Pending', 'Completed') DEFAULT 'Pending',
  created_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_maintenance_vehicle
    FOREIGN KEY (vehicle_id)
    REFERENCES vehicles(vehicle_id)
    ON DELETE CASCADE
);

CREATE TABLE fuel_logs (
  fuel_log_id    INT AUTO_INCREMENT PRIMARY KEY,
  vehicle_id     INT          NOT NULL,
  log_date       DATE         NOT NULL,
  fuel_type      ENUM('Diesel', 'Electric', 'CNG') NOT NULL,
  amount         DECIMAL(8,3) NOT NULL,  -- Liters or kWh
  cost           DECIMAL(8,2),
  distance       DECIMAL(8,1), -- km traveled
  operator_id    INT,
  CONSTRAINT fk_fuellogs_vehicle
    FOREIGN KEY (vehicle_id)
    REFERENCES vehicles(vehicle_id)
    ON DELETE CASCADE,
  CONSTRAINT fk_fuellogs_operator
    FOREIGN KEY (operator_id)
    REFERENCES users(user_id)
    ON DELETE SET NULL
);

-- GPS tracking with transit station support
CREATE TABLE gps_tracking (
  tracking_id    INT AUTO_INCREMENT PRIMARY KEY,
  vehicle_id     INT NOT NULL,
  latitude       DECIMAL(10, 8) NOT NULL,
  longitude      DECIMAL(11, 8) NOT NULL,
  timestamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  operator_id    INT,
  station_id     VARCHAR(50),  -- Transit station identifier
  event_type     ENUM('ARRIVAL', 'DEPARTURE', 'LOCATION') DEFAULT 'LOCATION',
  speed          DECIMAL(5,2),  -- Speed in km/h
  notes          TEXT,
  CONSTRAINT fk_gps_vehicle
    FOREIGN KEY (vehicle_id)
    REFERENCES vehicles(vehicle_id)
    ON DELETE CASCADE,
  CONSTRAINT fk_gps_operator
    FOREIGN KEY (operator_id)
    REFERENCES users(user_id)
    ON DELETE SET NULL
);

-- Component tracking for predictive maintenance
CREATE TABLE vehicle_components (
  component_id   INT AUTO_INCREMENT PRIMARY KEY,
  vehicle_id     INT NOT NULL,
  component_name VARCHAR(100) NOT NULL,
  usage_hours    DECIMAL(10,2) DEFAULT 0,
  max_hours      DECIMAL(10,2), -- Alert threshold
  status         ENUM('Good', 'Warning', 'Critical') DEFAULT 'Good',
  CONSTRAINT fk_components_vehicle
    FOREIGN KEY (vehicle_id)
    REFERENCES vehicles(vehicle_id)
    ON DELETE CASCADE
);

-- Alerts for maintenance and fuel consumption
CREATE TABLE alerts (
  alert_id       INT AUTO_INCREMENT PRIMARY KEY,
  vehicle_id     INT NOT NULL,
  alert_type     ENUM('Maintenance', 'Fuel Consumption', 'GPS') NOT NULL,
  message        TEXT NOT NULL,
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  status         ENUM('Open', 'Resolved') DEFAULT 'Open',
  CONSTRAINT fk_alerts_vehicle
    FOREIGN KEY (vehicle_id)
    REFERENCES vehicles(vehicle_id)
    ON DELETE CASCADE
);

-- 5) Insert sample data for testing
-- Sample users (passwords are BCrypt hashed for 'password123')
INSERT INTO users (name, email, username, password_hash, role) VALUES 
('John Manager', 'john.manager@ptfms.com', 'jmanager', '$2a$10$8K1p/a0dRxKU4X1cVhCLCe7vNX4kH0/i1rXo3qH7YF.yI2dN8u.SW', 'Manager'),
('Sarah Operator', 'sarah.operator@ptfms.com', 'soperator', '$2a$10$8K1p/a0dRxKU4X1cVhCLCe7vNX4kH0/i1rXo3qH7YF.yI2dN8u.SW', 'Operator'),
('Mike Wilson', 'mike.wilson@ptfms.com', 'mwilson', '$2a$10$8K1p/a0dRxKU4X1cVhCLCe7vNX4kH0/i1rXo3qH7YF.yI2dN8u.SW', 'Manager');

-- Sample vehicles
INSERT INTO vehicles (vin, vehicle_number, vehicle_type, make, model, year, fuel_type, consumption_rate, max_passengers, current_route, status) VALUES 
('1HGCM82633A004352', 'BUS001', 'Diesel Bus', 'Volvo', 'B8RLE', 2020, 'Diesel', 35.5, 40, 'Route 101', 'Active'),
('JH4TB2H26CC000000', 'LRT001', 'Electric Light Rail', 'Siemens', 'S70', 2021, 'Electric', 4.2, 120, 'Blue Line', 'Active'),
('WBAFR9C50BC123456', 'BUS002', 'CNG Bus', 'New Flyer', 'Xcelsior', 2019, 'CNG', 28.3, 35, 'Route 205', 'Available');

-- Sample components
INSERT INTO vehicle_components (vehicle_id, component_name, usage_hours, max_hours, status) VALUES
(1, 'Brake Pads', 1250.5, 2000, 'Good'),
(1, 'Engine', 8500.0, 10000, 'Warning'),
(2, 'Pantograph', 3200.0, 5000, 'Good');

-- Sample fuel logs
INSERT INTO fuel_logs (vehicle_id, log_date, fuel_type, amount, cost, distance, operator_id) VALUES
(1, '2025-08-01', 'Diesel', 85.5, 142.50, 220.3, 2),
(3, '2025-08-02', 'CNG', 65.2, 98.30, 199.9, 2),
(2, '2025-08-03', 'Electric', 124.8, 18.72, 94.6, 2);

-- Sample maintenance records
INSERT INTO maintenance (vehicle_id, service_date, description, cost, status) VALUES
(1, '2025-07-15', 'Regular oil change and filter replacement', 125.50, 'Completed'),
(1, '2025-08-05', 'Brake pad inspection and adjustment', 89.75, 'Completed'),
(2, '2025-07-20', 'Pantograph maintenance and electrical system check', 450.00, 'Completed'),
(2, '2025-08-10', 'Monthly safety inspection', 75.00, 'Pending'),
(3, '2025-07-25', 'CNG system pressure test and valve maintenance', 320.25, 'Completed'),
(3, '2025-08-08', 'Tire rotation and brake system inspection', 150.00, 'Pending'),
(1, '2025-08-12', 'Engine diagnostic and performance check', 200.00, 'Pending');

-- Sample alerts
INSERT INTO alerts (vehicle_id, alert_type, message) VALUES
(1, 'Maintenance', 'Engine approaching maximum service hours'),
(2, 'GPS', 'Routine inspection due'),
(3, 'Fuel Consumption', 'Fuel efficiency below threshold');

-- Sample reports
INSERT INTO reports (title, report_type, data_json, generated_by, generated_at, period_start, period_end, format, status) VALUES
('Monthly Fleet Performance Report', 'FLEET', '{"total_vehicles": 3, "active_vehicles": 2, "maintenance_vehicles": 0, "available_vehicles": 1, "total_mileage": 15420, "fuel_efficiency": 28.5}', 1, '2025-08-01 09:00:00', '2025-07-01 00:00:00', '2025-07-31 23:59:59', 'JSON', 'COMPLETED'),
('Weekly Maintenance Cost Analysis', 'MAINTENANCE', '{"total_cost": 1410.50, "completed_services": 4, "pending_services": 3, "average_cost_per_service": 235.08, "highest_cost_vehicle": "LRT001"}', 1, '2025-08-05 14:30:00', '2025-07-29 00:00:00', '2025-08-04 23:59:59', 'PDF', 'COMPLETED'),
('Fuel Consumption Report - July 2025', 'FUEL', '{"total_fuel_cost": 259.52, "total_fuel_amount": 275.5, "diesel_consumption": 85.5, "cng_consumption": 65.2, "electric_consumption": 124.8, "most_efficient_vehicle": "LRT001"}', 3, '2025-08-02 11:15:00', '2025-07-01 00:00:00', '2025-07-31 23:59:59', 'HTML', 'COMPLETED'),
('Operator Performance Summary', 'OPERATOR', '{"total_operators": 3, "active_operators": 2, "total_shifts": 45, "average_shift_duration": 8.2, "fuel_logs_recorded": 3, "maintenance_scheduled": 7}', 1, '2025-08-06 08:00:00', '2025-07-01 00:00:00', '2025-07-31 23:59:59', 'JSON', 'PENDING'),
('GPS Tracking and Route Analysis', 'GPS', '{"total_tracking_points": 1250, "routes_covered": 3, "average_speed": 45.2, "stops_recorded": 156, "route_efficiency": 87.3}', 2, '2025-08-04 16:45:00', '2025-07-15 00:00:00', '2025-08-04 23:59:59', 'PDF', 'COMPLETED'),
('Daily Fleet Status Report', 'FLEET', '{"vehicles_inspected": 3, "alerts_generated": 3, "fuel_refills": 2, "maintenance_due": 2, "operational_status": "Normal"}', 2, '2025-08-06 07:30:00', '2025-08-05 00:00:00', '2025-08-05 23:59:59', 'HTML', 'COMPLETED'),
('Quarterly Maintenance Forecast', 'MAINTENANCE', '{"predicted_costs": 2500.00, "scheduled_services": 12, "priority_vehicles": ["BUS001", "LRT001"], "budget_variance": -150.00}', 1, '2025-08-03 10:20:00', '2025-08-01 00:00:00', '2025-10-31 23:59:59', 'JSON', 'FAILED');

-- Reports table
CREATE TABLE reports (
  report_id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  report_type ENUM('FLEET', 'MAINTENANCE', 'FUEL', 'OPERATOR', 'GPS') NOT NULL,
  data_json TEXT,
  generated_by INT NOT NULL,
  generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  period_start TIMESTAMP,
  period_end TIMESTAMP,
  format ENUM('HTML', 'PDF', 'JSON') DEFAULT 'JSON',
  status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
  FOREIGN KEY (generated_by) REFERENCES users(user_id) ON DELETE CASCADE
);
