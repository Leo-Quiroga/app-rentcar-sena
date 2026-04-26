import { apiFetch } from "./http";

export function getFaqs() {
  return apiFetch("/api/faq");
}

export function createFaq(data) {
  return apiFetch("/api/faq", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export function updateFaq(id, data) {
  return apiFetch(`/api/faq/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export function deleteFaq(id) {
  return apiFetch(`/api/faq/${id}`, { method: "DELETE" });
}
