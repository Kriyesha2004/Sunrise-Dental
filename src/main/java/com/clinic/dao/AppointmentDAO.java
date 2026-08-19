package com.clinic.dao;

import com.clinic.model.Appointment;
import java.util.List;
import java.util.Optional;

public interface AppointmentDAO {
    Optional<Appointment> findById(Long id);
    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);
    boolean existsByAppointmentNumber(String appointmentNumber);
    Appointment save(Appointment appointment);
    List<Appointment> findAll();
    void deleteById(Long id);
}
