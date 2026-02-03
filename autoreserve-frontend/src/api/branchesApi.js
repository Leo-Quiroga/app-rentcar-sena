import { apiFetch } from "./http";

// API para sedes públicas (no requiere autenticación)

// Obtener todas las sedes
export function getBranches() {
  return apiFetch("/api/branches");
}

// Obtener sede por ID
export function getBranchById(id) {
  return apiFetch(`/api/branches/${id}`);
}