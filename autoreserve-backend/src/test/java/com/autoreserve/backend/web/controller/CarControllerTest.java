package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarRepository carRepository;

    private Car testCar;
    private Category testCategory;
    private Branch testBranch;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("SUV");

        testBranch = new Branch();
        testBranch.setId(1L);
        testBranch.setName("Sede Central");

        testCar = new Car();
        testCar.setId(1L);
        testCar.setBrand("Toyota");
        testCar.setModel("RAV4");
        testCar.setYear(2023);
        testCar.setPlate("ABC123");
        testCar.setPricePerDay(new BigDecimal("50.00"));
        testCar.setStatus(CarStatus.AVAILABLE);
        testCar.setCategory(testCategory);
        testCar.setBranch(testBranch);
    }

    @Test
    void getAvailableCars_NoFilters_ReturnsAllCars() throws Exception {
        when(carRepository.findByStatus(eq(CarStatus.AVAILABLE), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(testCar)));

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("RAV4"));
    }

    @Test
    void getAvailableCars_WithCategoryFilter_ReturnsFilteredCars() throws Exception {
        when(carRepository.findByCategoryIdAndStatus(eq(1L), eq(CarStatus.AVAILABLE), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(testCar)));

        mockMvc.perform(get("/api/cars?categoryId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("SUV"));
    }

    @Test
    void getCarById_ExistingCar_ReturnsCar() throws Exception {
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brand").value("Toyota"));
    }

    @Test
    void getCarById_NonExistingCar_ThrowsException() throws Exception {
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cars/999"))
                .andExpect(status().is4xxClientError());
    }
}
