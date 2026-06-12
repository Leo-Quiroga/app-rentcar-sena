// Página de factura para una reserva específica
import { useParams, Link } from "react-router-dom";
import { useState, useEffect } from "react";
import { getReservationById } from "../api/reservationsApi";

export default function Invoice() {
  const { id } = useParams();
  const [reserva, setReserva] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Cargar datos de la reserva
  useEffect(() => {
    const loadReservation = async () => {
      try {
        setLoading(true);
        const reservationData = await getReservationById(id);
        setReserva(reservationData);
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

  // Estados de carga y error
  if (loading) {
    return (
      <div className="max-w-3xl mx-auto py-10 px-4 sm:px-6 lg:px-8 text-center">
        <p className="text-gray-600">Cargando factura...</p>
      </div>
    );
  }

  if (error || !reserva) {
    return (
      <div className="max-w-3xl mx-auto py-10 px-4 sm:px-6 lg:px-8 text-center">
        <h1 className="text-2xl font-bold mb-4">Factura no encontrada</h1>
        <p className="text-gray-600 mb-6">
          {error || 'No existe una factura para la reserva solicitada.'}
        </p>
        <Link
          to="/reservas"
          className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition"
        >
          Volver a mis reservas
        </Link>
      </div>
    );
  }
  // Renderizar factura de la reserva
  return (
    <div className="max-w-4xl mx-auto py-10 px-4 sm:px-6 lg:px-8 bg-white shadow rounded-lg">
      {/* Encabezado */}
      <header className="border-b pb-6 mb-6">
        <h1 className="text-2xl font-bold text-neutral-dark">Factura</h1>
        <p className="text-sm text-gray-500">ID de factura: {reserva.id}</p>
        <p className="text-sm text-gray-500">
          Fecha de emisión: {new Date(reserva.createdAt).toLocaleDateString()}
        </p>
      </header>

      {/* Datos de la reserva */}
      <section className="space-y-3 mb-6">
        <h2 className="text-lg font-semibold">Detalle de la Reserva</h2>
        <p>
          <strong>Auto:</strong> {reserva.car?.name || reserva.carModel?.name || 'N/A'}
        </p>
        <p>
          <strong>Categoría:</strong> {reserva.car?.category || reserva.category?.name || 'N/A'}
        </p>
        <p>
          <strong>Sede de Retiro:</strong> {reserva.pickupBranch?.name || reserva.filters?.pickupCity || 'N/A'}
        </p>
        <p>
          <strong>Sede de Entrega:</strong> {reserva.dropoffBranch?.name || reserva.filters?.dropoffCity || 'N/A'}
        </p>
        <p>
          <strong>Fechas:</strong> {reserva.startDate || reserva.filters?.startDate} →{" "}
          {reserva.endDate || reserva.filters?.endDate}
        </p>
        <p>
          <strong>Días:</strong> {reserva.totalDays || reserva.dias || 'N/A'}
        </p>
      </section>

      {/* Resumen de costos */}
      <section className="border-t pt-6">
        <h2 className="text-lg font-semibold mb-4">Resumen de Pago</h2>
        <div className="flex justify-between text-sm mb-2">
          <span>Precio por día</span>
          <span>${reserva.pricePerDay || reserva.car?.pricePerDay || 'N/A'}</span>
        </div>
        <div className="flex justify-between text-sm mb-2">
          <span>Días reservados</span>
          <span>{reserva.totalDays || reserva.dias || 'N/A'}</span>
        </div>
        <div className="flex justify-between text-base font-bold border-t pt-2">
          <span>Total Pagado</span>
          <span>${reserva.totalAmount || reserva.total || 'N/A'}</span>
        </div>
      </section>

      {/* Acciones */}
      <div className="flex justify-end gap-3 mt-8">
        <Link
          to="/reservas"
          className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 transition"
        >
          Volver a mis reservas
        </Link>
        <button
          onClick={() => alert("Función futura: Descargar PDF")}
          className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition"
        >
          Descargar PDF
        </button>
      </div>
    </div>
  );
}
