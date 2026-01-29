package com.autoreserve.backend.domain.entity;
/**
 * Enumeración que define los estados posibles de un vehículo en el sistema.
 * Se utiliza para controlar la disponibilidad y el flujo operativo de la flota.
 */
public enum CarStatus {
    AVAILABLE,
    RESERVED,
    MAINTENANCE
}
