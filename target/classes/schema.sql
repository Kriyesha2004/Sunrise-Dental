-- Drop tables if they exist to start fresh
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS users;

-- Users Table (Staff Auth)
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    fullname VARCHAR(100) NOT NULL
);

-- Patients Table
CREATE TABLE patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    contact_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL
);

-- Appointments Table
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_number VARCHAR(50) NOT NULL UNIQUE,
    patient_id INT NOT NULL,
    dentist_name VARCHAR(100) NOT NULL,
    treatment_type VARCHAR(100) NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE
);

-- Bills Table
CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL UNIQUE,
    treatment_cost DECIMAL(10,2) NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL,
    total_cost DECIMAL(10,2) NOT NULL,
    tax DECIMAL(10,2) NOT NULL,
    grand_total DECIMAL(10,2) NOT NULL,
    bill_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE CASCADE
);
