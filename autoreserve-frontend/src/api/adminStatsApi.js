import { apiFetch } from "./http";

// API para estadísticas del dashboard admin (requiere rol ADMIN)

// Obtener estadísticas del dashboard
export function getAdminStats() {
  return apiFetch("/api/admin/stats");
}