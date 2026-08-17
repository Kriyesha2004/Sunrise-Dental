package com.clinic.dao.impl;

import com.clinic.dao.AppointmentDAO;
import com.clinic.dao.AppointmentRepository;
import com.clinic.model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class AppointmentDAOImpl implements AppointmentDAO {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentDAOImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Override
    public Optional<Appointment> findByAppointmentNumber(String appointmentNumber) {
        return appointmentRepository.findByAppointmentNumber(appointmentNumber);
    }

    @Override
    public boolean existsByAppointmentNumber(String appointmentNumber) {
        return appointmentRepository.existsByAppointmentNumber(appointmentNumber);
    }

    @Override
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
}
