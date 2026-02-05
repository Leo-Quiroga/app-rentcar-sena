package com.autoreserve.backend.domain.entity;

/**
 * Estados de pago de una reserva
 */
public enum PaymentStatus {
    PENDING,    // Pendiente de pago
    PAID,       // Pagado
    REFUNDED    // Reembolsado
}