import { apiFetch } from "./http";

// API para categorías públicas (no requiere autenticación)

// Obtener todas las categorías
export function getCategories() {
  return apiFetch("/api/categories");
}

// Obtener categoría por ID
export function getCategoryById(id) {
  return apiFetch(`/api/categories/${id}`);
}