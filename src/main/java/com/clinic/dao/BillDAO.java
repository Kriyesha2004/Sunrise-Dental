package com.clinic.dao;

import com.clinic.model.Bill;
import java.util.Optional;

public interface BillDAO {
    Optional<Bill> findById(Long id);
    Optional<Bill> findByAppointmentId(Long appointmentId);
    Optional<Bill> findByAppointmentNumber(String appointmentNumber);
    Bill save(Bill bill);
}
