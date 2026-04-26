import { apiFetch } from "./http";

// ===================== MODELOS =====================

export function getAdminCarModels() {
  return apiFetch("/api/admin/cars/models");
}

export function getAdminCarModelById(id) {
  return apiFetch(`/api/admin/cars/models/${id}`);
}

export function createCarModel(data) {
  return apiFetch("/api/admin/cars/models", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export function updateCarModel(id, data) {
  return apiFetch(`/api/admin/cars/models/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export function deleteCarModel(id) {
  return apiFetch(`/api/admin/cars/models/${id}`, { method: "DELETE" });
}

// ===================== UNIDADES =====================

export function getUnitsByModel(modelId) {
  return apiFetch(`/api/admin/cars/models/${modelId}/units`);
}

export function updateCarUnit(unitId, data) {
  return apiFetch(`/api/admin/cars/units/${unitId}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export function deleteCarUnit(unitId) {
  return apiFetch(`/api/admin/cars/units/${unitId}`, { method: "DELETE" });
}

// Compatibilidad con código anterior
export const getAdminCars = getAdminCarModels;
export const getAdminCarById = getAdminCarModelById;
export const createCar = createCarModel;
export const updateCar = updateCarModel;
export const deleteCar = deleteCarModel;
