package com.autoreserve.backend.dto.car;

import java.math.BigDecimal;

public class CarResponse {
    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private String plate;
    private BigDecimal pricePerDay;
    private String status;
    private String categoryName;
    private String branchName;
    private String image;

    public CarResponse(Long id, String brand, String model, Integer year, String plate, 
                      BigDecimal pricePerDay, String status, String categoryName, 
                      String branchName, String image) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.pricePerDay = pricePerDay;
        this.status = status;
        this.categoryName = categoryName;
        this.branchName = branchName;
        this.image = image;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}