package com.autoreserve.backend.dto.car;

public class CarUnitUpdateRequest {
    private String plate;
    private String color;
    private String status;
    private Long branchId;
    private String notes;

    public CarUnitUpdateRequest() {}

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
