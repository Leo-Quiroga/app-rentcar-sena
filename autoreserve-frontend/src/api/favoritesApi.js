import { apiFetch } from "./http";

// API para favoritos (requiere autenticación)
// Refactorizada para trabajar con modelos en lugar de unidades específicas

// Obtener mis modelos favoritos
export function getMyFavorites() {
  return apiFetch("/api/favorites/my");
}

// Agregar modelo a favoritos
export function addToFavorites(carModelId) {
  return apiFetch("/api/favorites", {
    method: "POST",
    body: JSON.stringify({ carModelId })
  });
}

// Quitar modelo de favoritos
export function removeFromFavorites(carModelId) {
  return apiFetch(`/api/favorites/${carModelId}`, {
    method: "DELETE"
  });
}

// Verificar si un modelo es favorito
export function isFavorite(carModelId) {
  return apiFetch(`/api/favorites/check/${carModelId}`);
}

// Obtener IDs de modelos favoritos (para marcar corazones)
export function getFavoriteIds() {
  return apiFetch("/api/favorites/ids");
}

// Toggle favorito (agregar o quitar según estado actual)
export async function toggleFavorite(carModelId) {
  try {
    const checkResponse = await isFavorite(carModelId);
    if (checkResponse.isFavorite) {
      return await removeFromFavorites(carModelId);
    } else {
      return await addToFavorites(carModelId);
    }
  } catch (error) {
    console.error('Error toggling favorite:', error);
    throw error;
  }
}