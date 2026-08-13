package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.CarModel;
import com.autoreserve.backend.domain.entity.Category;
import com.autoreserve.backend.domain.repository.CarModelRepository;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.FavoriteRepository;
import com.autoreserve.backend.util.TestImageUrls;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
class AdminFavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoriteRepository favoriteRepository;

    @MockitoBean
    private CarModelRepository carModelRepository;

    @MockitoBean
    private CarRepository carRepository;

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
        testCarModel.setImage(TestImageUrls.TOYOTA_RAV4);
    }

    @Test
    void getFavoriteStats_ReturnsStats() throws Exception {
        List<Object[]> favoritesByModel = new java.util.ArrayList<>();
        favoritesByModel.add(new Object[]{1L, 5L});
        when(favoriteRepository.countFavoritesByCarModel()).thenReturn(favoritesByModel);
        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));
        when(carRepository.countAvailableByModel(1L)).thenReturn(3L);
        when(carRepository.countByCarModelId(1L)).thenReturn(5L);
        when(favoriteRepository.count()).thenReturn(5L);
        when(carModelRepository.count()).thenReturn(10L);

        mockMvc.perform(get("/api/admin/favorites/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.generalStats.totalFavorites").value(5));
    }

    @Test
    void getFavoriteStats_SkipsMissingCarModels() throws Exception {
        List<Object[]> favoritesByModel = new java.util.ArrayList<>();
        favoritesByModel.add(new Object[]{1L, 5L});
        favoritesByModel.add(new Object[]{2L, 3L});
        when(favoriteRepository.countFavoritesByCarModel()).thenReturn(favoritesByModel);
        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));
        when(carModelRepository.findById(2L)).thenReturn(Optional.empty());
        when(carRepository.countAvailableByModel(1L)).thenReturn(3L);
        when(carRepository.countByCarModelId(1L)).thenReturn(5L);
        when(favoriteRepository.count()).thenReturn(8L);
        when(carModelRepository.count()).thenReturn(12L);

        mockMvc.perform(get("/api/admin/favorites/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.modelStats[0].carModelId").value(1));
    }

    @Test
    void getTopFavoriteModels_ReturnsTopModels() throws Exception {
        List<Object[]> favoritesByModel = new java.util.ArrayList<>();
        favoritesByModel.add(new Object[]{1L, 10L});
        when(favoriteRepository.countFavoritesByCarModel()).thenReturn(favoritesByModel);
        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));

        mockMvc.perform(get("/api/admin/favorites/top-models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.topModels[0].brand").value("Toyota"));
    }

    @Test
    void getTopFavoriteModels_SkipsMissingCarModels() throws Exception {
        List<Object[]> favoritesByModel = new java.util.ArrayList<>();
        favoritesByModel.add(new Object[]{1L, 10L});
        favoritesByModel.add(new Object[]{2L, 5L});
        when(favoriteRepository.countFavoritesByCarModel()).thenReturn(favoritesByModel);
        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));
        when(carModelRepository.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/favorites/top-models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.topModels[0].brand").value("Toyota"));
    }

    @Test
    void getUsersWithFavoriteModel_ExistingModel_ReturnsUsers() throws Exception {
        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));
        when(favoriteRepository.findUsersByFavoriteCarModel(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/favorites/model/1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    void getUsersWithFavoriteModel_NonExistingModel_ReturnsBadRequest() throws Exception {
        when(carModelRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/favorites/model/999/users"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
