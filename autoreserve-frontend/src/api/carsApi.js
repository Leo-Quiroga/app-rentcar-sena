import { apiFetch } from "./http";

// API para autos públicos (no requiere autenticación)

// Obtener autos disponibles con filtros
export function getCars(params = {}) {
  const queryParams = new URLSearchParams();
  
  if (params.page !== undefined) queryParams.append('page', params.page);
  if (params.size !== undefined) queryParams.append('size', params.size);
  if (params.categoryId) queryParams.append('categoryId', params.categoryId);
  if (params.branchId) queryParams.append('branchId', params.branchId);
  
  const queryString = queryParams.toString();
  return apiFetch(`/api/cars${queryString ? `?${queryString}` : ''}`);
}

// Obtener auto por ID
export function getCarById(id) {
  return apiFetch(`/api/cars/${id}`);
}