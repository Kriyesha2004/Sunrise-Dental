package com.clinic.service.impl;

import com.clinic.dao.AppointmentDAO;
import com.clinic.dao.BillDAO;
import com.clinic.factory.TreatmentCostCalculator;
import com.clinic.factory.TreatmentCostCalculatorFactory;
import com.clinic.model.Appointment;
import com.clinic.model.Bill;
import com.clinic.service.BillingService;
import com.clinic.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class BillingServiceImpl implements BillingService {

    private final AppointmentDAO appointmentDAO;
    private final BillDAO billDAO;
    private final EmailService emailService;

    @Autowired
    public BillingServiceImpl(AppointmentDAO appointmentDAO, BillDAO billDAO, EmailService emailService) {
        this.appointmentDAO = appointmentDAO;
        this.billDAO = billDAO;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public Bill calculateAndSaveBill(String appointmentNumber) {
        // Find appointment
        Appointment appointment = appointmentDAO.findByAppointmentNumber(appointmentNumber)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentNumber));

        // Check if bill already exists
        Optional<Bill> existingBillOpt = billDAO.findByAppointmentId(appointment.getId());
        if (existingBillOpt.isPresent()) {
            return existingBillOpt.get();
        }

        // Apply Factory Pattern for treatment cost calculation
        TreatmentCostCalculator calculator = TreatmentCostCalculatorFactory.getCalculator(appointment.getTreatmentType());
        BigDecimal treatmentCost = calculator.calculateCost();
        BigDecimal consultationFee = appointment.getConsultationFee();

        // Calculate totals
        BigDecimal totalCost = treatmentCost.add(consultationFee);
        BigDecimal tax = totalCost.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP); // 10% tax
        BigDecimal grandTotal = totalCost.add(tax).setScale(2, RoundingMode.HALF_UP);

        Bill bill = new Bill(
                appointment,
                treatmentCost,
                consultationFee,
                totalCost,
                tax,
                grandTotal,
                "UNPAID"
        );

        return billDAO.save(bill);
    }

    @Override
    public Optional<Bill> getBillByAppointmentNumber(String appointmentNumber) {
        return billDAO.findByAppointmentNumber(appointmentNumber);
    }

    @Override
    @Transactional
    public Bill payBill(Long billId, String baseUrl) {
        Bill bill = billDAO.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + billId));
        
        bill.setPaymentStatus("PAID");
        bill = billDAO.save(bill);
        
        // Let's also update the appointment in memory to keep it synchronized (the trigger will also run in MySQL)
        Appointment appointment = bill.getAppointment();
        appointment.setStatus("COMPLETED");
        appointmentDAO.save(appointment);

        // Send Email notification containing a link to the bill invoice
        String email = appointment.getPatient().getEmail();
        if (email != null && !email.trim().isEmpty()) {
            String invoiceUrl = baseUrl + "/api/bills/public/invoice/" + billId;
            String subject = "Payment Receipt - Sunrise Dental Clinic (Inv: " + billId + ")";
            String message = String.format(
                "Dear %s,\n\nYour payment of $%s for appointment %s has been received.\nYou can view and print your invoice here: %s\n\nThank you for choosing Sunrise Dental Clinic!",
                appointment.getPatient().getName(),
                bill.getGrandTotal().setScale(2, RoundingMode.HALF_UP).toString(),
                appointment.getAppointmentNumber(),
                invoiceUrl
            );
            emailService.sendEmail(email, subject, message);
            bill.setSentEmailMessage(message);
        }

        return bill;
    }

    @Override
    public Optional<Bill> getBillById(Long billId) {
        return billDAO.findById(billId);
    }

    @Override
    public java.util.List<Bill> getAllBills() {
        return billDAO.findAll();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteBill(Long id) {
        billDAO.deleteById(id);
    }
}
