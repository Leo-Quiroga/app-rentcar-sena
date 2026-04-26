package com.autoreserve.backend.dto.car;

import java.math.BigDecimal;

/**
 * Respuesta del catálogo público: modelo de vehículo con cantidad disponible.
 * El cliente ve el modelo, no las unidades individuales.
 */
public class CarModelResponse {
    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private BigDecimal pricePerDay;
    private String image;
    private String description;
    private String categoryName;
    private Long categoryId;
    private long availableUnits;
    private long totalUnits;

    public CarModelResponse() {}

    public CarModelResponse(Long id, String brand, String model, Integer year,
                            BigDecimal pricePerDay, String image, String description,
                            String categoryName, Long categoryId,
                            long availableUnits, long totalUnits) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.pricePerDay = pricePerDay;
        this.image = image;
        this.description = description;
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.availableUnits = availableUnits;
        this.totalUnits = totalUnits;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public long getAvailableUnits() { return availableUnits; }
    public void setAvailableUnits(long availableUnits) { this.availableUnits = availableUnits; }
    public long getTotalUnits() { return totalUnits; }
    public void setTotalUnits(long totalUnits) { this.totalUnits = totalUnits; }
}
