package com.autoreserve.backend.domain.entity;

public enum PaymentStatus {
    NO_PAYMENT,     // Reserva creada sin pago (PENDING)
    PAID,           // Pago confirmado
    REFUND_PENDING, // Cancelación aprobada, reembolso en proceso
    REFUNDED        // Dinero devuelto al cliente
}
