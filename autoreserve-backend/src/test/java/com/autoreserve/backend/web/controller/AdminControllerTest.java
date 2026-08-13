package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.CarStatus;
import com.autoreserve.backend.domain.entity.ReservationStatus;
import com.autoreserve.backend.domain.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CarRepository carRepository;

    @MockitoBean
    private ReservationRepository reservationRepository;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private BranchRepository branchRepository;

    @Test
    void getStats_ReturnsAllStats() throws Exception {
        when(userRepository.count()).thenReturn(10L);
        when(carRepository.count()).thenReturn(20L);
        when(reservationRepository.count()).thenReturn(50L);
        when(categoryRepository.count()).thenReturn(5L);
        when(branchRepository.count()).thenReturn(3L);
        when(carRepository.findByStatus(CarStatus.AVAILABLE)).thenReturn(List.of());
        when(carRepository.findByStatus(CarStatus.RENTED)).thenReturn(List.of());
        when(carRepository.findByStatus(CarStatus.MAINTENANCE)).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(10))
                .andExpect(jsonPath("$.totalCars").value(20))
                .andExpect(jsonPath("$.totalReservations").value(50))
                .andExpect(jsonPath("$.totalCategories").value(5))
                .andExpect(jsonPath("$.totalBranches").value(3));
    }
}
