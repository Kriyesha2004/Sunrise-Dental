package com.clinic.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class AppointmentRequest {

    @NotBlank(message = "Patient name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String patientName;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String patientAddress;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9+\\-\\s()]{7,20}$", message = "Please enter a valid phone number")
    private String patientContact;

    @NotBlank(message = "Patient email is required")
    @Email(message = "Please enter a valid email address")
    private String patientEmail;

    @NotBlank(message = "Dentist name is required")
    private String dentistName;

    @NotBlank(message = "Treatment type is required")
    private String treatmentType;

    @NotBlank(message = "Appointment date is required")
    private String appointmentDate; // YYYY-MM-DD

    @NotBlank(message = "Appointment time is required")
    private String appointmentTime; // HH:MM

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.00", message = "Fee cannot be negative")
    private BigDecimal consultationFee;

    // Constructors
    public AppointmentRequest() {}

    // Getters and Setters
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public String getPatientContact() {
        return patientContact;
    }

    public void setPatientContact(String patientContact) {
        this.patientContact = patientContact;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }
}
