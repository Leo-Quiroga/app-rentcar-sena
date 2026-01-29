package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entidad que representa la clasificación de los vehículos en el sistema.
 * Permite agrupar los automóviles por segmentos (ej. SUV, Compacto, Lujo)
 * y gestionar información descriptiva y visual de cada grupo.
 */
@Entity
@Table(name = "category")
public class Category {

    /**
     * Identificador único de la categoría. Generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la categoría. Campo obligatorio con límite de 100 caracteres.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Descripción detallada de las características de la categoría.
     * Definido como tipo TEXT para permitir contenido extenso.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Ruta o URL de la imagen representativa de la categoría.
     */
    @Column(length = 255)
    private String image;

    /**
     * Relación uno a muchos con la entidad Car.
     * Representa la colección de vehículos que pertenecen a esta clasificación.
     */
    @OneToMany(mappedBy = "category")
    private List<Car> cars;

    /**
     * Constructor por defecto para la instanciación de JPA.
     */
    public Category() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }
}