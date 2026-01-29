package com.autoreserve.backend.domain.entity;
/**
 * Enumeración que define los métodos de pago aceptados por el sistema.
 * Permite tipificar la procedencia de los fondos en las transacciones de reserva.
 */
public enum PaymentMethod {
    CARD,
    TRANSFER,
    CASH
}
