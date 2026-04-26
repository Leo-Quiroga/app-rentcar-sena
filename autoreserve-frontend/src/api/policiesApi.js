import { apiFetch } from "./http";

export function getPolicies() {
  return apiFetch("/api/policies");
}

export function updatePolicy(slug, data) {
  return apiFetch(`/api/policies/${slug}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}
