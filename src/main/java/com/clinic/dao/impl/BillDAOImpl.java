package com.clinic.dao.impl;

import com.clinic.dao.BillDAO;
import com.clinic.dao.BillRepository;
import com.clinic.model.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class BillDAOImpl implements BillDAO {

    private final BillRepository billRepository;

    @Autowired
    public BillDAOImpl(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    public Optional<Bill> findById(Long id) {
        return billRepository.findById(id);
    }

    @Override
    public Optional<Bill> findByAppointmentId(Long appointmentId) {
        return billRepository.findByAppointmentId(appointmentId);
    }

    @Override
    public Optional<Bill> findByAppointmentNumber(String appointmentNumber) {
        return billRepository.findByAppointmentAppointmentNumber(appointmentNumber);
    }

    @Override
    public Bill save(Bill bill) {
        return billRepository.save(bill);
    }

    @Override
    public java.util.List<Bill> findAll() {
        return billRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        billRepository.deleteById(id);
    }
}
