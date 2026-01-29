package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa la transacción financiera de una reserva.
 * Mapea la tabla "payment" y registra el monto, método y estado del pago realizado por el cliente.
 */
@Entity
@Table(name = "payment")
public class Payment {

    /**
     * Identificador único del registro de pago.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación uno a uno con la entidad Reservation.
     * Garantiza que cada pago esté vinculado exclusivamente a una única reserva.
     */
    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    /**
     * Importe total pagado.
     * Configurado con precisión de 10 dígitos y 2 decimales para precisión contable.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Método utilizado para efectuar el pago (ej. Tarjeta de crédito, Transferencia).
     * Se almacena como un valor de cadena basado en la enumeración PaymentMethod.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    /**
     * Estado actual del proceso de pago (ej. Pendiente, Completado, Fallido).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    /**
     * Fecha y hora exacta en la que se procesó la transacción.
     */
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    /**
     * Constructor por defecto para la gestión de persistencia JPA.
     */
    public Payment() {
    }

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
}