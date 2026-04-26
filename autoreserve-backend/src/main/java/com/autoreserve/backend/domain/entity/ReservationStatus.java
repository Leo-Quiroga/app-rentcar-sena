package com.autoreserve.backend.domain.entity;
/**
 * Enumeración que define los posibles estados de una reserva en su ciclo de vida.
 * PENDING: reserva creada, esperando pago (auto NO bloqueado en búsquedas)
 * CONFIRMED: pago procesado, auto bloqueado para otros usuarios
 * IN_PROGRESS: el período de alquiler está activo (startDate <= hoy <= endDate)
 * COMPLETED: el período de alquiler finalizó (hoy > endDate)
 * CANCELLED: reserva cancelada por el cliente, admin o por timeout de pago
 */
public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
