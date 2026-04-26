// Página de contacto / FAQ / soporte unificada
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { createTicket } from "../api/contactApi";
import { getFaqs, createFaq, updateFaq, deleteFaq } from "../api/faqApi";

const MESSAGE_TYPES = [
  { value: "PREGUNTA",     label: "Pregunta" },
  { value: "QUEJA",        label: "Queja" },
  { value: "RECLAMO",      label: "Reclamo" },
  { value: "SOLICITUD",    label: "Solicitud" },
  { value: "FELICITACION", label: "Felicitación" },
];

const DEFAULT_FAQS = [
  { id: "d1", question: "¿Cómo puedo modificar una reserva?", answer: "Actualmente no es posible modificar una reserva directamente. Debes cancelarla y crear una nueva con las fechas o el vehículo deseado." },
  { id: "d2", question: "¿Qué pasa si cancelo una reserva?", answer: "Puedes cancelar desde 'Mis Reservas'. Si la reserva está confirmada (pagada), aplican las políticas de reembolso según la anticipación de la cancelación." },
  { id: "d3", question: "¿Puedo recoger el auto en una ciudad y entregarlo en otra?", answer: "Sí, al momento de reservar puedes seleccionar sedes de retiro y entrega diferentes, siempre que ambas ciudades tengan sede disponible." },
  { id: "d4", question: "¿Qué documentos necesito para retirar el vehículo?", answer: "Necesitas tu licencia de conducción vigente, documento de identidad y la confirmación de reserva (número de reserva)." },
  { id: "d5", question: "¿Cuándo se realiza el cobro de la reserva?", answer: "El cobro se realiza al momento de confirmar el pago en la plataforma. Sin pago confirmado, la reserva queda pendiente por 24 horas y luego se cancela automáticamente." },
  { id: "d6", question: "¿Cómo hago seguimiento a mi mensaje de soporte?", answer: "Si estás registrado, puedes ver el historial de tus mensajes y las respuestas del equipo en la sección 'Mis mensajes' de tu perfil." },
];

