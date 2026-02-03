package com.autoreserve.backend.dto.car;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CarRequest {
    @NotBlank(message = "La marca es requerida")
    @Size(max = 100)
    private String brand;
    
    @NotBlank(message = "El modelo es requerido")
    @Size(max = 100)
    private String model;
    
    @NotNull(message = "El año es requerido")
    @Min(value = 1900, message = "Año inválido")
    @Max(value = 2030, message = "Año inválido")
    private Integer year;
    
    @Size(max = 20)
    private String plate;
    
    @NotNull(message = "El precio por día es requerido")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal pricePerDay;
    
    @NotNull(message = "La categoría es requerida")
    private Long categoryId;
    
    @NotNull(message = "La sede es requerida")
    private Long branchId;
    
    private String image;

    public CarRequest() {}
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }
    
    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}