import { useState } from "react";
import { mockTickets } from "../data/mockTickets";

export default function SupportTicket() {
  const [tickets, setTickets] = useState(mockTickets);
  const [form, setForm] = useState({ subject: "", message: "" });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const newTicket = {
      id: `TCK-${String(tickets.length + 1).padStart(3, "0")}`,
      subject: form.subject,
      status: "Abierto",
      createdAt: new Date().toISOString().split("T")[0],
    };
    setTickets([newTicket, ...tickets]);
    setForm({ subject: "", message: "" });
    alert("✅ Ticket creado (mock). En el futuro se conectará a la API.");
  };

  return (
    <div className="max-w-5xl mx-auto py-12 px-4 sm:px-6 lg:px-8 space-y-12">
      {/* Encabezado */}
      <header className="text-center">
        <h1 className="text-3xl font-bold text-neutral-dark mb-4">
          🎫 Tickets de Soporte
        </h1>
        <p className="text-gray-600">
          Crea un nuevo ticket o revisa el estado de tus solicitudes previas.
        </p>
      </header>

      {/* Formulario nuevo ticket */}
      <section className="bg-white shadow rounded-lg p-6 space-y-4">
        <h2 className="text-xl font-semibold text-neutral-dark">Nuevo Ticket</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="text"
            name="subject"
            placeholder="Asunto"
            value={form.subject}
            onChange={handleChange}
            required
            className="w-full border border-gray-300 rounded px-3 py-2"
          />
          <textarea
            name="message"
            placeholder="Describe tu problema o solicitud"
            rows="4"
            value={form.message}
            onChange={handleChange}
            required
            className="w-full border border-gray-300 rounded px-3 py-2"
          ></textarea>
          <button
            type="submit"
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
          >
            Enviar Ticket
          </button>
        </form>
      </section>

      {/* Lista de tickets previos */}
      <section>
        <h2 className="text-xl font-semibold text-neutral-dark mb-4">
          Mis Tickets
        </h2>
        {tickets.length === 0 ? (
          <p className="text-gray-600">No tienes tickets registrados.</p>
        ) : (
          <div className="space-y-3">
            {tickets.map((ticket) => (
              <div
                key={ticket.id}
                className="bg-white shadow rounded-lg p-4 flex justify-between items-center"
              >
                <div>
                  <p className="font-semibold text-neutral-dark">
                    {ticket.subject}
                  </p>
                  <p className="text-sm text-gray-600">
                    Creado: {ticket.createdAt}
                  </p>
                </div>
                <span
                  className={`px-3 py-1 rounded text-sm ${
                    ticket.status === "Abierto"
                      ? "bg-yellow-100 text-yellow-700"
                      : "bg-green-100 text-green-700"
                  }`}
                >
                  {ticket.status}
                </span>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
