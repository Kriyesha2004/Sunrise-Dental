package com.clinic.service;

import com.clinic.model.Appointment;
import java.math.BigDecimal;
import java.util.Optional;

public interface AppointmentService {
    Appointment registerAppointment(
        String patientName,
        String patientAddress,
        String patientContact,
        String dentistName,
        String treatmentType,
        String dateStr,
        String timeStr,
        BigDecimal consultationFee
    );

    Optional<Appointment> getAppointmentByNumber(String appointmentNumber);
}
