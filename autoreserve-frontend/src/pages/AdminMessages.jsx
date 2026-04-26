// Gestor de mensajes de soporte para el administrador
import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { getAdminTickets, getAdminTicketDetail, adminReply, adminCloseTicket } from "../api/contactApi";

const normalize = (str) =>
  String(str ?? "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();

const STATUS_CONFIG = {
  OPEN:        { label: "Abierto",       pill: "bg-red-100 text-red-700" },
  IN_PROGRESS: { label: "En revisión",   pill: "bg-yellow-100 text-yellow-700" },
  ANSWERED:    { label: "Respondido",    pill: "bg-blue-100 text-blue-700" },
  CLOSED:      { label: "Cerrado",       pill: "bg-gray-100 text-gray-500" },
};

const TYPE_CONFIG = {
  PREGUNTA:     { label: "Pregunta",     color: "text-blue-600" },
  QUEJA:        { label: "Queja",        color: "text-orange-600" },
  RECLAMO:      { label: "Reclamo",      color: "text-red-600" },
  SOLICITUD:    { label: "Solicitud",    color: "text-purple-600" },
  FELICITACION: { label: "Felicitación", color: "text-green-600" },
};

function StatusPill({ status }) {
  const s = STATUS_CONFIG[status] || { label: status, pill: "bg-gray-100 text-gray-600" };
  return <span className={`inline-block px-2 py-0.5 rounded-full text-xs font-medium ${s.pill}`}>{s.label}</span>;
}

function SortIcon({ col, sortCol, sortDir }) {
  if (sortCol !== col) return <span className="ml-1 text-gray-300">↕</span>;
  return <span className="ml-1">{sortDir === "asc" ? "↑" : "↓"}</span>;
}

// Componente del hilo de conversación
function TicketThread({ ticketId, onClose }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [reply, setReply] = useState("");
  const [sending, setSending] = useState(false);
  const [closing, setClosing] = useState(false);
  const bottomRef = useRef(null);

  const load = async () => {
    try {
      const res = await getAdminTicketDetail(ticketId);
      setData(res);
    } catch (err) {
      alert("Error: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, [ticketId]);
  useEffect(() => { bottomRef.current?.scrollIntoView({ behavior: "smooth" }); }, [data?.replies]);

  const handleReply = async () => {
    if (!reply.trim()) return;
    setSending(true);
    try {
      await adminReply(ticketId, reply);
      setReply("");
      await load();
    } catch (err) { alert("Error: " + err.message); }
    finally { setSending(false); }
  };

  const handleClose = async () => {
    if (!window.confirm("¿Cerrar este ticket?")) return;
    setClosing(true);
    try {
      await adminCloseTicket(ticketId);
      await load();
    } catch (err) { alert("Error: " + err.message); }
    finally { setClosing(false); }
  };

  if (loading) return <div className="p-6 text-center text-gray-500">Cargando conversación...</div>;
  if (!data) return null;

  const { ticket, replies } = data;
  const isClosed = ticket.status === "CLOSED";

  return (
    <div className="flex flex-col h-full">
      {/* Header del ticket */}
      <div className="px-6 py-4 border-b bg-gray-50 flex items-start justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 flex-wrap">
            <span className="font-bold text-gray-800">#{ticket.id} — {ticket.subject}</span>
            <StatusPill status={ticket.status} />
            <span className={`text-xs font-medium ${TYPE_CONFIG[ticket.type]?.color || "text-gray-600"}`}>
              {TYPE_CONFIG[ticket.type]?.label || ticket.type}
            </span>
          </div>
          <p className="text-xs text-gray-500 mt-1">
            De: <strong>{ticket.senderName}</strong> ({ticket.senderEmail}) ·{" "}
            {new Date(ticket.createdAt).toLocaleString()}
          </p>
        </div>
        <div className="flex gap-2 shrink-0">
          {!isClosed && (
            <button onClick={handleClose} disabled={closing}
              className="text-xs px-3 py-1 border border-gray-300 rounded hover:bg-gray-100 transition disabled:opacity-50">
              {closing ? "..." : "Cerrar ticket"}
            </button>
          )}
          <button onClick={onClose}
            className="text-xs px-3 py-1 border border-gray-300 rounded hover:bg-gray-100 transition">
            ✕ Cerrar
          </button>
        </div>
      </div>

      {/* Hilo de mensajes */}
      <div className="flex-1 overflow-y-auto px-6 py-4 space-y-4 bg-white" style={{ maxHeight: "400px" }}>
        {replies.map(r => (
          <div key={r.id} className={`flex ${r.sentBy === "ADMIN" ? "justify-end" : "justify-start"}`}>
            <div className={`max-w-[75%] rounded-2xl px-4 py-3 text-sm shadow-sm ${
              r.sentBy === "ADMIN"
                ? "bg-primary text-white rounded-br-none"
                : "bg-gray-100 text-gray-800 rounded-bl-none"
            }`}>
              <p className={`text-xs font-semibold mb-1 ${r.sentBy === "ADMIN" ? "text-blue-100" : "text-gray-500"}`}>
                {r.authorName}
              </p>
              <p className="whitespace-pre-line leading-relaxed">{r.content}</p>
              <p className={`text-xs mt-2 ${r.sentBy === "ADMIN" ? "text-blue-200" : "text-gray-400"}`}>
                {new Date(r.createdAt).toLocaleString()}
              </p>
            </div>
          </div>
        ))}
        <div ref={bottomRef} />
      </div>

      {/* Caja de respuesta */}
      {!isClosed ? (
        <div className="px-6 py-4 border-t bg-gray-50">
          <div className="flex gap-3">
            <textarea
              value={reply}
              onChange={e => setReply(e.target.value)}
              placeholder="Escribe tu respuesta..."
              rows={3}
              className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-primary focus:border-primary resize-none"
              onKeyDown={e => { if (e.key === "Enter" && e.ctrlKey) handleReply(); }}
            />
            <button onClick={handleReply} disabled={sending || !reply.trim()}
              className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark disabled:opacity-50 transition text-sm self-end">
              {sending ? "..." : "Enviar"}
            </button>
          </div>
          <p className="text-xs text-gray-400 mt-1">Ctrl+Enter para enviar</p>
        </div>
      ) : (
        <div className="px-6 py-3 border-t bg-gray-50 text-center text-sm text-gray-500">
          Este ticket está cerrado. No se pueden agregar más respuestas.
        </div>
      )}
    </div>
  );
}

export default function AdminMessages() {
  const navigate = useNavigate();
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedId, setSelectedId] = useState(null);

  const [search, setSearch] = useState("");
  const [filterStatus, setFilterStatus] = useState("");
  const [filterType, setFilterType] = useState("");
  const [sortCol, setSortCol] = useState("createdAt");
  const [sortDir, setSortDir] = useState("desc");

  useEffect(() => {
    getAdminTickets()
      .then(setTickets)
      .catch(err => alert("Error: " + err.message))
      .finally(() => setLoading(false));
  }, []);

  const handleSort = (col) => {
    if (sortCol === col) setSortDir(d => d === "asc" ? "desc" : "asc");
    else { setSortCol(col); setSortDir("asc"); }
  };

  const filtered = tickets.filter(t => {
    const term = normalize(search);
    const matchSearch = !search ||
      String(t.id).includes(term) ||
      normalize(t.senderName).includes(term) ||
      normalize(t.senderEmail).includes(term) ||
      normalize(t.subject).includes(term);
    const matchStatus = !filterStatus || t.status === filterStatus;
    const matchType   = !filterType   || t.type === filterType;
    return matchSearch && matchStatus && matchType;
  });

  const sorted = [...filtered].sort((a, b) => {
    const va = normalize(a[sortCol] ?? "");
    const vb = normalize(b[sortCol] ?? "");
    return sortDir === "asc" ? va.localeCompare(vb) : vb.localeCompare(va);
  });

  const hasFilters = search || filterStatus || filterType;

  if (loading) return (
    <div className="max-w-7xl mx-auto py-10 px-4 text-center">
      <p className="text-gray-600">Cargando mensajes...</p>
    </div>
  );

  return (
    <div className="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <div className="mb-6">
        <button onClick={() => navigate("/admin")} className="text-primary hover:underline text-sm mb-2">
          ← Volver al Dashboard
        </button>
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-neutral-dark">💬 Gestor de Mensajes</h1>
            <p className="text-sm text-gray-500 mt-1">{sorted.length} mensaje(s)</p>
          </div>
        </div>
      </div>

      {/* Filtros */}
      <div className="bg-white shadow-sm rounded-lg p-4 mb-6">
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
          <input type="text" placeholder="Buscar ID, nombre, email, asunto..."
            value={search} onChange={e => setSearch(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary" />
          <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary">
            <option value="">Todos los estados</option>
            {Object.entries(STATUS_CONFIG).map(([k, v]) => (
              <option key={k} value={k}>{v.label}</option>
            ))}
          </select>
          <select value={filterType} onChange={e => setFilterType(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary">
            <option value="">Todos los tipos</option>
            {Object.entries(TYPE_CONFIG).map(([k, v]) => (
              <option key={k} value={k}>{v.label}</option>
            ))}
          </select>
        </div>
        {hasFilters && (
          <button onClick={() => { setSearch(""); setFilterStatus(""); setFilterType(""); }}
            className="mt-3 text-xs text-gray-500 hover:text-red-500 underline">
            Limpiar filtros
          </button>
        )}
      </div>

      <div className={`grid gap-6 ${selectedId ? "grid-cols-1 lg:grid-cols-2" : "grid-cols-1"}`}>
        {/* Tabla */}
        <div className="bg-white shadow rounded-lg overflow-x-auto">
          <table className="w-full table-auto text-sm">
            <thead className="bg-gray-50 border-b">
              <tr>
                {[
                  { col: "id",          label: "ID" },
                  { col: "senderName",  label: "Remitente" },
                  { col: "subject",     label: "Asunto" },
                  { col: "type",        label: "Tipo" },
                  { col: "status",      label: "Estado" },
                  { col: "createdAt",   label: "Fecha" },
                ].map(({ col, label }) => (
                  <th key={col} onClick={() => handleSort(col)}
                    className="px-4 py-3 text-left font-semibold text-gray-700 cursor-pointer hover:bg-gray-100 select-none whitespace-nowrap">
                    {label}<SortIcon col={col} sortCol={sortCol} sortDir={sortDir} />
                  </th>
                ))}
                <th className="px-4 py-3 text-center font-semibold text-gray-700">Acción</th>
              </tr>
            </thead>
            <tbody>
              {sorted.map(t => (
                <tr key={t.id}
                  className={`border-t hover:bg-gray-50 transition cursor-pointer ${selectedId === t.id ? "bg-blue-50" : ""}`}
                  onClick={() => setSelectedId(t.id)}>
                  <td className="px-4 py-3 font-mono text-xs text-gray-500">#{t.id}</td>
                  <td className="px-4 py-3">
                    <p className="font-medium text-gray-800">{t.senderName}</p>
                    <p className="text-xs text-gray-400">{t.senderEmail}</p>
                  </td>
                  <td className="px-4 py-3 text-sm max-w-[200px] truncate">{t.subject}</td>
                  <td className="px-4 py-3">
                    <span className={`text-xs font-medium ${TYPE_CONFIG[t.type]?.color || "text-gray-600"}`}>
                      {TYPE_CONFIG[t.type]?.label || t.type}
                    </span>
                  </td>
                  <td className="px-4 py-3"><StatusPill status={t.status} /></td>
                  <td className="px-4 py-3 text-xs text-gray-500">
                    {new Date(t.createdAt).toLocaleDateString()}
                  </td>
                  <td className="px-4 py-3 text-center">
                    <button onClick={e => { e.stopPropagation(); setSelectedId(t.id); }}
                      className="text-xs px-3 py-1 bg-primary text-white rounded hover:bg-primary-dark transition">
                      Ver hilo
                    </button>
                  </td>
                </tr>
              ))}
              {sorted.length === 0 && (
                <tr>
                  <td colSpan="7" className="text-center py-10 text-gray-500">
                    {hasFilters ? "No hay mensajes con los filtros aplicados." : "No hay mensajes registrados."}
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Panel de conversación */}
        {selectedId && (
          <div className="bg-white shadow rounded-lg overflow-hidden flex flex-col">
            <TicketThread
              ticketId={selectedId}
              onClose={() => setSelectedId(null)}
            />
          </div>
        )}
      </div>
    </div>
  );
}
