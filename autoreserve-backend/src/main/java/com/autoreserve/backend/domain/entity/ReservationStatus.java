package com.autoreserve.backend.domain.entity;
/**
 * Enumeración que define los posibles estados de una reserva en su ciclo de vida.
 * Permite controlar la lógica de negocio, la disponibilidad del vehículo y los procesos de facturación.
 */
public enum ReservationStatus {
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

