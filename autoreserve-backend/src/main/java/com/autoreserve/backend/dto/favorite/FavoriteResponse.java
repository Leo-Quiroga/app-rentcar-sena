package com.autoreserve.backend.dto.favorite;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de modelos favoritos del usuario
 */
public class FavoriteResponse {
    
    private Long favoriteId;
    private Long carModelId;
    private String brand;
    private String model;
    private Integer year;
    private BigDecimal pricePerDay;
    private String image;
    private String description;
    private String categoryName;
    private Long availableUnits;
    private Long totalUnits;
    private LocalDateTime addedAt;
    private boolean isFavorite; // Siempre true en lista de favoritos

    public FavoriteResponse() {}

    public FavoriteResponse(Long favoriteId, Long carModelId, String brand, String model, 
                           Integer year, BigDecimal pricePerDay, String image, String description,
                           String categoryName, Long availableUnits, Long totalUnits, 
                           LocalDateTime addedAt) {
        this.favoriteId = favoriteId;
        this.carModelId = carModelId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.pricePerDay = pricePerDay;
        this.image = image;
        this.description = description;
        this.categoryName = categoryName;
        this.availableUnits = availableUnits;
        this.totalUnits = totalUnits;
        this.addedAt = addedAt;
        this.isFavorite = true;
    }

    // Getters y Setters
    public Long getFavoriteId() { return favoriteId; }
    public void setFavoriteId(Long favoriteId) { this.favoriteId = favoriteId; }

    public Long getCarModelId() { return carModelId; }
    public void setCarModelId(Long carModelId) { this.carModelId = carModelId; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Long getAvailableUnits() { return availableUnits; }
    public void setAvailableUnits(Long availableUnits) { this.availableUnits = availableUnits; }

    public Long getTotalUnits() { return totalUnits; }
    public void setTotalUnits(Long totalUnits) { this.totalUnits = totalUnits; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}