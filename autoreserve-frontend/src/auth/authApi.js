import { apiFetch } from "../api/http";

// Registro de nuevo usuario
export function loginRequest(email, password) {
  return apiFetch("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password })
  });
}
