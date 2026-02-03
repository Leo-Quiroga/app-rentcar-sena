import { apiFetch } from "./http";

// API para gestión administrativa de categorías (requiere rol ADMIN)

// Obtener todas las categorías (admin)
export function getAdminCategories() {
  return apiFetch("/api/admin/categories");
}

// Obtener categoría por ID (admin)
export function getAdminCategoryById(id) {
  return apiFetch(`/api/admin/categories/${id}`);
}

// Crear nueva categoría
export function createCategory(data) {
  return apiFetch("/api/admin/categories", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

// Actualizar categoría
export function updateCategory(id, data) {
  return apiFetch(`/api/admin/categories/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

// Eliminar categoría
export function deleteCategory(id) {
  return apiFetch(`/api/admin/categories/${id}`, {
    method: "DELETE"
  });
}