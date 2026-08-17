package com.clinic.controller;

import com.clinic.dto.AppointmentRequest;
import com.clinic.model.Appointment;
import com.clinic.model.Patient;
import com.clinic.model.User;
import com.clinic.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AppointmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();
    }

    @Test
    public void testRegisterAppointment_Unauthorized() throws Exception {
        AppointmentRequest request = new AppointmentRequest();
        request.setPatientName("Test Patient");
        request.setPatientContact("0771112222");
        request.setDentistName("Dr. Smith");
        request.setTreatmentType("Cleaning");
        request.setAppointmentDate("2026-08-20");
        request.setAppointmentTime("10:00");
        request.setConsultationFee(new BigDecimal("30.00"));

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRegisterAppointment_Success() throws Exception {
        // Arrange
        User sessionUser = new User("receptionist", "pw", "RECEPTIONIST", "Dilina Perera");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", sessionUser);

        AppointmentRequest request = new AppointmentRequest();
        request.setPatientName("John Doe");
        request.setPatientAddress("Colombo");
        request.setPatientContact("0771234567");
        request.setDentistName("Dr. Smith");
        request.setTreatmentType("Cleaning");
        request.setAppointmentDate("2026-08-20");
        request.setAppointmentTime("10:00");
        request.setConsultationFee(new BigDecimal("30.00"));

        Patient patient = new Patient("John Doe", "Colombo", "0771234567");
        Appointment mockAppointment = new Appointment(
                "APT1234",
                patient,
                "Dr. Smith",
                "Cleaning",
                LocalDate.of(2026, 8, 20),
                LocalTime.of(10, 0),
                new BigDecimal("30.00"),
                "SCHEDULED"
        );

        when(appointmentService.registerAppointment(
                eq("John Doe"), eq("Colombo"), eq("0771234567"),
                eq("Dr. Smith"), eq("Cleaning"), eq("2026-08-20"),
                eq("10:00"), eq(new BigDecimal("30.00"))
        )).thenReturn(mockAppointment);

        // Act & Assert
        mockMvc.perform(post("/api/appointments")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentNumber", is("APT1234")))
                .andExpect(jsonPath("$.patient.name", is("John Doe")))
                .andExpect(jsonPath("$.dentistName", is("Dr. Smith")))
                .andExpect(jsonPath("$.treatmentType", is("Cleaning")));
    }

    @Test
    public void testSearchAppointment_Found() throws Exception {
        User sessionUser = new User("receptionist", "pw", "RECEPTIONIST", "Dilina Perera");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", sessionUser);

        Patient patient = new Patient("John Doe", "Colombo", "0771234567");
        Appointment mockAppointment = new Appointment(
                "APT1234",
                patient,
                "Dr. Smith",
                "Cleaning",
                LocalDate.of(2026, 8, 20),
                LocalTime.of(10, 0),
                new BigDecimal("30.00"),
                "SCHEDULED"
        );

        when(appointmentService.getAppointmentByNumber("APT1234")).thenReturn(Optional.of(mockAppointment));

        mockMvc.perform(get("/api/appointments/APT1234").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentNumber", is("APT1234")))
                .andExpect(jsonPath("$.patient.name", is("John Doe")));
    }

    @Test
    public void testSearchAppointment_NotFound() throws Exception {
        User sessionUser = new User("receptionist", "pw", "RECEPTIONIST", "Dilina Perera");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", sessionUser);

        when(appointmentService.getAppointmentByNumber("APT0000")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/appointments/APT0000").session(session))
                .andExpect(status().isNotFound());
    }
}
