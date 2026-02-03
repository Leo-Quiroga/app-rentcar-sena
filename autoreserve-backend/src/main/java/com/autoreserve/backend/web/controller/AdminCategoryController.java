package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Category;
import com.autoreserve.backend.domain.repository.CategoryRepository;
import com.autoreserve.backend.dto.category.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;

    public AdminCategoryController(CategoryRepository categoryRepository) {
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

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImage(request.getImage());

        Category saved = categoryRepository.save(category);

        CategoryResponse response = new CategoryResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getImage(),
                0
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImage(request.getImage());

        Category updated = categoryRepository.save(category);

        CategoryResponse response = new CategoryResponse(
                updated.getId(),
                updated.getName(),
                updated.getDescription(),
                updated.getImage(),
                updated.getCars() != null ? updated.getCars().size() : 0
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (category.getCars() != null && !category.getCars().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("No se puede eliminar una categoría que tiene autos asociados");
        }

        categoryRepository.delete(category);
        return ResponseEntity.ok("Categoría eliminada correctamente");
    }
}