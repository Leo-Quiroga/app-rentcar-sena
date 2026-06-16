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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarRepository carRepository;

    @MockitoBean
    private CarModelRepository carModelRepository;

    private CarModel testCarModel;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("SUV");

        testCarModel = new CarModel();
        testCarModel.setId(1L);
        testCarModel.setBrand("Toyota");
        testCarModel.setModel("RAV4");
        testCarModel.setYear(2023);
        testCarModel.setPricePerDay(new BigDecimal("50.00"));
        testCarModel.setCategory(testCategory);
        testCarModel.setDescription("Spacious and reliable SUV perfect for family trips and city driving");
        testCarModel.setImage(TestImageUrls.getImageUrl("Toyota", "RAV4"));
    }

    @Test
    void getAvailableModels_NoFilters_ReturnsAllModels() throws Exception {
        when(carModelRepository.findModelsWithAvailableUnits(null))
                .thenReturn(List.of(testCarModel));
        when(carRepository.countAvailableByModel(1L)).thenReturn(3L);
        when(carRepository.countByCarModelId(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("RAV4"))
                .andExpect(jsonPath("$[0].categoryName").value("SUV"));
    }

    @Test
    void getAvailableModels_WithCategoryFilter_ReturnsFilteredModels() throws Exception {
        when(carModelRepository.findModelsWithAvailableUnits(eq(1L)))
                .thenReturn(List.of(testCarModel));
        when(carRepository.countAvailableByModel(1L)).thenReturn(2L);
        when(carRepository.countByCarModelId(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/cars?categoryId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("SUV"));
    }

    @Test
    void getModelById_ExistingModel_ReturnsModel() throws Exception {
        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));
        when(carRepository.countAvailableByModel(1L)).thenReturn(3L);
        when(carRepository.countByCarModelId(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brand").value("Toyota"));
    }

    @Test
    void getModelById_NonExistingModel_ThrowsException() throws Exception {
        when(carModelRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cars/999"))
                .andExpect(status().is4xxClientError());
    }
}