// Mis mensajes de soporte — vista del cliente
import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { getMyTickets, getMyTicketDetail, clientReply, closeTicket } from "../api/contactApi";

const STATUS_CONFIG = {
  OPEN:        { label: "Abierto",     pill: "bg-red-100 text-red-700" },
  IN_PROGRESS: { label: "En revisión", pill: "bg-yellow-100 text-yellow-700" },
  ANSWERED:    { label: "Respondido",  pill: "bg-blue-100 text-blue-700" },
  CLOSED:      { label: "Cerrado",     pill: "bg-gray-100 text-gray-500" },
};

const TYPE_LABEL = {
  PREGUNTA: "Pregunta", QUEJA: "Queja", RECLAMO: "Reclamo",
  SOLICITUD: "Solicitud", FELICITACION: "Felicitación",
};

function StatusPill({ status }) {
  const s = STATUS_CONFIG[status] || { label: status, pill: "bg-gray-100 text-gray-600" };
  return <span className={`inline-block px-2 py-0.5 rounded-full text-xs font-medium ${s.pill}`}>{s.label}</span>;
}

function TicketThread({ ticketId, onBack }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [reply, setReply] = useState("");
  const [sending, setSending] = useState(false);
  const [closing, setClosing] = useState(false);
  const bottomRef = useRef(null);

  const load = async () => {
    try {
      const res = await getMyTicketDetail(ticketId);
      setData(res);
    } catch (err) { alert("Error: " + err.message); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, [ticketId]);
  useEffect(() => { bottomRef.current?.scrollIntoView({ behavior: "smooth" }); }, [data?.replies]);

  const handleReply = async () => {
    if (!reply.trim()) return;
    setSending(true);
    try {
      await clientReply(ticketId, reply);
      setReply("");
      await load();
    } catch (err) { alert("Error: " + err.message); }
    finally { setSending(false); }
  };

  const handleClose = async () => {
    if (!window.confirm("¿Marcar este caso como resuelto y cerrarlo?")) return;
    setClosing(true);
    try {
      await closeTicket(ticketId);
      await load();
    } catch (err) { alert("Error: " + err.message); }
    finally { setClosing(false); }
  };

  if (loading) return <div className="py-10 text-center text-gray-500">Cargando conversación...</div>;
  if (!data) return null;

  const { ticket, replies } = data;
  const isClosed = ticket.status === "CLOSED";

  return (
    <div className="bg-white shadow rounded-lg overflow-hidden">
      {/* Header */}
      <div className="px-6 py-4 border-b bg-gray-50 flex items-start justify-between gap-4">
        <div>
          <button onClick={onBack} className="text-primary hover:underline text-sm mb-1">
            ← Volver a mis mensajes
          </button>
          <div className="flex items-center gap-2 flex-wrap">
            <span className="font-bold text-gray-800">#{ticket.id} — {ticket.subject}</span>
            <StatusPill status={ticket.status} />
            <span className="text-xs text-gray-500">{TYPE_LABEL[ticket.type] || ticket.type}</span>
          </div>
          <p className="text-xs text-gray-400 mt-1">
            Creado el {new Date(ticket.createdAt).toLocaleString()}
          </p>
        </div>
        {!isClosed && (
          <button onClick={handleClose} disabled={closing}
            className="text-xs px-3 py-1 border border-green-300 text-green-700 rounded hover:bg-green-50 transition shrink-0 disabled:opacity-50">
            {closing ? "..." : "✓ Marcar como resuelto"}
          </button>
        )}
      </div>

      {/* Hilo */}
      <div className="px-6 py-4 space-y-4 overflow-y-auto" style={{ maxHeight: "450px" }}>
        {replies.map(r => (
          <div key={r.id} className={`flex ${r.sentBy === "CLIENT" ? "justify-end" : "justify-start"}`}>
            <div className={`max-w-[80%] rounded-2xl px-4 py-3 text-sm shadow-sm ${
              r.sentBy === "CLIENT"
                ? "bg-primary text-white rounded-br-none"
                : "bg-gray-100 text-gray-800 rounded-bl-none"
            }`}>
              <p className={`text-xs font-semibold mb-1 ${r.sentBy === "CLIENT" ? "text-blue-100" : "text-gray-500"}`}>
                {r.sentBy === "CLIENT" ? "Tú" : "Soporte AutoReserve"}
              </p>
              <p className="whitespace-pre-line leading-relaxed">{r.content}</p>
              <p className={`text-xs mt-2 ${r.sentBy === "CLIENT" ? "text-blue-200" : "text-gray-400"}`}>
                {new Date(r.createdAt).toLocaleString()}
              </p>
            </div>
          </div>
        ))}
        <div ref={bottomRef} />
      </div>

      {/* Respuesta */}
      {!isClosed ? (
        <div className="px-6 py-4 border-t bg-gray-50">
          {ticket.status === "ANSWERED" && (
            <div className="bg-blue-50 border border-blue-200 text-blue-700 text-xs rounded p-2 mb-3">
              💬 El equipo de soporte te ha respondido. Puedes escribir una contra-pregunta o marcar el caso como resuelto si quedaste satisfecho.
            </div>
          )}
          <div className="flex gap-3">
            <textarea value={reply} onChange={e => setReply(e.target.value)}
              placeholder="Escribe tu respuesta o contra-pregunta..."
              rows={3}
              className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-primary focus:border-primary resize-none"
              onKeyDown={e => { if (e.key === "Enter" && e.ctrlKey) handleReply(); }} />
            <button onClick={handleReply} disabled={sending || !reply.trim()}
              className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark disabled:opacity-50 transition text-sm self-end">
              {sending ? "..." : "Enviar"}
            </button>
          </div>
          <p className="text-xs text-gray-400 mt-1">Ctrl+Enter para enviar</p>
        </div>
      ) : (
        <div className="px-6 py-3 border-t bg-green-50 text-center text-sm text-green-700">
          ✓ Este caso está cerrado. Si necesitas más ayuda, puedes abrir un nuevo mensaje desde Contacto.
        </div>
      )}
    </div>
  );
}

export default function MyMessages() {
  const navigate = useNavigate();
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedId, setSelectedId] = useState(null);

  useEffect(() => {
    getMyTickets()
      .then(setTickets)
      .catch(err => alert("Error: " + err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return (
    <div className="max-w-4xl mx-auto py-10 px-4 text-center">
      <p className="text-gray-600">Cargando mensajes...</p>
    </div>
  );

  if (selectedId) return (
    <div className="max-w-4xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <TicketThread ticketId={selectedId} onBack={() => setSelectedId(null)} />
    </div>
  );

  return (
    <div className="max-w-4xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <div className="mb-6">
        <button onClick={() => navigate("/perfil")} className="text-primary hover:underline text-sm mb-2">
          ← Volver al Perfil
        </button>
        <h1 className="text-2xl font-bold">💬 Mis Mensajes</h1>
        <p className="text-sm text-gray-500 mt-1">Historial de tus consultas y respuestas del equipo de soporte</p>
      </div>

      {tickets.length === 0 ? (
        <div className="bg-white shadow rounded-lg p-10 text-center">
          <p className="text-gray-500 mb-4">No tienes mensajes todavía.</p>
          <button onClick={() => navigate("/contacto")}
            className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition text-sm">
            Enviar un mensaje
          </button>
        </div>
      ) : (
        <div className="space-y-3">
          {tickets.map(t => (
            <div key={t.id}
              onClick={() => setSelectedId(t.id)}
              className="bg-white shadow-sm rounded-lg p-5 cursor-pointer hover:shadow-md transition flex items-start justify-between gap-4">
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 flex-wrap mb-1">
                  <span className="font-semibold text-gray-800 truncate">#{t.id} — {t.subject}</span>
                  <StatusPill status={t.status} />
                </div>
                <p className="text-xs text-gray-500">
                  {TYPE_LABEL[t.type] || t.type} · {new Date(t.createdAt).toLocaleDateString()}
                </p>
                {t.status === "ANSWERED" && (
                  <p className="text-xs text-blue-600 mt-1 font-medium">💬 Tienes una respuesta pendiente de leer</p>
                )}
              </div>
              <span className="text-primary text-sm shrink-0">Ver →</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
