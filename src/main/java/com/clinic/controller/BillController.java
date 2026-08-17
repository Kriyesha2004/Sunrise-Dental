package com.clinic.controller;

import com.clinic.dto.BillResponse;
import com.clinic.model.Bill;
import com.clinic.model.User;
import com.clinic.service.BillingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillingService billingService;

    @Autowired
    public BillController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/calculate/{appointmentNumber}")
    public ResponseEntity<?> calculateBill(@PathVariable String appointmentNumber, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please login first");
        }

        try {
            Bill bill = billingService.calculateAndSaveBill(appointmentNumber);
            return ResponseEntity.ok(mapToResponse(bill));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error calculating bill: " + e.getMessage());
        }
    }

    @GetMapping("/appointment/{appointmentNumber}")
    public ResponseEntity<?> getBill(@PathVariable String appointmentNumber, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please login first");
        }

        Optional<Bill> billOpt = billingService.getBillByAppointmentNumber(appointmentNumber);
        if (billOpt.isPresent()) {
            return ResponseEntity.ok(mapToResponse(billOpt.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bill not found for appointment: " + appointmentNumber);
    }

    @PostMapping("/pay/{billId}")
    public ResponseEntity<?> payBill(@PathVariable Long billId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please login first");
        }

        try {
            Bill bill = billingService.payBill(billId);
            return ResponseEntity.ok(mapToResponse(bill));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment: " + e.getMessage());
        }
    }

    private BillResponse mapToResponse(Bill bill) {
        return new BillResponse(
                bill.getId(),
                bill.getAppointment().getAppointmentNumber(),
                bill.getAppointment().getPatient().getName(),
                bill.getAppointment().getTreatmentType(),
                bill.getTreatmentCost(),
                bill.getConsultationFee(),
                bill.getTotalCost(),
                bill.getTax(),
                bill.getGrandTotal(),
                bill.getPaymentStatus(),
                bill.getBillDate()
        );
    }
}
