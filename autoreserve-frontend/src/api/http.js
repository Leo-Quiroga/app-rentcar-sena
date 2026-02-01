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
    const errorData = await response.json().catch(() => ({}));
    
    // Mapear códigos de estado a mensajes específicos
    let errorMessage;
    switch (response.status) {
      case 400:
        errorMessage = errorData.message || "Datos inválidos";
        break;
      case 401:
        errorMessage = "Credenciales incorrectas";
        break;
      case 404:
        errorMessage = "Usuario no encontrado";
        break;
      case 422:
        errorMessage = errorData.message || "Formato de email inválido";
        break;
      case 500:
        errorMessage = "Error interno del servidor";
        break;
      default:
        errorMessage = errorData.message || "Error en la petición";
    }
    
    const error = new Error(errorMessage);
    error.status = response.status;
    error.data = errorData;
    throw error;
  }

  if (response.status === 204) return null;

  const contentType = response.headers.get("content-type");
  if (contentType && contentType.includes("application/json")) {
    return await response.json();
  } else {
    return await response.text();
  }
}