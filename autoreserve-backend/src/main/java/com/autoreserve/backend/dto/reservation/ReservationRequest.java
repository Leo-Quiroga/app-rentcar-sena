package com.autoreserve.backend.dto.reservation;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ReservationRequest {
    @NotNull(message = "El auto es requerido")
    private Long carId;
    
    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDate startDate;
    
    @NotNull(message = "La fecha de fin es requerida")
    private LocalDate endDate;

    public ReservationRequest() {}

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}