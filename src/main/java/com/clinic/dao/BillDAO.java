package com.clinic.dao;

import com.clinic.model.Bill;
import java.util.List;
import java.util.Optional;

public interface BillDAO {
    Optional<Bill> findById(Long id);
    Optional<Bill> findByAppointmentId(Long appointmentId);
    Optional<Bill> findByAppointmentNumber(String appointmentNumber);
    Bill save(Bill bill);
    List<Bill> findAll();
    void deleteById(Long id);
}
