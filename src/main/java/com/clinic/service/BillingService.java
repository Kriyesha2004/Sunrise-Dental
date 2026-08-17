package com.clinic.service;

import com.clinic.model.Bill;
import java.util.Optional;

public interface BillingService {
    Bill calculateAndSaveBill(String appointmentNumber);
    Optional<Bill> getBillByAppointmentNumber(String appointmentNumber);
    Bill payBill(Long billId);
}
