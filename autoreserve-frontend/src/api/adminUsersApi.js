import { apiFetch } from "./http";

//Api para la gestión de usuarios por parte del administrador
// Obtener lista de usuarios con paginación
export function getUsers(page = 0, size = 10) {
  return apiFetch(`/api/admin/users?page=${page}&size=${size}`);
}
// Obtener detalles de un usuario por ID
export function getUserById(id) {
  return apiFetch(`/api/admin/users/${id}`);
}
// Crear un nuevo usuario
export function createUser(data) {
  return apiFetch("/api/admin/users", {
    method: "POST",
    body: JSON.stringify(data),
  });
}
// Actualizar un usuario existente
export function updateUser(id, data) {
  return apiFetch(`/api/admin/users/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}
// Eliminar un usuario por ID
export const deleteUser = async (id) => {
  // Pasamos el endpoint y el objeto de opciones con el método DELETE
  return await apiFetch(`/api/admin/users/${id}`, {
    method: "DELETE"
  });
};
