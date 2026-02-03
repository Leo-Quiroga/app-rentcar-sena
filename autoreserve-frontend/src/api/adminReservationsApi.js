import { apiFetch } from "./http";

// API para gestión administrativa de reservas (requiere rol ADMIN)

// Obtener todas las reservas (admin)
export function getAdminReservations(page = 0, size = 20) {
  return apiFetch(`/api/admin/reservations?page=${page}&size=${size}`);
}

// Crear reserva para un cliente (admin)
export function createReservationForClient(data) {
  return apiFetch("/api/admin/reservations", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

// Actualizar estado de reserva (admin)
export function updateReservationStatus(id, status) {
  return apiFetch(`/api/admin/reservations/${id}/status?status=${status}`, {
    method: "PUT",
  });
}