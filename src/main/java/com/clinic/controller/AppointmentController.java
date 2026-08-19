package com.clinic.controller;

import com.clinic.dto.AppointmentRequest;
import com.clinic.model.Appointment;
import com.clinic.model.User;
import com.clinic.service.AppointmentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<?> registerAppointment(@Valid @RequestBody AppointmentRequest request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please login first");
        }

        try {
            Appointment appointment = appointmentService.registerAppointment(
                    request.getPatientName(),
                    request.getPatientAddress(),
                    request.getPatientContact(),
                    request.getPatientEmail(),
                    request.getDentistName(),
                    request.getTreatmentType(),
                    request.getAppointmentDate(),
                    request.getAppointmentTime(),
                    request.getConsultationFee()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error registering appointment: " + e.getMessage());
        }
    }

    @GetMapping("/{appointmentNumber}")
    public ResponseEntity<?> getAppointment(@PathVariable String appointmentNumber, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please login first");
        }

        Optional<Appointment> appointmentOpt = appointmentService.getAppointmentByNumber(appointmentNumber);
        if (appointmentOpt.isPresent()) {
            return ResponseEntity.ok(appointmentOpt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found with number: " + appointmentNumber);
    }

    @GetMapping
    public ResponseEntity<?> getAllAppointments(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please login first");
        }
        if (!"ADMIN".equalsIgnoreCase(user.getRole()) && !"DENTIST".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Access restricted to Admin and Dentist");
        }
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please login first");
        }
        if (!"ADMIN".equalsIgnoreCase(user.getRole()) && !"DENTIST".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Access restricted to Admin and Dentist");
        }
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.ok("Appointment deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting appointment: " + e.getMessage());
        }
    }
}