// Componente reutilizable para gestionar FAQ (igual patrón que Policies)
function FaqSection({ isAdmin }) {
  const [faqs, setFaqs] = useState([]);
  const [openId, setOpenId] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [editForm, setEditForm] = useState({ question: "", answer: "" });
  const [saving, setSaving] = useState(false);
  const [showNewForm, setShowNewForm] = useState(false);
  const [newForm, setNewForm] = useState({ question: "", answer: "" });
  const [creating, setCreating] = useState(false);

  const load = () =>
    getFaqs()
      .then(data => setFaqs(data.length > 0 ? data : DEFAULT_FAQS))
      .catch(() => setFaqs(DEFAULT_FAQS));

  useEffect(() => { load(); }, []);

  const startEdit = (faq) => {
    setEditingId(faq.id);
    setEditForm({ question: faq.question, answer: faq.answer });
    setOpenId(null);
  };

  const handleSave = async (id) => {
    setSaving(true);
    try {
      await updateFaq(id, editForm);
      setFaqs(prev => prev.map(f => f.id === id ? { ...f, ...editForm } : f));
      setEditingId(null);
    } catch (err) { alert("Error: " + err.message); }
    finally { setSaving(false); }
  };

  const handleDelete = async (id, question) => {
    if (!window.confirm(`¿Eliminar la pregunta "${question}"?`)) return;
    try {
      await deleteFaq(id);
      await load();
    } catch (err) { alert("Error: " + err.message); }
  };

  const handleCreate = async () => {
    if (!newForm.question.trim() || !newForm.answer.trim()) {
      alert("La pregunta y la respuesta son obligatorias.");
      return;
    }
    setCreating(true);
    try {
      await createFaq(newForm);
      await load();
      setNewForm({ question: "", answer: "" });
      setShowNewForm(false);
    } catch (err) { alert("Error: " + err.message); }
    finally { setCreating(false); }
  };

  return (
    <section>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-semibold text-neutral-dark">❓ Preguntas Frecuentes</h2>
        {isAdmin && (
          <span className="text-xs text-blue-600 bg-blue-50 px-2 py-1 rounded-full">
            ✏️ Modo administrador
          </span>
        )}
      </div>

      <div className="space-y-3">
        {faqs.map((faq, index) => (
          <div key={faq.id} className="bg-white shadow-sm rounded-lg overflow-hidden">
            {editingId === faq.id ? (
              // Modo edición
              <div className="p-5 space-y-3">
                <div>
                  <label className="block text-xs font-semibold text-gray-500 mb-1">Pregunta</label>
                  <input type="text" value={editForm.question}
                    onChange={e => setEditForm({ ...editForm, question: e.target.value })}
                    className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary" />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-500 mb-1">Respuesta</label>
                  <textarea value={editForm.answer} rows={4}
                    onChange={e => setEditForm({ ...editForm, answer: e.target.value })}
                    className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary resize-y" />
                </div>
                <div className="flex gap-2">
                  <button onClick={() => handleSave(faq.id)} disabled={saving}
                    className="px-4 py-2 bg-primary text-white text-sm rounded hover:bg-primary-dark disabled:opacity-50 transition">
                    {saving ? "Guardando..." : "Guardar"}
                  </button>
                  <button onClick={() => setEditingId(null)}
                    className="px-4 py-2 border border-gray-300 text-sm rounded hover:bg-gray-100 transition">
                    Cancelar
                  </button>
                </div>
              </div>
            ) : (
              // Modo lectura con acordeón
              <>
                <button
                  onClick={() => setOpenId(openId === faq.id ? null : faq.id)}
                  className="w-full text-left px-5 py-4 font-medium text-gray-800 flex justify-between items-center hover:bg-gray-50 transition"
                >
                  <span>{index + 1}. {faq.question}</span>
                  <div className="flex items-center gap-2 shrink-0 ml-4">
                    {isAdmin && (
                      <>
                        <span onClick={e => { e.stopPropagation(); startEdit(faq); }}
                          className="text-xs px-2 py-0.5 border border-blue-200 text-blue-600 rounded hover:bg-blue-50 transition cursor-pointer">
                          ✏️
                        </span>
                        <span onClick={e => { e.stopPropagation(); handleDelete(faq.id, faq.question); }}
                          className="text-xs px-2 py-0.5 border border-red-200 text-red-600 rounded hover:bg-red-50 transition cursor-pointer">
                          🗑️
                        </span>
                      </>
                    )}
                    <span className="text-gray-400">{openId === faq.id ? "▲" : "▼"}</span>
                  </div>
                </button>
                {openId === faq.id && (
                  <div className="px-5 pb-4 text-gray-600 text-sm leading-relaxed border-t border-gray-100">
                    {faq.answer}
                  </div>
                )}
              </>
            )}
          </div>
        ))}
      </div>

      {/* Agregar nueva FAQ (solo admin) */}
      {isAdmin && (
        <div className="mt-4">
          {!showNewForm ? (
            <button onClick={() => setShowNewForm(true)}
              className="w-full py-3 border-2 border-dashed border-gray-300 text-gray-500 rounded-lg hover:border-primary hover:text-primary transition text-sm font-medium">
              + Agregar nueva pregunta frecuente ({faqs.length + 1})
            </button>
          ) : (
            <div className="border-2 border-primary border-dashed rounded-lg p-5 bg-blue-50 space-y-3 mt-3">
              <h3 className="font-semibold text-gray-700 text-sm">Nueva pregunta — #{faqs.length + 1}</h3>
              <div>
                <label className="block text-xs font-semibold text-gray-500 mb-1">Pregunta *</label>
                <input type="text" value={newForm.question}
                  onChange={e => setNewForm({ ...newForm, question: e.target.value })}
                  placeholder="Ej: ¿Cómo puedo cambiar mi contraseña?"
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm bg-white focus:ring-primary focus:border-primary" />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-500 mb-1">Respuesta *</label>
                <textarea value={newForm.answer} rows={4}
                  onChange={e => setNewForm({ ...newForm, answer: e.target.value })}
                  placeholder="Escribe la respuesta..."
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm bg-white focus:ring-primary focus:border-primary resize-y" />
              </div>
              <div className="flex gap-2">
                <button onClick={handleCreate} disabled={creating}
                  className="px-4 py-2 bg-primary text-white text-sm rounded hover:bg-primary-dark disabled:opacity-50 transition">
                  {creating ? "Creando..." : "Crear pregunta"}
                </button>
                <button onClick={() => { setShowNewForm(false); setNewForm({ question: "", answer: "" }); }}
                  className="px-4 py-2 border border-gray-300 text-sm rounded hover:bg-gray-100 transition">
                  Cancelar
                </button>
              </div>
            </div>
          )}
        </div>
      )}
    </section>
  );
}

