package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entidad que representa una sede o sucursal de la empresa de alquiler.
 * Mapea la tabla "branch" en la base de datos y gestiona la ubicación de los vehículos.
 */
@Entity
@Table(name = "branch")
public class Branch {

    /**
     * Identificador único de la sede. Generado automáticamente mediante estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre descriptivo de la sede. Campo obligatorio.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Dirección física de las instalaciones de la sede.
     */
    @Column(length = 255)
    private String address;

    /**
     * Ciudad donde se encuentra ubicada la sucursal.
     */
    @Column(length = 100)
    private String city;

    /**
     * Número telefónico de contacto de la sede.
     */
    @Column(length = 30)
    private String phone;

    /**
     * Relación uno a muchos con la entidad Car.
     * Representa el inventario de vehículos asociados a esta sede específica.
     */
    @OneToMany(mappedBy = "branch")
    private List<Car> cars;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Branch() {
    }

    // Métodos de acceso (Getters y Setters)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }
}