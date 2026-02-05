package com.autoreserve.backend.dto.reservation;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AdminReservationRequest {
    @NotNull(message = "El ID del usuario es requerido")
    private Long userId;
    
    @NotNull(message = "El ID del auto es requerido")
    private Long carId;
    
    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDate startDate;
    
    @NotNull(message = "La fecha de fin es requerida")
    private LocalDate endDate;
    
    @NotNull(message = "La sede de retiro es requerida")
    private Long pickupBranchId;
    
    @NotNull(message = "La sede de entrega es requerida")
    private Long dropoffBranchId;

    public AdminReservationRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public Long getPickupBranchId() { return pickupBranchId; }
    public void setPickupBranchId(Long pickupBranchId) { this.pickupBranchId = pickupBranchId; }
    
    public Long getDropoffBranchId() { return dropoffBranchId; }
    public void setDropoffBranchId(Long dropoffBranchId) { this.dropoffBranchId = dropoffBranchId; }
}