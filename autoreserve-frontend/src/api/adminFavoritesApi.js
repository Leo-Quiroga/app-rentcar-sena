import { apiFetch } from "./http";

// API para administración de favoritos (requiere rol ADMIN)

// Obtener estadísticas generales de favoritos
export function getFavoriteStats() {
  return apiFetch("/api/admin/favorites/stats");
}

// Obtener usuarios que tienen un modelo como favorito
export function getUsersWithFavoriteModel(carModelId) {
  return apiFetch(`/api/admin/favorites/model/${carModelId}/users`);
}

// Obtener top modelos más populares
export function getTopFavoriteModels() {
  return apiFetch("/api/admin/favorites/top-models");
}

// Función helper para formatear estadísticas
export function formatFavoriteStats(stats) {
  if (!stats || !stats.success) return null;
  
  return {
    general: stats.generalStats,
    models: stats.modelStats,
    topModels: stats.topModels
  };
}