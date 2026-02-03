import { apiFetch } from "./http";

// API para gestión administrativa de sedes (requiere rol ADMIN)

// Obtener todas las sedes (admin)
export function getAdminBranches() {
  return apiFetch("/api/admin/branches");
}

// Obtener sede por ID (admin)
export function getAdminBranchById(id) {
  return apiFetch(`/api/admin/branches/${id}`);
}

// Crear nueva sede
export function createBranch(data) {
  return apiFetch("/api/admin/branches", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

// Actualizar sede
export function updateBranch(id, data) {
  return apiFetch(`/api/admin/branches/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

// Eliminar sede
export function deleteBranch(id) {
  return apiFetch(`/api/admin/branches/${id}`, {
    method: "DELETE"
  });
}