// Detalle de reserva para el administrador
import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getAdminReservationById, updateReservationStatus, updatePaymentStatus } from "../api/adminReservationsApi";

const STATUS_CONFIG = {
  PENDING:     { label: "Pendiente de pago", pill: "bg-orange-100 text-orange-800" },
  CONFIRMED:   { label: "Confirmada",        pill: "bg-green-100 text-green-800" },
  IN_PROGRESS: { label: "En curso",          pill: "bg-blue-100 text-blue-800" },
  COMPLETED:   { label: "Completada",        pill: "bg-gray-100 text-gray-600" },
  CANCELLED:   { label: "Cancelada",         pill: "bg-red-100 text-red-800" },
};

const PAYMENT_CONFIG = {
  NO_PAYMENT:     { label: "Sin pago",      pill: "bg-gray-100 text-gray-600" },
  PAID:           { label: "Pagado",        pill: "bg-green-100 text-green-800" },
  REFUND_PENDING: { label: "En devolución", pill: "bg-yellow-100 text-yellow-800" },
  REFUNDED:       { label: "Pago devuelto", pill: "bg-blue-100 text-blue-800" },
};

function Pill({ value, config }) {
  const s = config[value] || { label: value, pill: "bg-gray-100 text-gray-600" };
  return <span className={`inline-block px-3 py-1 rounded-full text-sm font-medium ${s.pill}`}>{s.label}</span>;
}

export default function AdminReservationDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [reservation, setReservation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadReservation = async () => {
      try {
        setLoading(true);
        console.log('Cargando reserva con ID:', id);
        const data = await getAdminReservationById(id);
        console.log('Datos de reserva recibidos:', data);
        setReservation(data);
        setError(null);
      } catch (err) {
        console.error('Error cargando reserva:', err);
        setError(err.message || 'Error al cargar la reserva');
      } finally {
        setLoading(false);
      }
    };
    
    if (id) {
      loadReservation();
    }
  }, [id]);

  const handleStatusChange = async (newStatus) => {
    try {
      await updateReservationStatus(id, newStatus);
      setReservation(prev => ({ ...prev, status: newStatus }));
    } catch (err) { alert("Error: " + err.message); }
  };

  const handlePaymentStatusChange = async (newPaymentStatus) => {
    try {
      await updatePaymentStatus(id, newPaymentStatus);
      setReservation(prev => ({ ...prev, paymentStatus: newPaymentStatus }));
    } catch (err) { alert("Error: " + err.message); }
  };

  if (loading) return <div className="max-w-4xl mx-auto py-10 px-4 text-center"><p className="text-gray-600">Cargando...</p></div>;
  if (error || !reservation) return (
    <div className="max-w-4xl mx-auto py-10 px-4 text-center">
      <p className="text-red-600">{error || "Reserva no encontrada"}</p>
      <button onClick={() => navigate("/admin/reservas")} className="mt-4 px-4 py-2 bg-primary text-white rounded">Volver</button>
    </div>
  );

  return (
    <div className="max-w-4xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <div className="mb-6">
        <button onClick={() => navigate("/admin/reservas")} className="text-primary hover:underline text-sm mb-2">
          ← Volver al Gestor de Reservas
        </button>
        <h1 className="text-2xl font-bold">Detalle de Reserva #{reservation.id}</h1>
      </div>

      <div className="bg-white shadow rounded-lg overflow-hidden">
        {/* Header */}
        <div className="px-6 py-4 border-b flex justify-between items-start">
          <div>
            <h2 className="text-xl font-semibold">{reservation.carBrand} {reservation.carModel}</h2>
            <p className="text-gray-500 text-sm">{reservation.categoryName} · {reservation.carYear}</p>
            <p className="text-sm text-gray-600 mt-1">
              Cliente: <strong>{reservation.userFirstName} {reservation.userLastName}</strong> ({reservation.userEmail})
            </p>
          </div>
          <div className="flex flex-col items-end gap-2">
            <Pill value={reservation.status} config={STATUS_CONFIG} />
            <Pill value={reservation.paymentStatus} config={PAYMENT_CONFIG} />
          </div>
        </div>

        {/* Detalles */}
        <div className="px-6 py-4 grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <h3 className="font-semibold mb-3">Detalles de la Reserva</h3>
            <div className="space-y-2 text-sm">
              <p><span className="font-medium">Fecha de retiro:</span> {reservation.startDate}</p>
              <p><span className="font-medium">Fecha de entrega:</span> {reservation.endDate}</p>
              <p><span className="font-medium">Total días:</span> {reservation.totalDays}</p>
              <p><span className="font-medium">Sede de retiro:</span> {reservation.pickupBranchName}</p>
              <p><span className="font-medium">Sede de entrega:</span> {reservation.dropoffBranchName}</p>
            </div>
          </div>
          <div>
            <h3 className="font-semibold mb-3">Resumen de Costos</h3>
            <div className="space-y-2 text-sm">
              <p><span className="font-medium">Precio por día:</span> ${reservation.pricePerDay?.toLocaleString()}</p>
              <p><span className="font-medium">Días:</span> {reservation.totalDays}</p>
              <p className="font-bold text-base border-t pt-2">Total: ${reservation.totalAmount?.toLocaleString()}</p>
            </div>
          </div>
        </div>

        {/* Acciones admin */}
        <div className="px-6 py-4 border-t bg-gray-50">
          <h3 className="font-semibold mb-3 text-sm">Gestión administrativa</h3>
          <div className="flex flex-wrap gap-4">
            <div>
              <label className="block text-xs text-gray-500 mb-1">Estado de reserva</label>
              <select value={reservation.status} onChange={e => handleStatusChange(e.target.value)}
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary">
                {Object.entries(STATUS_CONFIG).map(([k, v]) => (
                  <option key={k} value={k}>{v.label}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-xs text-gray-500 mb-1">Estado de pago</label>
              <select value={reservation.paymentStatus} onChange={e => handlePaymentStatusChange(e.target.value)}
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary">
                {Object.entries(PAYMENT_CONFIG).map(([k, v]) => (
                  <option key={k} value={k}>{v.label}</option>
                ))}
              </select>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
