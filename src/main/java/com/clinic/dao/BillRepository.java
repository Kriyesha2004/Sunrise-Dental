package com.clinic.dao;

import com.clinic.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByAppointmentId(Long appointmentId);
    Optional<Bill> findByAppointmentAppointmentNumber(String appointmentNumber);
}
