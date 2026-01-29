package com.autoreserve.backend.domain.entity;
/**
 * Enumeración que representa los estados posibles de una transacción de pago.
 * Define el ciclo de vida y la validez financiera de una reserva en el sistema.
 */
public enum PaymentStatus {
    SUCCESS,
    FAILED,
    PENDING
}
