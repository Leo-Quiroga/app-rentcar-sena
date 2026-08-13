package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.Category;
import com.autoreserve.backend.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("SUV");
        testCategory.setDescription("Sport Utility Vehicle");
    }

    @Test
    void save_ReturnsSavedCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.save(testCategory);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("SUV", result.getName());
        verify(categoryRepository, times(1)).save(testCategory);
    }

    @Test
    void findAll_ReturnsAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(testCategory));

        List<Category> result = categoryService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SUV", result.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void findById_ExistingCategory_ReturnsOptional() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        Optional<Category> result = categoryService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NonExistingCategory_ReturnsEmpty() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.findById(999L);

        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    void deleteById_CallsRepository() {
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteById(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }
}
