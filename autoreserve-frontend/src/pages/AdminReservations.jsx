// Gestor de reservas del administrador
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getAdminReservations, updateReservationStatus, updatePaymentStatus } from "../api/adminReservationsApi";

const normalize = (str) =>
  String(str ?? "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();

const STATUS_CONFIG = {
  PENDING:     { label: "Pendiente de pago", pill: "bg-orange-100 text-orange-800" },
  CONFIRMED:   { label: "Confirmada",        pill: "bg-green-100 text-green-800" },
  IN_PROGRESS: { label: "En curso",          pill: "bg-blue-100 text-blue-800" },
  COMPLETED:   { label: "Completada",        pill: "bg-gray-100 text-gray-600" },
  CANCELLED:   { label: "Cancelada",         pill: "bg-red-100 text-red-800" },
};

const PAYMENT_CONFIG = {
  NO_PAYMENT:     { label: "Sin pago",       pill: "bg-gray-100 text-gray-600" },
  PAID:           { label: "Pagado",         pill: "bg-green-100 text-green-800" },
  REFUND_PENDING: { label: "En devolución",  pill: "bg-yellow-100 text-yellow-800" },
  REFUNDED:       { label: "Pago devuelto",  pill: "bg-blue-100 text-blue-800" },
};

function Pill({ value, config }) {
  const s = config[value] || { label: value, pill: "bg-gray-100 text-gray-600" };
  return (
    <span className={`inline-block px-2 py-0.5 rounded-full text-xs font-medium ${s.pill}`}>
      {s.label}
    </span>
  );
}

function SortIcon({ col, sortCol, sortDir }) {
  if (sortCol !== col) return <span className="ml-1 text-gray-300">↕</span>;
  return <span className="ml-1">{sortDir === "asc" ? "↑" : "↓"}</span>;
}

export default function AdminReservations() {
  const navigate = useNavigate();
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [search, setSearch] = useState("");
  const [filterStatus, setFilterStatus] = useState("");
  const [filterPayment, setFilterPayment] = useState("");
  const [filterDateFrom, setFilterDateFrom] = useState("");
  const [filterDateTo, setFilterDateTo] = useState("");
  const [sortCol, setSortCol] = useState("startDate");
  const [sortDir, setSortDir] = useState("desc");

  useEffect(() => { loadReservations(); }, []);

  const loadReservations = async () => {
    try {
      setLoading(true);
      const response = await getAdminReservations();
      // El backend retorna { success: true, data: [...] }
      const data = response.data || response;
      setReservations(Array.isArray(data) ? data : []);
      setError(null);
    } catch (err) {
      console.error('Error cargando reservas:', err);
      setError(err.message || 'Error al cargar las reservas');
    } finally {
      setLoading(false);
    }
  };

  const handleSort = (col) => {
    if (sortCol === col) setSortDir(d => d === "asc" ? "desc" : "asc");
    else { setSortCol(col); setSortDir("asc"); }
  };

  const handleStatusChange = async (reservationId, newStatus) => {
    try {
      await updateReservationStatus(reservationId, newStatus);
      setReservations(prev =>
        prev.map(r => r.id === reservationId ? { ...r, status: newStatus } : r)
      );
    } catch (err) {
      alert("Error actualizando estado: " + err.message);
    }
  };

  const handlePaymentStatusChange = async (reservationId, newPaymentStatus) => {
    try {
      await updatePaymentStatus(reservationId, newPaymentStatus);
      setReservations(prev =>
        prev.map(r => r.id === reservationId ? { ...r, paymentStatus: newPaymentStatus } : r)
      );
    } catch (err) {
      alert("Error actualizando estado de pago: " + err.message);
    }
  };

  const filtered = reservations.filter(r => {
    const term = normalize(search);
    const matchSearch = !search ||
      String(r.id).includes(term) ||
      normalize(r.carBrand).includes(term) ||
      normalize(r.carModel).includes(term) ||
      normalize(r.userFirstName).includes(term) ||
      normalize(r.userLastName).includes(term) ||
      normalize(r.userEmail).includes(term) ||
      normalize(r.pickupBranchName).includes(term);
    const matchStatus  = !filterStatus  || r.status === filterStatus;
    const matchPayment = !filterPayment || r.paymentStatus === filterPayment;
    const matchFrom    = !filterDateFrom || r.startDate >= filterDateFrom;
    const matchTo      = !filterDateTo   || r.startDate <= filterDateTo;
    return matchSearch && matchStatus && matchPayment && matchFrom && matchTo;
  });

  const sorted = [...filtered].sort((a, b) => {
    let va = a[sortCol] ?? "";
    let vb = b[sortCol] ?? "";
    if (typeof va === "number") return sortDir === "asc" ? va - vb : vb - va;
    return sortDir === "asc"
      ? String(va).localeCompare(String(vb))
      : String(vb).localeCompare(String(va));
  });

  const clearFilters = () => {
    setSearch(""); setFilterStatus(""); setFilterPayment("");
    setFilterDateFrom(""); setFilterDateTo("");
  };

  const hasFilters = search || filterStatus || filterPayment || filterDateFrom || filterDateTo;

  if (loading) return (
    <div className="max-w-7xl mx-auto py-10 px-4 text-center">
      <p className="text-gray-600">Cargando reservas...</p>
    </div>
  );

  if (error) return (
    <div className="max-w-7xl mx-auto py-10 px-4 text-center">
      <p className="text-red-600">Error: {error}</p>
      <button onClick={loadReservations} className="mt-4 px-4 py-2 bg-primary text-white rounded">Reintentar</button>
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
            <h1 className="text-2xl font-bold text-neutral-dark">📋 Gestor de Reservas</h1>
            <p className="text-sm text-gray-500 mt-1">{sorted.length} reserva(s) encontrada(s)</p>
          </div>
          <button onClick={() => navigate("/")}
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition text-sm">
            + Nueva Reserva
          </button>
        </div>
      </div>

      {/* Filtros */}
      <div className="bg-white shadow-sm rounded-lg p-4 mb-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
          <input type="text" placeholder="Buscar ID, auto, cliente, sede..."
            value={search} onChange={e => setSearch(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary" />
          <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary">
            <option value="">Todos los estados</option>
            {Object.entries(STATUS_CONFIG).map(([k, v]) => (
              <option key={k} value={k}>{v.label}</option>
            ))}
          </select>
          <select value={filterPayment} onChange={e => setFilterPayment(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary">
            <option value="">Todos los pagos</option>
            {Object.entries(PAYMENT_CONFIG).map(([k, v]) => (
              <option key={k} value={k}>{v.label}</option>
            ))}
          </select>
          <input type="date" value={filterDateFrom} onChange={e => setFilterDateFrom(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary"
            title="Desde fecha de retiro" />
          <input type="date" value={filterDateTo} onChange={e => setFilterDateTo(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary"
            title="Hasta fecha de retiro" />
        </div>
        {hasFilters && (
          <button onClick={clearFilters} className="mt-3 text-xs text-gray-500 hover:text-red-500 underline">
            Limpiar filtros
          </button>
        )}
      </div>

      {/* Tabla */}
      <div className="bg-white shadow rounded-lg overflow-x-auto">
        <table className="w-full table-auto text-sm">
          <thead className="bg-gray-50 text-left border-b">
            <tr>
              {[
                { col: "id",             label: "ID" },
                { col: "carBrand",       label: "Vehículo" },
                { col: "userLastName",   label: "Cliente" },
                { col: "startDate",      label: "Fechas" },
                { col: "pickupBranchName", label: "Sedes" },
                { col: "totalDays",      label: "Días" },
                { col: "totalAmount",    label: "Total" },
                { col: "status",         label: "Estado" },
                { col: "paymentStatus",  label: "Pago" },
              ].map(({ col, label }) => (
                <th key={col} onClick={() => handleSort(col)}
                  className="px-4 py-3 font-semibold text-gray-700 cursor-pointer hover:bg-gray-100 select-none whitespace-nowrap">
                  {label}<SortIcon col={col} sortCol={sortCol} sortDir={sortDir} />
                </th>
              ))}
              <th className="px-4 py-3 font-semibold text-gray-700 text-center">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {sorted.map(r => (
              <tr key={r.id} className="border-t hover:bg-gray-50 transition">
                <td className="px-4 py-3 font-medium text-gray-700">#{r.id}</td>
                <td className="px-4 py-3">
                  <p className="font-medium">{r.carBrand} {r.carModel}</p>
                  <p className="text-xs text-gray-500">{r.categoryName} · {r.carYear}</p>
                </td>
                <td className="px-4 py-3">
                  <p className="font-medium">{r.userFirstName} {r.userLastName}</p>
                  <p className="text-xs text-gray-500">#{r.userId} · {r.userEmail}</p>
                </td>
                <td className="px-4 py-3 text-xs">
                  <p><span className="font-medium">Retiro:</span> {r.startDate}</p>
                  <p><span className="font-medium">Entrega:</span> {r.endDate}</p>
                </td>
                <td className="px-4 py-3 text-xs">
                  <p><span className="font-medium">↑</span> {r.pickupBranchName}</p>
                  <p><span className="font-medium">↓</span> {r.dropoffBranchName}</p>
                </td>
                <td className="px-4 py-3 text-center">{r.totalDays}</td>
                <td className="px-4 py-3 font-medium">${r.totalAmount?.toLocaleString()}</td>
                <td className="px-4 py-3">
                  <select value={r.status} onChange={e => handleStatusChange(r.id, e.target.value)}
                    className={`px-2 py-1 rounded text-xs font-medium border border-gray-200 cursor-pointer ${STATUS_CONFIG[r.status]?.pill || "bg-gray-100 text-gray-600"}`}>
                    {Object.entries(STATUS_CONFIG).map(([k, v]) => (
                      <option key={k} value={k}>{v.label}</option>
                    ))}
                  </select>
                </td>
                <td className="px-4 py-3">
                  {/* Admin puede editar estado de pago */}
                  <select value={r.paymentStatus} onChange={e => handlePaymentStatusChange(r.id, e.target.value)}
                    className={`px-2 py-1 rounded text-xs font-medium border border-gray-200 cursor-pointer ${PAYMENT_CONFIG[r.paymentStatus]?.pill || "bg-gray-100 text-gray-600"}`}>
                    {Object.entries(PAYMENT_CONFIG).map(([k, v]) => (
                      <option key={k} value={k}>{v.label}</option>
                    ))}
                  </select>
                </td>
                <td className="px-4 py-3">
                  <div className="flex gap-1 justify-center flex-wrap">
                    {r.status !== "CANCELLED" && r.status !== "COMPLETED" && (
                      <button onClick={() => handleStatusChange(r.id, "CANCELLED")}
                        className="text-xs px-2 py-1 text-red-600 border border-red-200 rounded hover:bg-red-50 transition">
                        Cancelar
                      </button>
                    )}
                    {/* Fix: navegar a ruta del admin, no del cliente */}
                    <button onClick={() => navigate(`/admin/reservas/${r.id}`)}
                      className="text-xs px-2 py-1 bg-primary text-white rounded hover:bg-primary-dark transition">
                      Ver
                    </button>
                  </div>
                </td>
              </tr>
            ))}
            {sorted.length === 0 && (
              <tr>
                <td colSpan="10" className="text-center py-10 text-gray-500">
                  {hasFilters ? "No hay reservas con los filtros aplicados." : "No hay reservas registradas."}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
