package com.clinic.service.impl;

import com.clinic.dao.AppointmentDAO;
import com.clinic.dao.PatientDAO;
import com.clinic.model.Appointment;
import com.clinic.model.Patient;
import com.clinic.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final PatientDAO patientDAO;
    private final AppointmentDAO appointmentDAO;
    private final Random random = new Random();

    @Autowired
    public AppointmentServiceImpl(PatientDAO patientDAO, AppointmentDAO appointmentDAO) {
        this.patientDAO = patientDAO;
        this.appointmentDAO = appointmentDAO;
    }

    @Override
    @Transactional
    public Appointment registerAppointment(
            String patientName,
            String patientAddress,
            String patientContact,
            String patientEmail,
            String dentistName,
            String treatmentType,
            String dateStr,
            String timeStr,
            BigDecimal consultationFee) {

        // Find or create patient
        Optional<Patient> patientOpt = patientDAO.findByNameAndContactNumber(patientName, patientContact);
        Patient patient = patientOpt.map(p -> {
            if (p.getEmail() == null || !p.getEmail().equals(patientEmail)) {
                p.setEmail(patientEmail);
                return patientDAO.save(p);
            }
            return p;
        }).orElseGet(() -> {
            Patient p = new Patient(patientName, patientAddress, patientContact, patientEmail);
            return patientDAO.save(p);
        });

        // Generate unique appointment number
        String appointmentNumber;
        do {
            appointmentNumber = "APT" + String.format("%04d", random.nextInt(10000));
        } while (appointmentDAO.existsByAppointmentNumber(appointmentNumber));

        // Parse date and time
        LocalDate appointmentDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalTime appointmentTime = LocalTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_TIME);

        Appointment appointment = new Appointment(
                appointmentNumber,
                patient,
                dentistName,
                treatmentType,
                appointmentDate,
                appointmentTime,
                consultationFee,
                "SCHEDULED"
        );

        return appointmentDAO.save(appointment);
    }

    @Override
    public Optional<Appointment> getAppointmentByNumber(String appointmentNumber) {
        return appointmentDAO.findByAppointmentNumber(appointmentNumber);
    }
}
