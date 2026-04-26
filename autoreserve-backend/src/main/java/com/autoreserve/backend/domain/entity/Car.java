package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Representa una unidad física de vehículo en el inventario.
 * Cada unidad pertenece a un CarModel y se identifica por placa y color.
 * Estado inicial: PENDING_REGISTRATION (sin placa ni color).
 * Solo puede pasar a AVAILABLE cuando tiene placa Y color registrados.
 */
@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Modelo al que pertenece esta unidad física */
    @ManyToOne(optional = false)
    @JoinColumn(name = "car_model_id", nullable = false)
    private CarModel carModel;

    /** Placa única de la unidad. Null hasta que sea identificada. */
    @Column(unique = true, length = 20)
    private String plate;

    /** Color de la unidad. Null hasta que sea identificada. */
    @Column(length = 50)
    private String color;

    /** Sede donde se encuentra físicamente la unidad */
    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    /** Estado operativo de la unidad */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status;

    /** Notas adicionales sobre la unidad (kilometraje, observaciones, etc.) */
    @Column(length = 500)
    private String notes;

    /** Reservas asociadas a esta unidad */
    @OneToMany(mappedBy = "car")
    private List<Reservation> reservations;

    public Car() {
        this.status = CarStatus.PENDING_REGISTRATION;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CarModel getCarModel() { return carModel; }
    public void setCarModel(CarModel carModel) { this.carModel = carModel; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }

    public CarStatus getStatus() { return status; }
    public void setStatus(CarStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    // Helpers para acceder a datos del modelo desde la unidad
    public String getBrand() { return carModel != null ? carModel.getBrand() : null; }
    public String getModelName() { return carModel != null ? carModel.getModel() : null; }
    public Integer getYear() { return carModel != null ? carModel.getYear() : null; }
    public java.math.BigDecimal getPricePerDay() { return carModel != null ? carModel.getPricePerDay() : null; }
    public String getImage() { return carModel != null ? carModel.getImage() : null; }
    public Category getCategory() { return carModel != null ? carModel.getCategory() : null; }
}
