// Página de detalle de reserva
import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getReservationById, cancelReservation } from "../api/reservationsApi";

const STATUS_CONFIG = {
  PENDING: {
    label: "Pendiente de pago",
    pill: "bg-orange-100 text-orange-800",
    banner: "bg-orange-50 border-orange-200 text-orange-800",
    icon: "⏳",
    message: "Tienes 24 horas para completar el pago. Si no pagas en ese tiempo, la reserva será cancelada automáticamente.",
  },
  CONFIRMED: {
    label: "Confirmada",
    pill: "bg-green-100 text-green-800",
    banner: "bg-green-50 border-green-200 text-green-800",
    icon: "✅",
    message: "Tu pago fue recibido y la reserva está confirmada. El vehículo estará disponible en la sede de retiro en la fecha indicada. Puedes cancelar con al menos 7 días de anticipación.",
  },
  IN_PROGRESS: {
    label: "En curso",
    pill: "bg-blue-100 text-blue-800",
    banner: "bg-blue-50 border-blue-200 text-blue-800",
    icon: "🚗",
    message: "El período de alquiler está activo. Recuerda devolver el vehículo en la sede y fecha de entrega acordadas.",
  },
  COMPLETED: {
    label: "Completada",
    pill: "bg-gray-100 text-gray-600",
    banner: "bg-gray-50 border-gray-200 text-gray-600",
    icon: "🏁",
    message: "El alquiler ha finalizado. Gracias por usar AutoReserve.",
  },
  CANCELLED: {
    label: "Cancelada",
    pill: "bg-red-100 text-red-800",
    banner: "bg-red-50 border-red-200 text-red-800",
    icon: "❌",
    message: "Esta reserva fue cancelada.",
  },
};

const PAYMENT_CONFIG = {
  NO_PAYMENT:     { label: "Sin pago",      pill: "bg-gray-100 text-gray-600" },
  PAID:           { label: "Pagado",        pill: "bg-green-100 text-green-800" },
  REFUND_PENDING: { label: "En devolución", pill: "bg-yellow-100 text-yellow-800" },
  REFUNDED:       { label: "Pago devuelto", pill: "bg-blue-100 text-blue-800" },
};

function StatusPill({ value, config }) {
  const s = config[value] || { label: value, pill: "bg-gray-100 text-gray-600" };
  return (
    <span className={`inline-block px-3 py-1 rounded-full text-sm font-medium ${s.pill}`}>
      {s.label}
    </span>
  );
}

