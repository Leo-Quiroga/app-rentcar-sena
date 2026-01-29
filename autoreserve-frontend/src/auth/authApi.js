// const API_URL = "http://localhost:8080/api/auth";
// import { apiFetch } from "../api/http";

// export async function loginRequest(email, password) {
//   const response = await fetch(`${API_URL}/login`, {
//     method: "POST",
//     headers: {
//       "Content-Type": "application/json"
//     },
//     body: JSON.stringify({ email, password })
//   });

//   if (!response.ok) {
//     throw new Error("Credenciales inválidas");
//   }

//   return response.json();
// }
import { apiFetch } from "../api/http";

export function loginRequest(email, password) {
  return apiFetch("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password })
  });
}
