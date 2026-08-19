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

        // Send Email notification containing the stylized HTML invoice receipt
        String email = appointment.getPatient().getEmail();
        if (email != null && !email.trim().isEmpty()) {
            String subject = "Payment Receipt - Sunrise Dental Clinic (Inv: " + billId + ")";
            String message = String.format(
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Payment Receipt - Sunrise Dental Clinic</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: sans-serif;\n" +
                "            background-color: #f8f9fa;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .invoice-card {\n" +
                "            background: #ffffff;\n" +
                "            border: 1px solid #dee2e6;\n" +
                "            border-radius: 12px;\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            padding: 30px;\n" +
                "            box-shadow: 0 4px 10px rgba(0,0,0,0.05);\n" +
                "        }\n" +
                "        .header {\n" +
                "            border-bottom: 2px solid #0d6efd;\n" +
                "            padding-bottom: 15px;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .header h3 {\n" +
                "            color: #0d6efd;\n" +
                "            margin: 0 0 5px 0;\n" +
                "        }\n" +
                "        .details-box {\n" +
                "            background-color: #f1f3f5;\n" +
                "            border: 1px solid #ced4da;\n" +
                "            border-radius: 8px;\n" +
                "            padding: 15px;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .details-box table {\n" +
                "            width: 100%%;\n" +
                "            border-collapse: collapse;\n" +
                "        }\n" +
                "        .details-box td {\n" +
                "            padding: 5px 0;\n" +
                "            font-size: 0.9rem;\n" +
                "        }\n" +
                "        .table {\n" +
                "            width: 100%%;\n" +
                "            border-collapse: collapse;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .table th, .table td {\n" +
                "            border: 1px solid #dee2e6;\n" +
                "            padding: 10px;\n" +
                "            text-align: left;\n" +
                "            font-size: 0.9rem;\n" +
                "        }\n" +
                "        .table th {\n" +
                "            background-color: #f8f9fa;\n" +
                "        }\n" +
                "        .text-end {\n" +
                "            text-align: right;\n" +
                "        }\n" +
                "        .stamp-paid {\n" +
                "            background-color: #d1e7dd;\n" +
                "            color: #0f5132;\n" +
                "            border: 2px solid #badbcc;\n" +
                "            font-weight: bold;\n" +
                "            padding: 8px 16px;\n" +
                "            border-radius: 6px;\n" +
                "            display: inline-block;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"invoice-card\">\n" +
                "        <div class=\"header\">\n" +
                "            <h3>Sunrise Dental Clinic</h3>\n" +
                "            <p style=\"margin: 0; font-size: 0.85rem; color: #6c757d;\">\n" +
                "                No. 45 Galle Road, Colombo 03, Sri Lanka<br/>\n" +
                "                Tel: +94 11 234 5678 | Email: info@sunrisedental.lk\n" +
                "            </p>\n" +
                "        </div>\n" +
                "        <h4 style=\"margin-top: 0; color: #495057;\">PAYMENT RECEIPT (INV-%d)</h4>\n" +
                "        <div class=\"details-box\">\n" +
                "            <table>\n" +
                "                <tr>\n" +
                "                    <td><strong>Patient Name:</strong></td>\n" +
                "                    <td>%s</td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td><strong>Email Address:</strong></td>\n" +
                "                    <td>%s</td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td><strong>Appointment No:</strong></td>\n" +
                "                    <td style=\"color: #0d6efd; font-weight: bold;\">%s</td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td><strong>Treatment Type:</strong></td>\n" +
                "                    <td>%s</td>\n" +
                "                </tr>\n" +
                "            </table>\n" +
                "        </div>\n" +
                "        <table class=\"table\">\n" +
                "            <thead>\n" +
                "                <tr>\n" +
                "                    <th>Service Description</th>\n" +
                "                    <th class=\"text-end\" style=\"width: 120px;\">Amount</th>\n" +
                "                </tr>\n" +
                "            </thead>\n" +
                "            <tbody>\n" +
                "                <tr>\n" +
                "                    <td>Dental Treatment: <strong>%s</strong> (Base Fee)</td>\n" +
                "                    <td class=\"text-end\">$%s</td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td>Dentist Consultation Fee</td>\n" +
                "                    <td class=\"text-end\">$%s</td>\n" +
                "                </tr>\n" +
                "                <tr style=\"font-weight: bold;\">\n" +
                "                    <td class=\"text-end\">Subtotal:</td>\n" +
                "                    <td class=\"text-end\">$%s</td>\n" +
                "                </tr>\n" +
                "                <tr style=\"color: #6c757d;\">\n" +
                "                    <td class=\"text-end\">Government Service Tax (10.0%%):</td>\n" +
                "                    <td class=\"text-end\">$%s</td>\n" +
                "                </tr>\n" +
                "                <tr style=\"font-weight: bold; background-color: #e9ecef;\">\n" +
                "                    <td class=\"text-end\" style=\"font-size: 1.1rem;\">Total Paid (USD):</td>\n" +
                "                    <td class=\"text-end text-primary\" style=\"font-size: 1.1rem;\">$%s</td>\n" +
                "                </tr>\n" +
                "            </tbody>\n" +
                "        </table>\n" +
                "        <div style=\"margin-top: 30px; display: flex; justify-content: space-between; align-items: center;\">\n" +
                "            <div>\n" +
                "                <span class=\"stamp-paid\">PAID</span>\n" +
                "            </div>\n" +
                "            <div style=\"text-align: right;\">\n" +
                "                <span style=\"font-size: 0.85rem; color: #6c757d;\">Thank you for choosing Sunrise Dental!</span>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>",
                bill.getId(),
                appointment.getPatient().getName(),
                appointment.getPatient().getEmail(),
                appointment.getAppointmentNumber(),
                appointment.getTreatmentType(),
                appointment.getTreatmentType(),
                bill.getTreatmentCost().setScale(2, RoundingMode.HALF_UP).toString(),
                bill.getConsultationFee().setScale(2, RoundingMode.HALF_UP).toString(),
                bill.getTotalCost().setScale(2, RoundingMode.HALF_UP).toString(),
                bill.getTax().setScale(2, RoundingMode.HALF_UP).toString(),
                bill.getGrandTotal().setScale(2, RoundingMode.HALF_UP).toString()
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
