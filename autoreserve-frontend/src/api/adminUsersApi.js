import { apiFetch } from "./http";

export function getUsers(page = 0, size = 10) {
  return apiFetch(`/api/admin/users?page=${page}&size=${size}`);
}

export function deleteUser(id) {
  return apiFetch(`/api/admin/users/${id}`, {
    method: "DELETE"
  });
}
