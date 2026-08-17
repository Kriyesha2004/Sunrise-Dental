package com.clinic.service;

import com.clinic.dao.AppointmentDAO;
import com.clinic.dao.BillDAO;
import com.clinic.model.Appointment;
import com.clinic.model.Bill;
import com.clinic.model.Patient;
import com.clinic.service.impl.BillingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BillingServiceTest {

    @Mock
    private AppointmentDAO appointmentDAO;

    @Mock
    private BillDAO billDAO;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private BillingServiceImpl billingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCalculateAndSaveBill_NewBill() {
        // Arrange
        String appNum = "APT9999";
        Patient patient = new Patient("Test Patient", "Colombo", "0771112222", "test@example.com");
        Appointment appointment = new Appointment(
                appNum,
                patient,
                "Dr. Smith",
                "Cleaning",
                LocalDate.now(),
                LocalTime.now(),
                new BigDecimal("30.00"), // consultation fee
                "SCHEDULED"
        );
        appointment.setId(1L);

        when(appointmentDAO.findByAppointmentNumber(appNum)).thenReturn(Optional.of(appointment));
        when(billDAO.findByAppointmentId(1L)).thenReturn(Optional.empty());
        when(billDAO.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Bill bill = billingService.calculateAndSaveBill(appNum);

        // Assert
        assertNotNull(bill);
        assertEquals(appointment, bill.getAppointment());
        assertEquals(new BigDecimal("50.00"), bill.getTreatmentCost()); // Cleaning base
        assertEquals(new BigDecimal("30.00"), bill.getConsultationFee());
        assertEquals(new BigDecimal("80.00"), bill.getTotalCost()); // 50 + 30
        assertEquals(new BigDecimal("8.00"), bill.getTax()); // 10% of 80
        assertEquals(new BigDecimal("88.00"), bill.getGrandTotal()); // 80 + 8
        assertEquals("UNPAID", bill.getPaymentStatus());

        verify(billDAO, times(1)).save(any(Bill.class));
    }

    @Test
    public void testPayBill() {
        // Arrange
        Patient patient = new Patient("Test Patient", "Colombo", "0771112222", "test@example.com");
        Appointment appointment = new Appointment(
                "APT9999",
                patient,
                "Dr. Smith",
                "Cleaning",
                LocalDate.now(),
                LocalTime.now(),
                new BigDecimal("30.00"),
                "SCHEDULED"
        );
        appointment.setId(1L);

        Bill bill = new Bill(
                appointment,
                new BigDecimal("50.00"),
                new BigDecimal("30.00"),
                new BigDecimal("80.00"),
                new BigDecimal("8.00"),
                new BigDecimal("88.00"),
                "UNPAID"
        );
        bill.setId(10L);

        when(billDAO.findById(10L)).thenReturn(Optional.of(bill));
        when(billDAO.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(appointmentDAO.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Bill paidBill = billingService.payBill(10L, "http://localhost:8080");

        // Assert
        assertNotNull(paidBill);
        assertEquals("PAID", paidBill.getPaymentStatus());
        assertEquals("COMPLETED", paidBill.getAppointment().getStatus());
        verify(billDAO, times(1)).save(bill);
        verify(appointmentDAO, times(1)).save(appointment);
    }
}
