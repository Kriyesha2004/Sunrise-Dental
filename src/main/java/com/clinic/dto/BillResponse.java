package com.clinic.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BillResponse {
    private Long billId;
    private String appointmentNumber;
    private String patientName;
    private String treatmentType;
    private BigDecimal treatmentCost;
    private BigDecimal consultationFee;
    private BigDecimal totalCost;
    private BigDecimal tax;
    private BigDecimal grandTotal;
    private String paymentStatus;
    private LocalDateTime billDate;

    // Constructors
    public BillResponse() {}

    public BillResponse(Long billId, String appointmentNumber, String patientName, String treatmentType,
                        BigDecimal treatmentCost, BigDecimal consultationFee, BigDecimal totalCost,
                        BigDecimal tax, BigDecimal grandTotal, String paymentStatus, LocalDateTime billDate) {
        this.billId = billId;
        this.appointmentNumber = appointmentNumber;
        this.patientName = patientName;
        this.treatmentType = treatmentType;
        this.treatmentCost = treatmentCost;
        this.consultationFee = consultationFee;
        this.totalCost = totalCost;
        this.tax = tax;
        this.grandTotal = grandTotal;
        this.paymentStatus = paymentStatus;
        this.billDate = billDate;
    }

    // Getters and Setters
    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }

    public BigDecimal getTreatmentCost() {
        return treatmentCost;
    }

    public void setTreatmentCost(BigDecimal treatmentCost) {
        this.treatmentCost = treatmentCost;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }
}
