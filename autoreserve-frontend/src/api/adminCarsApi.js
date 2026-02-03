import { apiFetch } from "./http";

// API para gestión administrativa de autos (requiere rol ADMIN)

// Obtener todos los autos (admin)
export function getAdminCars(page = 0, size = 10) {
  return apiFetch(`/api/admin/cars?page=${page}&size=${size}`);
}

// Obtener auto por ID (admin)
export function getAdminCarById(id) {
  return apiFetch(`/api/admin/cars/${id}`);
}

// Crear nuevo auto
export function createCar(data) {
  return apiFetch("/api/admin/cars", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

// Actualizar auto
export function updateCar(id, data) {
  return apiFetch(`/api/admin/cars/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

// Eliminar auto
export function deleteCar(id) {
  return apiFetch(`/api/admin/cars/${id}`, {
    method: "DELETE"
  });
}