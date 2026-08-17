package com.clinic.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(name = "treatment_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal treatmentCost;

    @Column(name = "consultation_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;

    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tax;

    @Column(name = "grand_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal grandTotal;

    @Column(name = "bill_date", nullable = false, insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime billDate;

    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus = "UNPAID"; // UNPAID, PAID

    @Transient
    private String sentEmailMessage;

    // Constructors
    public Bill() {}

    public Bill(Appointment appointment, BigDecimal treatmentCost, BigDecimal consultationFee,
                BigDecimal totalCost, BigDecimal tax, BigDecimal grandTotal, String paymentStatus) {
        this.appointment = appointment;
        this.treatmentCost = treatmentCost;
        this.consultationFee = consultationFee;
        this.totalCost = totalCost;
        this.tax = tax;
        this.grandTotal = grandTotal;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
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

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getSentEmailMessage() {
        return sentEmailMessage;
    }

    public void setSentEmailMessage(String sentEmailMessage) {
        this.sentEmailMessage = sentEmailMessage;
    }
}
