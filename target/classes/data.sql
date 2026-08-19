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

-- Insert initial Help Guide
INSERT INTO help_guides (step_instructions, design_constraints) VALUES 
(
  'User Authentication: Log into the system using your staff credentials. Access is restricted to authorized clinic employees (Administrators, Receptionists, and Dentists).|Register New Appointment: Navigate to the Book Appointment tab. Input the patient''s full name, address, phone number, select their assigned dentist, treatment type, and enter the date/time. The default consultation fee is configured automatically. Upon saving, a unique Appointment Registration Number (e.g., APT0482) will be generated. Copy or record this number.|Search Appointment Details: Go to the Search tab, type the APTXXXX number, and click Search. This will display all registered patient information, scheduling status, and details.|Calculate and Process Bills: Under the Billing tab, search by the appointment number. The system applies the Factory Design Pattern to fetch treatment costs based on the treatment type: Cleaning ($50.00), Filling ($80.00), Extraction ($120.00), Root Canal ($300.00). The total cost equals the treatment cost plus the consultation fee, plus 10% government service tax.|Settle Payments & Printing: Click Process Payment to mark the invoice as PAID. This triggers a database operation that automatically changes the appointment status to COMPLETED. Click Print Bill / Receipt to open a custom, print-ready document format.|Exit System: To terminate your secure session, click Exit System in the navigation bar. This safely invalidates the session and prevents unauthorized access.',
  'Database Triggers: The after_bill_payment trigger watches updates to the bills table. When payment_status shifts to PAID, the trigger automatically fires an update statement on the appointments table, updating its status to COMPLETED.|Factory Pattern: Treatment prices are strictly managed by a polymorphic strategy engine in the business logic layer, decoupling price policies from the database structure.|Secure Sessions: Standard session cookies are used. The server checks the authentication token on every API call to prevent unauthorized API spoofing.'
);