export default function Contact() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const isLoggedIn = Boolean(user);
  const isAdmin = user?.role === "ADMIN";

  const [form, setForm] = useState({
    name: user ? `${user.firstName || ""} ${user.lastName || ""}`.trim() : "",
    email: user?.email || "",
    subject: "",
    type: "PREGUNTA",
    message: "",
  });
  const [sending, setSending] = useState(false);
  const [sent, setSent] = useState(false);
  const [ticketId, setTicketId] = useState(null);
  const [error, setError] = useState("");

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSending(true);
    try {
      const res = await createTicket(form);
      setTicketId(res.ticketId);
      setSent(true);
    } catch (err) {
      setError(err.message);
    } finally {
      setSending(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8 space-y-12">

      {isAdmin && (
        <button onClick={() => navigate("/admin")} className="text-primary hover:underline text-sm">
          ← Volver al Dashboard
        </button>
      )}

      {/* Encabezado */}
      <header className="text-center">
        <h1 className="text-3xl font-bold text-neutral-dark mb-4">📞 Contacto y Soporte</h1>
        <p className="text-gray-600">¿Tienes dudas, sugerencias o necesitas ayuda? Estamos aquí para ti.</p>
      </header>

      {/* Datos de contacto */}
      <section className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {[
          { icon: "📍", title: "Oficina principal", info: "Calle 100 #15-20\nBogotá, Colombia" },
          { icon: "📱", title: "Teléfono de soporte", info: "+57 1 234 5678\nLun–Vie 8:00–18:00" },
          { icon: "✉️", title: "Correo electrónico", info: "soporte@autoreserve.com" },
        ].map(({ icon, title, info }) => (
          <div key={title} className="bg-white shadow-sm rounded-lg p-5 text-center">
            <div className="text-3xl mb-2">{icon}</div>
            <p className="font-semibold text-gray-800 text-sm">{title}</p>
            <p className="text-gray-500 text-xs mt-1 whitespace-pre-line">{info}</p>
          </div>
        ))}
      </section>

      {/* Formulario — oculto para admin */}
      {!isAdmin && (
        <section className="bg-white shadow rounded-lg p-6">
          <h2 className="text-xl font-semibold text-neutral-dark mb-4">Envíanos un mensaje</h2>
          {sent ? (
            <div className="text-center py-8 space-y-4">
              <div className="text-5xl">✅</div>
              <h3 className="text-lg font-semibold text-green-700">¡Mensaje enviado exitosamente!</h3>
              <p className="text-gray-600 text-sm">
                Tu caso fue registrado con el número <strong>#{ticketId}</strong>.
                {isLoggedIn
                  ? " Puedes hacer seguimiento desde \"Mis mensajes\" en tu perfil."
                  : " Te responderemos al correo que indicaste."}
              </p>
              <div className="flex gap-3 justify-center flex-wrap">
                {isLoggedIn && (
                  <button onClick={() => navigate("/mis-mensajes")}
                    className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition text-sm">
                    Ver mis mensajes
                  </button>
                )}
                <button onClick={() => { setSent(false); setForm({ ...form, subject: "", message: "" }); }}
                  className="px-4 py-2 border border-gray-300 rounded hover:bg-gray-100 transition text-sm">
                  Enviar otro mensaje
                </button>
              </div>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-4">
              {error && (
                <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded p-3">{error}</div>
              )}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Nombre *</label>
                  <input type="text" name="name" value={form.name} onChange={handleChange} required
                    disabled={isLoggedIn}
                    className="w-full border border-gray-300 rounded px-3 py-2 text-sm disabled:bg-gray-50 focus:ring-primary focus:border-primary" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Correo electrónico *</label>
                  <input type="email" name="email" value={form.email} onChange={handleChange} required
                    disabled={isLoggedIn}
                    className="w-full border border-gray-300 rounded px-3 py-2 text-sm disabled:bg-gray-50 focus:ring-primary focus:border-primary" />
                </div>
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Tipo de mensaje *</label>
                  <select name="type" value={form.type} onChange={handleChange}
                    className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary">
                    {MESSAGE_TYPES.map(t => (
                      <option key={t.value} value={t.value}>{t.label}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Asunto *</label>
                  <input type="text" name="subject" value={form.subject} onChange={handleChange} required
                    placeholder="Describe brevemente tu consulta"
                    className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary" />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Mensaje *</label>
                <textarea name="message" value={form.message} onChange={handleChange} required rows={5}
                  placeholder="Describe tu consulta con el mayor detalle posible..."
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary resize-y" />
              </div>
              {isLoggedIn && (
                <p className="text-xs text-gray-400">
                  ℹ️ Como usuario registrado, podrás hacer seguimiento a este mensaje en "Mis mensajes".
                </p>
              )}
              <button type="submit" disabled={sending}
                className="px-6 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark disabled:opacity-50 transition">
                {sending ? "Enviando..." : "Enviar mensaje"}
              </button>
            </form>
          )}
        </section>
      )}

      {/* FAQ gestionable */}
      <FaqSection isAdmin={isAdmin} />
    </div>
  );
}
