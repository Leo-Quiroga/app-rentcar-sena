package com.autoreserve.backend.dto.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReservationResponse {
    private Long id;
    private Long carId;
    private String carBrand;
    private String carModel;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private BigDecimal totalAmount;
    private String branchName;

    public ReservationResponse(Long id, Long carId, String carBrand, String carModel, 
                              LocalDate startDate, LocalDate endDate, String status, 
                              BigDecimal totalAmount, String branchName) {
        this.id = id;
        this.carId = carId;
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.branchName = branchName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    
    public String getCarBrand() { return carBrand; }
    public void setCarBrand(String carBrand) { this.carBrand = carBrand; }
    
    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
}