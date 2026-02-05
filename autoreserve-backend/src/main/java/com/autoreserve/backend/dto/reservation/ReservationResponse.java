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
    private String pickupBranchName;
    private String dropoffBranchName;
    private String paymentStatus;
    private Integer totalDays;
    private BigDecimal pricePerDay;
    private String carImage;
    private Integer carYear;
    private String categoryName;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;

    public ReservationResponse(Long id, Long carId, String carBrand, String carModel, Integer carYear,
                              String carImage, String categoryName, LocalDate startDate, LocalDate endDate, 
                              String status, String paymentStatus, BigDecimal totalAmount, Integer totalDays,
                              BigDecimal pricePerDay, String pickupBranchName, String dropoffBranchName,
                              Long userId, String userFirstName, String userLastName, String userEmail) {
        this.id = id;
        this.carId = carId;
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.carYear = carYear;
        this.carImage = carImage;
        this.categoryName = categoryName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
        this.totalDays = totalDays;
        this.pricePerDay = pricePerDay;
        this.pickupBranchName = pickupBranchName;
        this.dropoffBranchName = dropoffBranchName;
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
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
    
    public String getPickupBranchName() { return pickupBranchName; }
    public void setPickupBranchName(String pickupBranchName) { this.pickupBranchName = pickupBranchName; }
    
    public String getDropoffBranchName() { return dropoffBranchName; }
    public void setDropoffBranchName(String dropoffBranchName) { this.dropoffBranchName = dropoffBranchName; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public Integer getTotalDays() { return totalDays; }
    public void setTotalDays(Integer totalDays) { this.totalDays = totalDays; }
    
    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }
    
    public String getCarImage() { return carImage; }
    public void setCarImage(String carImage) { this.carImage = carImage; }
    
    public Integer getCarYear() { return carYear; }
    public void setCarYear(Integer carYear) { this.carYear = carYear; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserFirstName() { return userFirstName; }
    public void setUserFirstName(String userFirstName) { this.userFirstName = userFirstName; }
    
    public String getUserLastName() { return userLastName; }
    public void setUserLastName(String userLastName) { this.userLastName = userLastName; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}