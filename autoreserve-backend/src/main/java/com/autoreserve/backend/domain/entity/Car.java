package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Entidad que representa un vehículo dentro del sistema de inventario.
 * Mapea la tabla "car" y gestiona las especificaciones técnicas, costos y disponibilidad.
 */
@Entity
@Table(name = "car")
public class Car {

    /**
     * Identificador único del vehículo.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Marca del fabricante del vehículo.
     */
    @Column(nullable = false, length = 100)
    private String brand;

    /**
     * Modelo específico del vehículo.
     */
    @Column(nullable = false, length = 100)
    private String model;

    /**
     * Año de fabricación del vehículo.
     */
    @Column
    private Integer year;

    /**
     * Placa o número de matrícula única del vehículo.
     */
    @Column(unique = true, length = 20)
    private String plate;

    /**
     * Tarifa de alquiler diaria. Se define con precisión de 10 dígitos y 2 decimales.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    /**
     * Estado operativo actual del vehículo (Disponible, Mantenimiento, etc.).
     * Se almacena como un valor de cadena en la base de datos.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status;

    /**
     * Clasificación del vehículo según su tipo o segmento.
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Sede física a la que pertenece o donde se encuentra estacionado el vehículo.
     */
    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    /**
     * URL de la imagen representativa del vehículo.
     */
    @Column(length = 500)
    private String image;

    /**
     * Listado histórico y futuro de reservas asociadas a este vehículo.
     */
    @OneToMany(mappedBy = "car")
    private List<Reservation> reservations;

    /**
     * Constructor por defecto requerido por el motor de persistencia JPA.
     */
    public Car() {
    }

    // Métodos de acceso (Getters y Setters)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}