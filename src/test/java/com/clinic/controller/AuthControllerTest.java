package com.clinic.controller;

import com.clinic.dto.LoginRequest;
import com.clinic.model.User;
import com.clinic.service.AuthService;
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
import java.util.Optional;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("receptionist", "password");
        User dummyUser = new User("receptionist", "password_hash", "RECEPTIONIST", "Dilina Perera");

        when(authService.login("receptionist", "password")).thenReturn(Optional.of(dummyUser));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("receptionist")))
                .andExpect(jsonPath("$.role", is("RECEPTIONIST")))
                .andExpect(jsonPath("$.fullname", is("Dilina Perera")));

        verify(authService, times(1)).login("receptionist", "password");
    }

    @Test
    public void testLogin_Failure() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("wronguser", "wrongpassword");
        when(authService.login("wronguser", "wrongpassword")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));

        verify(authService, times(1)).login("wronguser", "wrongpassword");
    }

    @Test
    public void testGetMe_Authenticated() throws Exception {
        // Arrange
        User dummyUser = new User("receptionist", "password_hash", "RECEPTIONIST", "Dilina Perera");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", dummyUser);

        // Act & Assert
        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("receptionist")))
                .andExpect(jsonPath("$.role", is("RECEPTIONIST")))
                .andExpect(jsonPath("$.fullname", is("Dilina Perera")));
    }

    @Test
    public void testGetMe_NotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Not authenticated"));
    }
}
