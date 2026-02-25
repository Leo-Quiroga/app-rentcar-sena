package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarRepository carRepository;

    private Car testCar;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setId(1L);
        category.setName("SUV");

        Branch branch = new Branch();
        branch.setId(1L);
        branch.setName("Sede Central");

        testCar = new Car();
        testCar.setId(1L);
        testCar.setBrand("Toyota");
        testCar.setModel("RAV4");
        testCar.setYear(2023);
        testCar.setPlate("ABC123");
        testCar.setPricePerDay(new BigDecimal("50.00"));
        testCar.setStatus(CarStatus.AVAILABLE);
        testCar.setCategory(category);
        testCar.setBranch(branch);
    }

    @Test
    void searchAvailableCars_WithDates_ReturnsAvailableCars() throws Exception {
        when(carRepository.findAvailableCars(any(LocalDate.class), any(LocalDate.class), eq(null)))
                .thenReturn(List.of(testCar));

        mockMvc.perform(get("/api/search/cars")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-01-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("Toyota"));
    }

    @Test
    void searchAvailableCars_WithCategoryFilter_ReturnsFilteredCars() throws Exception {
        when(carRepository.findAvailableCars(any(LocalDate.class), any(LocalDate.class), eq(1L)))
                .thenReturn(List.of(testCar));

        mockMvc.perform(get("/api/search/cars")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-01-05")
                .param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("SUV"));
    }
}
