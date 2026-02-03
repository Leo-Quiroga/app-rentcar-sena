package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Category;
import com.autoreserve.backend.domain.repository.CategoryRepository;
import com.autoreserve.backend.dto.category.CategoryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        
        List<CategoryResponse> responses = categories.stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.getImage(),
                        category.getCars() != null ? category.getCars().size() : 0
                ))
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        
        CategoryResponse response = new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getImage(),
                category.getCars() != null ? category.getCars().size() : 0
        );
        
        return ResponseEntity.ok(response);
    }
}