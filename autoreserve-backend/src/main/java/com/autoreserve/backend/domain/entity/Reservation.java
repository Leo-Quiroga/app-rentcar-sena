package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Unidad física asignada. NULL mientras la reserva esté en PENDING (sin pago).
     * Se asigna al confirmar el pago.
     */
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    /**
     * Modelo solicitado. Siempre presente desde la creación.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "car_model_id")
    private CarModel carModel;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @ManyToOne
    @JoinColumn(name = "pickup_branch_id")
    private Branch pickupBranch;

    @ManyToOne
    @JoinColumn(name = "dropoff_branch_id")
    private Branch dropoffBranch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column
    private Integer totalDays;

    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Reservation() {
        this.paymentStatus = PaymentStatus.NO_PAYMENT;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
    public CarModel getCarModel() { return carModel; }
    public void setCarModel(CarModel carModel) { this.carModel = carModel; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Branch getPickupBranch() { return pickupBranch; }
    public void setPickupBranch(Branch pickupBranch) { this.pickupBranch = pickupBranch; }
    public Branch getDropoffBranch() { return dropoffBranch; }
    public void setDropoffBranch(Branch dropoffBranch) { this.dropoffBranch = dropoffBranch; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public Integer getTotalDays() { return totalDays; }
    public void setTotalDays(Integer totalDays) { this.totalDays = totalDays; }
    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
