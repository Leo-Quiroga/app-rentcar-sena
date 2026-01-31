import { apiFetch } from "../api/http";

// Petición de login
export function loginRequest(email, password) {
  return apiFetch("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password })
  });
}
