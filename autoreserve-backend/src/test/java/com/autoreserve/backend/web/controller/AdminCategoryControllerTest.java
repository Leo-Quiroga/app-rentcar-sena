package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Category;
import com.autoreserve.backend.domain.repository.CategoryRepository;
import com.autoreserve.backend.dto.category.CategoryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
class AdminCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("SUV");
        testCategory.setDescription("Sport Utility Vehicle");
        testCategory.setCarModels(new ArrayList<>());
    }

    @Test
    void getAllCategories_ReturnsAllCategories() throws Exception {
        when(categoryRepository.findAll()).thenReturn(List.of(testCategory));

        mockMvc.perform(get("/api/admin/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("SUV"));
    }

    @Test
    void getCategoryById_ExistingCategory_ReturnsCategory() throws Exception {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        mockMvc.perform(get("/api/admin/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("SUV"));
    }

    @Test
    void getCategoryById_NonExisting_ThrowsException() throws Exception {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/categories/999"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createCategory_ValidData_ReturnsCreated() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Sedan");
        request.setDescription("Sedan cars");

        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        mockMvc.perform(post("/api/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateCategory_ExistingCategory_ReturnsUpdated() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("SUV Updated");
        request.setDescription("Updated description");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        mockMvc.perform(put("/api/admin/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategory_WithNoModels_ReturnsSuccess() throws Exception {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        mockMvc.perform(delete("/api/admin/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteCategory_WithModels_ReturnsBadRequest() throws Exception {
        Category categoryWithModels = new Category();
        categoryWithModels.setId(2L);
        categoryWithModels.setName("SUV");
        categoryWithModels.setCarModels(List.of(new com.autoreserve.backend.domain.entity.CarModel()));

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(categoryWithModels));

        mockMvc.perform(delete("/api/admin/categories/2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteCategory_NonExisting_ReturnsBadRequest() throws Exception {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/admin/categories/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
