package com.autoreserve.backend.dto.car;

public class CarUnitResponse {
    private Long id;
    private Long carModelId;
    private String brand;
    private String model;
    private Integer year;
    private String plate;
    private String color;
    private String status;
    private String branchName;
    private Long branchId;
    private String notes;

    public CarUnitResponse() {}

    public CarUnitResponse(Long id, Long carModelId, String brand, String model,
                           Integer year, String plate, String color,
                           String status, String branchName, Long branchId, String notes) {
        this.id = id;
        this.carModelId = carModelId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.color = color;
        this.status = status;
        this.branchName = branchName;
        this.branchId = branchId;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCarModelId() { return carModelId; }
    public void setCarModelId(Long carModelId) { this.carModelId = carModelId; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
