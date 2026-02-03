package com.autoreserve.backend.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100)
    private String name;
    
    private String description;
    private String image;

    public CategoryRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}