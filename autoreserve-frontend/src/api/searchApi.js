import { apiFetch } from "./http";

// API para búsqueda de autos disponibles

// Buscar autos disponibles por fechas y categoría
export function searchAvailableCars(startDate, endDate, categoryId = null) {
  const params = new URLSearchParams({
    startDate,
    endDate
  });
  
  if (categoryId) {
    params.append('categoryId', categoryId);
  }
  
  return apiFetch(`/api/search/cars?${params.toString()}`);
}

// Obtener ciudades con sedes activas
export function getCitiesWithBranches() {
  return apiFetch("/api/branches").then(branches => {
    const cities = [...new Set(branches.map(branch => branch.city))];
    return cities.sort();
  });
}