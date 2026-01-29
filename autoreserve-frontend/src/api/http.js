export const API_URL = "http://localhost:8080";

export async function apiFetch(endpoint, options = {}) {
  const auth = localStorage.getItem("auth");
  const token = auth ? JSON.parse(auth).token : null;

  const headers = {
    "Content-Type": "application/json",
    ...(token && { Authorization: `Bearer ${token}` }),
    ...options.headers
  };

  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers
  });

  if (!response.ok) {
    throw new Error("Error en la petición");
  }

  return response.status === 204 ? null : response.json();
}
