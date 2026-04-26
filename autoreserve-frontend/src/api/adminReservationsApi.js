import { apiFetch } from "./http";

export function getAdminReservations(page = 0, size = 20) {
  return apiFetch(`/api/admin/reservations?page=${page}&size=${size}`)
    .then(response => {
      // Si la respuesta tiene estructura { success: true, data: [...] }
      if (response.success && response.data) {
        return response.data;
      }
      // Si es un array directo
      return response;
    });
}

export function getAdminReservationById(id) {
  return apiFetch(`/api/admin/reservations/${id}`)
    .then(response => {
      if (response.success && response.data) {
        return response.data;
      }
      return response;
    });
}

export function createReservationForClient(data) {
  return apiFetch("/api/admin/reservations", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export function updateReservationStatus(id, status) {
  return apiFetch(`/api/admin/reservations/${id}/status?status=${status}`, {
    method: "PUT",
  });
}

export function updatePaymentStatus(id, paymentStatus) {
  return apiFetch(`/api/admin/reservations/${id}/payment-status?paymentStatus=${paymentStatus}`, {
    method: "PUT",
  });
}
