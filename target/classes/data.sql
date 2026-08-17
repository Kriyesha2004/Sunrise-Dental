-- Insert users (Passwords are BCrypt hash of 'password': $2a$10$ulhWR8QjfKQsBf2nR61KWu8QBbKYuUxhDqDhK22jiFH8se.7tiMsu)
INSERT INTO users (username, password_hash, role, fullname) VALUES 
('admin', '$2a$10$ulhWR8QjfKQsBf2nR61KWu8QBbKYuUxhDqDhK22jiFH8se.7tiMsu', 'ADMIN', 'Admin Staff'),
('receptionist', '$2a$10$ulhWR8QjfKQsBf2nR61KWu8QBbKYuUxhDqDhK22jiFH8se.7tiMsu', 'RECEPTIONIST', 'Dilina Perera'),
('dentist', '$2a$10$ulhWR8QjfKQsBf2nR61KWu8QBbKYuUxhDqDhK22jiFH8se.7tiMsu', 'DENTIST', 'Dr. Smith');

-- Insert initial patient records
INSERT INTO patients (name, address, contact_number, email) VALUES 
('John Doe', '123 Temple Rd, Colombo', '0771234567', 'john.doe@example.com'),
('Jane Silva', '45 Galle Rd, Galle', '0719876543', 'jane.silva@example.com'),
('Robert Perera', '78 Kandy Rd, Kadawatha', '0723456789', 'robert.perera@example.com');

-- Insert initial appointments
INSERT INTO appointments (appointment_number, patient_id, dentist_name, treatment_type, appointment_date, appointment_time, consultation_fee, status) VALUES 
('APT001', 1, 'Dr. Smith', 'Cleaning', '2026-08-20', '10:00:00', 50.00, 'SCHEDULED'),
('APT002', 2, 'Dr. Smith', 'Root Canal', '2026-08-21', '14:30:00', 50.00, 'SCHEDULED'),
('APT003', 3, 'Dr. Perera', 'Filling', '2026-08-22', '09:00:00', 40.00, 'SCHEDULED');
