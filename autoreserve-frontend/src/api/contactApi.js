import { apiFetch } from "./http";

// ── CLIENTE ──────────────────────────────────────────────────────────────────

export function createTicket(data) {
  return apiFetch("/api/contact", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export function getMyTickets() {
  return apiFetch("/api/contact/my");
}

export function getMyTicketDetail(id) {
  return apiFetch(`/api/contact/my/${id}`);
}

export function clientReply(id, message) {
  return apiFetch(`/api/contact/my/${id}/reply`, {
    method: "POST",
    body: JSON.stringify({ message }),
  });
}

export function closeTicket(id) {
  return apiFetch(`/api/contact/my/${id}/close`, { method: "PUT" });
}

// ── ADMIN ─────────────────────────────────────────────────────────────────────

export function getAdminTickets() {
  return apiFetch("/api/contact/admin");
}

export function getAdminTicketDetail(id) {
  return apiFetch(`/api/contact/admin/${id}`);
}

export function adminReply(id, message) {
  return apiFetch(`/api/contact/admin/${id}/reply`, {
    method: "POST",
    body: JSON.stringify({ message }),
  });
}

export function adminCloseTicket(id) {
  return apiFetch(`/api/contact/admin/${id}/close`, { method: "PUT" });
}
