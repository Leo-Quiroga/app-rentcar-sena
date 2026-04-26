import { apiFetch } from "./http";

export function getPolicies() {
  return apiFetch("/api/policies");
}

export function createPolicy(data) {
  return apiFetch("/api/policies", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export function updatePolicy(id, data) {
  return apiFetch(`/api/policies/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export function deletePolicy(id) {
  return apiFetch(`/api/policies/${id}`, { method: "DELETE" });
}
