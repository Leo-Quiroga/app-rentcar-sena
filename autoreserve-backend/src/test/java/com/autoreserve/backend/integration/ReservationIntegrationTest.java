package com.autoreserve.backend.integration;

import com.autoreserve.backend.dto.auth.LoginRequest;
import com.autoreserve.backend.dto.auth.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getReservations_WithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/reservations/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getReservations_WithValidToken_ReturnsOk() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setEmail("reservationtest@test.com");
        register.setPassword("password123");
        register.setFirstName("Reservation");
        register.setLastName("Test");
        register.setPhone("123456789");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest();
        login.setEmail("reservationtest@test.com");
        login.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String response = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(response).path("data").path("token").asText();

        mockMvc.perform(get("/api/reservations/my")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpoint_WithClientToken_ReturnsForbidden() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setEmail("clientrbac@test.com");
        register.setPassword("password123");
        register.setFirstName("Client");
        register.setLastName("RBAC");
        register.setPhone("987654321");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest();
        login.setEmail("clientrbac@test.com");
        login.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String response = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(response).path("data").path("token").asText();

        mockMvc.perform(get("/api/admin/stats")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
