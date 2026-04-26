package com.autoreserve.backend.domain.entity;
/**
 * Estados posibles de una unidad física de vehículo.
 * PENDING_REGISTRATION: recién creada, sin placa ni color asignados
 * AVAILABLE: identificada y lista para rentar
 * RENTED: asignada a una reserva activa
 * MAINTENANCE: en taller o fuera de servicio temporal
 * OUT_OF_SERVICE: dada de baja definitivamente
 */
public enum CarStatus {
    PENDING_REGISTRATION,
    AVAILABLE,
    RENTED,
    MAINTENANCE,
    OUT_OF_SERVICE
}
