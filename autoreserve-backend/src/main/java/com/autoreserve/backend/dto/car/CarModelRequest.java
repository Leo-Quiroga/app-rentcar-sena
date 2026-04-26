package com.autoreserve.backend.dto.car;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CarModelRequest {

    @NotBlank(message = "La marca es requerida")
    @Size(max = 100)
    private String brand;

    @NotBlank(message = "El modelo es requerido")
    @Size(max = 100)
    private String model;

    @NotNull(message = "El año es requerido")
    @Min(value = 1900) @Max(value = 2030)
    private Integer year;

    @NotNull(message = "El precio por día es requerido")
    @DecimalMin(value = "0.01")
    private BigDecimal pricePerDay;

    private String image;
    private String description;

    @NotNull(message = "La categoría es requerida")
    private Long categoryId;

    @NotNull(message = "La sede es requerida")
    private Long branchId;

    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "Debe registrar al menos 1 unidad")
    @Max(value = 50, message = "Máximo 50 unidades por registro")
    private Integer quantity;

    public CarModelRequest() {}

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
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
