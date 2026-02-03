import { apiFetch } from "./http";

// API para favoritos (requiere autenticación)

// Obtener mis favoritos
export function getMyFavorites() {
  return apiFetch("/api/favorites/my");
}

// Agregar a favoritos
export function addToFavorites(carId) {
  return apiFetch(`/api/favorites?carId=${carId}`, {
    method: "POST"
  });
}

// Quitar de favoritos
export function removeFromFavorites(carId) {
  return apiFetch(`/api/favorites/${carId}`, {
    method: "DELETE"
  });
}