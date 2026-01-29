import { apiFetch } from "./http";

// Perfil del usuario autenticado
export function getMyProfile() {
  return apiFetch("/api/users/me");
}

// Actualizar datos propios
export function updateMyProfile(data) {
  return apiFetch("/api/users/me", {
    method: "PUT",
    body: JSON.stringify(data),
  });
}
