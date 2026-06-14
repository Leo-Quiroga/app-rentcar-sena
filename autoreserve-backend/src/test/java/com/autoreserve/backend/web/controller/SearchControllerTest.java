package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.CarModelRepository;
import com.autoreserve.backend.util.TestImageUrls;
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

    @MockBean
    private CarModelRepository carModelRepository;

    private CarModel testCarModel;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setId(1L);
        category.setName("SUV");

        testCarModel = new CarModel();
        testCarModel.setId(1L);
        testCarModel.setBrand("Toyota");
        testCarModel.setModel("RAV4");
        testCarModel.setYear(2023);
        testCarModel.setPricePerDay(new BigDecimal("50.00"));
        testCarModel.setCategory(category);
        testCarModel.setDescription("Spacious and reliable SUV perfect for family trips and city driving");
        testCarModel.setImage(TestImageUrls.getImageUrl("Toyota", "RAV4"));
    }

    @Test
    void searchAvailableModels_WithDates_ReturnsAvailableModels() throws Exception {
        when(carModelRepository.findAvailableModels(any(LocalDate.class), any(LocalDate.class), eq(null)))
                .thenReturn(List.of(testCarModel));
        when(carRepository.findAvailableUnitForModel(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());
        when(carRepository.countByCarModelId(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/search/cars")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-01-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("Toyota"));
    }

    @Test
    void searchAvailableModels_WithCategoryFilter_ReturnsFilteredModels() throws Exception {
        when(carModelRepository.findAvailableModels(any(LocalDate.class), any(LocalDate.class), eq(1L)))
                .thenReturn(List.of(testCarModel));
        when(carRepository.findAvailableUnitForModel(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());
        when(carRepository.countByCarModelId(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/search/cars")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-01-05")
                .param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("SUV"));
    }

    @Test
    void searchAvailableModels_InvalidDateRange_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/search/cars")
                .param("startDate", "2024-01-05")
                .param("endDate", "2024-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}