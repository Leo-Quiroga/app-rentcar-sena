export const API_URL = "http://localhost:8080";

// Función genérica para hacer peticiones a la API
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
    // Si el error es un JSON (como el que te salió), lo parseamos
    const errorData = await response.json().catch(() => ({}));

    // Si el back envió un campo "message" o "error", lo usamos
    const errorMessage = errorData.message || errorData.error || "Error en la petición";
    throw new Error(errorMessage);
  }

  // Si es un DELETE exitoso, a veces el back devuelve 200 con un String o 204 sin nada
  if (response.status === 204) return null;

  // Intentamos parsear a JSON, si falla (porque es un String), devolvemos el texto
  const contentType = response.headers.get("content-type");
  if (contentType && contentType.includes("application/json")) {
    return await response.json();
  } else {
    return await response.text();
  }
}