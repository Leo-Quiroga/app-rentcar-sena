import { apiFetch } from "../api/http";

// Petición de login
export function loginRequest(email, password) {
  return apiFetch("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password })
  });
}

// Petición de registro
export function registerRequest(userData) {
  return apiFetch("/api/auth/register", {
    method: "POST",
    body: JSON.stringify(userData)
  });
}
