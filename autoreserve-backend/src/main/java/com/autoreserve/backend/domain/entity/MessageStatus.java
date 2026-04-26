package com.autoreserve.backend.domain.entity;

public enum MessageStatus {
    OPEN,        // Ticket abierto, esperando respuesta del admin
    IN_PROGRESS, // Admin vio el ticket
    ANSWERED,    // Admin respondió, esperando al cliente
    CLOSED       // Cliente cerró el caso como resuelto
}
