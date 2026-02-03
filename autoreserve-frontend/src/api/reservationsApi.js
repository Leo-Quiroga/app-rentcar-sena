import { apiFetch } from "./http";

// API para gestión de reservas

// Obtener mis reservas
export function getMyReservations() {
  return apiFetch("/api/reservations/my");
}

// Obtener reserva por ID
export function getReservationById(id) {
  return apiFetch(`/api/reservations/${id}`);
}

// Crear nueva reserva
export function createReservation(data) {
  return apiFetch("/api/reservations", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

// Cancelar reserva
export function cancelReservation(id) {
  return apiFetch(`/api/reservations/${id}/cancel`, {
    method: "PUT",
  });
}