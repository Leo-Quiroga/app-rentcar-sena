package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa la reserva de un vehículo por parte de un usuario.
 * Es el núcleo del sistema, vinculando clientes, automóviles y periodos de tiempo,
 * además de gestionar el estado del contrato de alquiler.
 */
@Entity
@Table(name = "reservation")
public class Reservation {

    /**
     * Identificador único de la reserva.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Referencia al usuario (cliente) que realiza la reserva.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Referencia al vehículo seleccionado para el alquiler.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "car_id")
    private Car car;

    /**
     * Fecha de inicio del periodo de alquiler.
     */
    @Column(nullable = false)
    private LocalDate startDate;

    /**
     * Fecha de finalización del periodo de alquiler.
     */
    @Column(nullable = false)
    private LocalDate endDate;

    /**
     * Estado actual de la reserva (ej. CONFIRMED, CANCELLED, COMPLETED).
     * Se persiste como una cadena de texto para facilitar la legibilidad en BD.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    /**
     * Costo total calculado de la reserva basado en los días y la tarifa del auto.
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Sede de retiro del vehículo.
     */
    @ManyToOne
    @JoinColumn(name = "pickup_branch_id")
    private Branch pickupBranch;

    /**
     * Sede de entrega del vehículo.
     */
    @ManyToOne
    @JoinColumn(name = "dropoff_branch_id")
    private Branch dropoffBranch;

    /**
     * Estado del pago de la reserva.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    /**
     * Número total de días de la reserva.
     */
    @Column
    private Integer totalDays;

    /**
     * Precio por día aplicado en esta reserva.
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    /**
     * Constructor por defecto para JPA.
     */
    public Reservation() {
    }

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Branch getPickupBranch() {
        return pickupBranch;
    }

    public void setPickupBranch(Branch pickupBranch) {
        this.pickupBranch = pickupBranch;
    }

    public Branch getDropoffBranch() {
        return dropoffBranch;
    }

    public void setDropoffBranch(Branch dropoffBranch) {
        this.dropoffBranch = dropoffBranch;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }
}