package com.autoreserve.backend.dto.category;

public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private String image;
    private Integer carCount;

    public CategoryResponse(Long id, String name, String description, String image, Integer carCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.carCount = carCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public Integer getCarCount() { return carCount; }
    public void setCarCount(Integer carCount) { this.carCount = carCount; }
}