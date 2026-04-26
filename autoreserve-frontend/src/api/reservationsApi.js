import { apiFetch } from "./http";

// API para gestión de reservas

// Obtener mis reservas
export function getMyReservations() {
  return apiFetch("/api/reservations/my")
    .then(response => {
      // Si la respuesta tiene estructura { success: true, data: [...] }
      if (response.success && response.data) {
        return response.data;
      }
      // Si es un array directo
      return response;
    });
}

// Obtener reserva por ID
export function getReservationById(id) {
  return apiFetch(`/api/reservations/${id}`)
    .then(response => {
      if (response.success && response.data) {
        return response.data;
      }
      return response;
    });
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

// Confirmar pago de reserva
export function confirmPayment(id) {
  return apiFetch(`/api/reservations/${id}/confirm-payment`, {
    method: "PUT",
  });
}