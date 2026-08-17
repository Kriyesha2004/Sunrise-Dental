package com.clinic.controller;

import com.clinic.dto.BillResponse;
import com.clinic.model.Bill;
import com.clinic.model.User;
import com.clinic.service.BillingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    public ResponseEntity<?> payBill(@PathVariable Long billId, HttpSession session, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please login first");
        }

        try {
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String contextPath = request.getContextPath();
            String baseUrl = scheme + "://" + serverName + ":" + serverPort + contextPath;
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }

            Bill bill = billingService.payBill(billId, baseUrl);
            return ResponseEntity.ok(mapToResponse(bill));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment: " + e.getMessage());
        }
    }

    @GetMapping(value = "/public/invoice/{billId}", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getPublicInvoiceHtml(@PathVariable Long billId) {
        Optional<Bill> billOpt = billingService.getBillById(billId);
        if (billOpt.isEmpty()) {
            return "<html><body><h2 style='text-align:center; margin-top:50px; font-family:sans-serif;'>Invoice not found</h2></body></html>";
        }
        Bill bill = billOpt.get();
        String statusClass = "PAID".equalsIgnoreCase(bill.getPaymentStatus()) ? "stamp-paid" : "stamp-unpaid";
        
        return String.format(
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Invoice Receipt - Sunrise Dental Clinic</title>\n" +
            "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
            "    <link href=\"https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;700;800&display=swap\" rel=\"stylesheet\">\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: 'Outfit', sans-serif;\n" +
            "            background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);\n" +
            "            min-height: 100vh;\n" +
            "            display: flex;\n" +
            "            align-items: center;\n" +
            "            justify-content: center;\n" +
            "            padding: 20px;\n" +
            "        }\n" +
            "        .invoice-card {\n" +
            "            background: rgba(255, 255, 255, 0.85);\n" +
            "            backdrop-filter: blur(10px);\n" +
            "            border: 1px solid rgba(255, 255, 255, 0.6);\n" +
            "            border-radius: 16px;\n" +
            "            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);\n" +
            "            max-width: 800px;\n" +
            "            width: 100%;\n" +
            "            padding: 40px;\n" +
            "        }\n" +
            "        .header-title {\n" +
            "            color: #0d6efd;\n" +
            "            font-weight: 700;\n" +
            "        }\n" +
            "        .invoice-title {\n" +
            "            font-size: 1.5rem;\n" +
            "            font-weight: 600;\n" +
            "            color: #495057;\n" +
            "        }\n" +
            "        .stamp-paid {\n" +
            "            background-color: #d1e7dd;\n" +
            "            color: #0f5132;\n" +
            "            border: 2px solid #badbcc;\n" +
            "            font-weight: 700;\n" +
            "            font-size: 1.1rem;\n" +
            "            display: inline-block;\n" +
            "            padding: 8px 24px;\n" +
            "            border-radius: 8px;\n" +
            "            transform: rotate(-5deg);\n" +
            "            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);\n" +
            "        }\n" +
            "        .stamp-unpaid {\n" +
            "            background-color: #fff3cd;\n" +
            "            color: #664d03;\n" +
            "            border: 2px solid #ffecb5;\n" +
            "            font-weight: 700;\n" +
            "            font-size: 1.1rem;\n" +
            "            display: inline-block;\n" +
            "            padding: 8px 24px;\n" +
            "            border-radius: 8px;\n" +
            "            transform: rotate(-5deg);\n" +
            "            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);\n" +
            "        }\n" +
            "        .btn-print {\n" +
            "            border-radius: 8px;\n" +
            "            font-weight: 600;\n" +
            "            padding: 10px 24px;\n" +
            "            transition: all 0.3s ease;\n" +
            "        }\n" +
            "        .btn-print:hover {\n" +
            "            transform: translateY(-2px);\n" +
            "            box-shadow: 0 4px 12px rgba(13, 110, 253, 0.2);\n" +
            "        }\n" +
            "        @media print {\n" +
            "            body {\n" +
            "                background: none;\n" +
            "                padding: 0;\n" +
            "            }\n" +
            "            .invoice-card {\n" +
            "                box-shadow: none;\n" +
            "                border: none;\n" +
            "                padding: 0;\n" +
            "            }\n" +
            "            .btn-print-container {\n" +
            "                display: none !important;\n" +
            "            }\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"invoice-card\">\n" +
            "        <!-- Invoice Header -->\n" +
            "        <div class=\"row mb-4 border-bottom pb-4 align-items-center\">\n" +
            "            <div class=\"col-md-6 col-12 text-md-start text-center\">\n" +
            "                <h3 class=\"header-title mb-1\">Sunrise Dental Clinic</h3>\n" +
            "                <p class=\"text-muted mb-0\" style=\"font-size: 0.9rem;\">\n" +
            "                    No. 45 Galle Road, Colombo 03, Sri Lanka<br />\n" +
            "                    Tel: +94 11 234 5678 | Email: info@sunrisedental.lk\n" +
            "                </p>\n" +
            "            </div>\n" +
            "            <div class=\"col-md-6 col-12 text-md-end text-center mt-md-0 mt-3\">\n" +
            "                <h4 class=\"invoice-title mb-1\">INVOICE / RECEIPT</h4>\n" +
            "                <span class=\"text-muted\" style=\"font-size: 0.85rem;\">Invoice ID: <strong>INV-%d</strong></span><br />\n" +
            "                <span class=\"text-muted\" style=\"font-size: 0.85rem;\">Date: %s</span>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "\n" +
            "        <!-- Patient & Appointment Details -->\n" +
            "        <div class=\"row mb-4 bg-light p-3 rounded mx-0 border border-secondary-subtle\">\n" +
            "            <div class=\"col-md-6 mb-2\">\n" +
            "                <span class=\"text-muted d-block\" style=\"font-size: 0.75rem; letter-spacing: 0.5px;\">PATIENT NAME</span>\n" +
            "                <span class=\"fw-bold text-dark\">%s</span>\n" +
            "            </div>\n" +
            "            <div class=\"col-md-6 mb-2 text-md-end\">\n" +
            "                <span class=\"text-muted d-block\" style=\"font-size: 0.75rem; letter-spacing: 0.5px;\">EMAIL ADDRESS</span>\n" +
            "                <span class=\"fw-bold text-dark\">%s</span>\n" +
            "            </div>\n" +
            "            <div class=\"col-md-6 mb-2\">\n" +
            "                <span class=\"text-muted d-block\" style=\"font-size: 0.75rem; letter-spacing: 0.5px;\">APPOINTMENT NO.</span>\n" +
            "                <span class=\"fw-bold text-primary\">%s</span>\n" +
            "            </div>\n" +
            "            <div class=\"col-md-6 mb-2 text-md-end\">\n" +
            "                <span class=\"text-muted d-block\" style=\"font-size: 0.75rem; letter-spacing: 0.5px;\">TREATMENT TYPE</span>\n" +
            "                <span class=\"fw-semibold text-dark\">%s</span>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "\n" +
            "        <!-- Calculations Table -->\n" +
            "        <div class=\"table-responsive mb-4\">\n" +
            "            <table class=\"table table-bordered align-middle\">\n" +
            "                <thead class=\"table-light\">\n" +
            "                    <tr>\n" +
            "                        <th>Service Description</th>\n" +
            "                        <th class=\"text-end\" style=\"width: 180px;\">Amount (USD)</th>\n" +
            "                    </tr>\n" +
            "                </thead>\n" +
            "                <tbody>\n" +
            "                    <tr>\n" +
            "                        <td>Dental Treatment: <strong>%s</strong> (Base Fee)</td>\n" +
            "                        <td class=\"text-end fw-semibold\">$%s</td>\n" +
            "                    </tr>\n" +
            "                    <tr>\n" +
            "                        <td>Dentist Consultation Fee</td>\n" +
            "                        <td class=\"text-end fw-semibold\">$%s</td>\n" +
            "                    </tr>\n" +
            "                    <tr class=\"table-light\">\n" +
            "                        <td class=\"text-end fw-bold\">Subtotal:</td>\n" +
            "                        <td class=\"text-end fw-bold\">$%s</td>\n" +
            "                    </tr>\n" +
            "                    <tr>\n" +
            "                        <td class=\"text-end text-muted\">Government Service Tax (10.0%):</td>\n" +
            "                        <td class=\"text-end text-muted fw-semibold\">$%s</td>\n" +
            "                    </tr>\n" +
            "                    <tr class=\"table-secondary\">\n" +
            "                        <td class=\"text-end fw-bold h5 mb-0\">Total Paid:</td>\n" +
            "                        <td class=\"text-end fw-extrabold h5 mb-0 text-primary\">$%s</td>\n" +
            "                    </tr>\n" +
            "                </tbody>\n" +
            "            </table>\n" +
            "        </div>\n" +
            "\n" +
            "        <!-- Footer / Signature / Status -->\n" +
            "        <div class=\"row align-items-center mt-4\">\n" +
            "            <div class=\"col-6\">\n" +
            "                <span class=\"text-muted d-block mb-1\" style=\"font-size: 0.75rem;\">INVOICE STATUS</span>\n" +
            "                <div class=\"%s\">%s</div>\n" +
            "            </div>\n" +
            "            <div class=\"col-6 text-end\">\n" +
            "                <p class=\"text-muted mb-0\" style=\"font-size: 0.75rem;\">Authorized Signature</p>\n" +
            "                <div class=\"mt-3 border-top border-dark border-opacity-50 pt-1 d-inline-block\" style=\"width: 150px; font-size: 0.8rem;\">\n" +
            "                    Sunrise Dental Staff\n" +
            "                </div>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "\n" +
            "        <!-- Print Action Button (not printed) -->\n" +
            "        <div class=\"text-center mt-5 btn-print-container\">\n" +
            "            <button onclick=\"window.print()\" class=\"btn btn-primary btn-print\">\n" +
            "                Print Invoice / Receipt\n" +
            "            </button>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>",
            bill.getId(),
            bill.getBillDate().toString().replace("T", " ").substring(0, 19),
            bill.getAppointment().getPatient().getName(),
            bill.getAppointment().getPatient().getEmail(),
            bill.getAppointment().getAppointmentNumber(),
            bill.getAppointment().getTreatmentType(),
            bill.getAppointment().getTreatmentType(),
            bill.getTreatmentCost().setScale(2).toString(),
            bill.getConsultationFee().setScale(2).toString(),
            bill.getTotalCost().setScale(2).toString(),
            bill.getTax().setScale(2).toString(),
            bill.getGrandTotal().setScale(2).toString(),
            statusClass,
            bill.getPaymentStatus()
        );
    }

    private BillResponse mapToResponse(Bill bill) {
        return new BillResponse(
                bill.getId(),
                bill.getAppointment().getAppointmentNumber(),
                bill.getAppointment().getPatient().getName(),
                bill.getAppointment().getPatient().getEmail(),
                bill.getAppointment().getTreatmentType(),
                bill.getTreatmentCost(),
                bill.getConsultationFee(),
                bill.getTotalCost(),
                bill.getTax(),
                bill.getGrandTotal(),
                bill.getPaymentStatus(),
                bill.getBillDate(),
                bill.getSentEmailMessage()
        );
    }
}