export default function ReservationDetail() {
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
        const data = await getReservationById(id);
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

  const handleCancel = async () => {
    if (!window.confirm("¿Estás seguro de cancelar esta reserva?")) return;
    try {
      await cancelReservation(id);
      setReservation(prev => ({ ...prev, status: "CANCELLED" }));
    } catch (err) {
      alert("Error cancelando reserva: " + err.message);
    }
  };

  if (loading) {
    return (
      <div className="max-w-4xl mx-auto py-10 px-4 sm:px-6 lg:px-8 text-center">
        <p className="text-gray-600">Cargando detalle de reserva...</p>
      </div>
    );
  }

  if (error || !reservation) {
    return (
      <div className="max-w-3xl mx-auto py-10 px-4 sm:px-6 lg:px-8 text-center">
        <h1 className="text-2xl font-bold mb-6">Reserva no encontrada</h1>
        <p className="text-red-600 mb-4">{error}</p>
        <button onClick={() => navigate("/reservas")} className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark">
          Volver a reservas
        </button>
      </div>
    );
  }

  const statusCfg = STATUS_CONFIG[reservation.status] || STATUS_CONFIG.CANCELLED;
  const canPay    = reservation.status === "PENDING";
  const canCancel = reservation.status === "PENDING" || reservation.status === "CONFIRMED";

  return (
    <div className="max-w-4xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <div className="mb-6">
        <button onClick={() => navigate("/reservas")} className="text-primary hover:underline mb-4">
          ← Volver a Mis Reservas
        </button>
        <h1 className="text-2xl font-bold">Detalle de Reserva #{reservation.id}</h1>
      </div>

      {/* Banner de estado */}
      <div className={`border rounded-lg p-4 mb-6 flex items-start gap-3 ${statusCfg.banner}`}>
        <span className="text-2xl">{statusCfg.icon}</span>
        <div>
          <p className="font-semibold">{statusCfg.label}</p>
          <p className="text-sm mt-1">{statusCfg.message}</p>
        </div>
      </div>

      <div className="bg-white shadow rounded-lg overflow-hidden">
        {/* Header */}
        <div className="px-6 py-4 border-b flex justify-between items-center">
          <div>
            <h2 className="text-xl font-semibold">{reservation.carBrand} {reservation.carModel}</h2>
            <p className="text-gray-500 text-sm">{reservation.categoryName} · {reservation.carYear}</p>
          </div>
          <div className="flex flex-col items-end gap-2">
            <StatusPill value={reservation.status} config={STATUS_CONFIG} />
            {/* Mostrar estado de pago cuando aporta información adicional */}
            {(reservation.paymentStatus === "PAID" ||
              reservation.paymentStatus === "REFUND_PENDING" ||
              reservation.paymentStatus === "REFUNDED") && (
              <StatusPill value={reservation.paymentStatus} config={PAYMENT_CONFIG} />
            )}
          </div>
        </div>

        {/* Imagen */}
        {reservation.carImage && (
          <div className="px-6 py-4">
            <img
              src={reservation.carImage}
              alt={`${reservation.carBrand} ${reservation.carModel}`}
              className="w-full h-64 object-cover rounded-lg"
            />
          </div>
        )}

        {/* Detalles */}
        <div className="px-6 py-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="font-semibold text-lg mb-3">Información del Vehículo</h3>
              <div className="space-y-2 text-sm">
                <p><span className="font-medium">Marca:</span> {reservation.carBrand}</p>
                <p><span className="font-medium">Modelo:</span> {reservation.carModel}</p>
                <p><span className="font-medium">Año:</span> {reservation.carYear}</p>
                <p><span className="font-medium">Categoría:</span> {reservation.categoryName}</p>
              </div>
            </div>
            <div>
              <h3 className="font-semibold text-lg mb-3">Detalles de la Reserva</h3>
              <div className="space-y-2 text-sm">
                <p><span className="font-medium">Fecha de retiro:</span> {reservation.startDate}</p>
                <p><span className="font-medium">Fecha de entrega:</span> {reservation.endDate}</p>
                <p><span className="font-medium">Total días:</span> {reservation.totalDays}</p>
                <p><span className="font-medium">Sede de retiro:</span> {reservation.pickupBranchName}</p>
                <p><span className="font-medium">Sede de entrega:</span> {reservation.dropoffBranchName}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Costos */}
        <div className="px-6 py-4 bg-gray-50">
          <h3 className="font-semibold text-lg mb-3">Resumen de Costos</h3>
          <div className="space-y-2 text-sm">
            <div className="flex justify-between">
              <span>Precio por día</span>
              <span>${reservation.pricePerDay?.toLocaleString()}</span>
            </div>
            <div className="flex justify-between">
              <span>Número de días</span>
              <span>{reservation.totalDays}</span>
            </div>
            <div className="flex justify-between font-bold text-base border-t pt-2">
              <span>Total</span>
              <span className="text-primary">${reservation.totalAmount?.toLocaleString()}</span>
            </div>
          </div>
        </div>

        {/* Acciones */}
        {(canPay || canCancel) && (
          <div className="px-6 py-4 border-t flex gap-3">
            {canPay && (
              <button
                onClick={() => navigate("/reservas/checkout", {
                  state: {
                    reservationData: {
                      reservationId: reservation.id,
                      carBrand: reservation.carBrand,
                      carModel: reservation.carModel,
                      startDate: reservation.startDate,
                      endDate: reservation.endDate,
                      pickupBranchName: reservation.pickupBranchName,
                      dropoffBranchName: reservation.dropoffBranchName,
                      estimatedTotal: reservation.totalAmount,
                    },
                  },
                })}
                className="px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition"
              >
                💳 Pagar ahora
              </button>
            )}
            {canCancel && (
              <button
                onClick={handleCancel}
                className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition"
              >
                Cancelar Reserva
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
