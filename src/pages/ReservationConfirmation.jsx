// src/pages/ReservationConfirmation.jsx
import { Link, useLocation } from "react-router-dom";

export default function ReservationConfirmation() {
  const location = useLocation();
  const reserva = location.state?.reserva; // 📌 recibe datos desde navegación

  if (!reserva) {
    return (
      <div className="text-center py-12">
        <h2 className="text-2xl font-bold">No hay reserva confirmada</h2>
        <p className="mt-4 text-gray-600">
          Parece que aún no has confirmado ninguna reserva.
        </p>
        <Link
          to="/"
          className="mt-6 inline-block px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition"
        >
          Volver al inicio
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto py-10 px-4 sm:px-6 lg:px-8 text-center">
      <h1 className="text-2xl font-bold mb-6">✅ Reserva Confirmada</h1>

      <div className="bg-white shadow rounded-lg p-6 space-y-4">
        <p>
          <strong>ID de reserva:</strong> {reserva.id}
        </p>
        <p>
          <strong>Auto:</strong> {reserva.car.name}
        </p>
        <p>
          <strong>Ciudad de retiro:</strong> {reserva.filters.pickupCity}
        </p>
        <p>
          <strong>Ciudad de entrega:</strong> {reserva.filters.dropoffCity}
        </p>
        <p>
          <strong>Fechas:</strong> {reserva.filters.startDate} -{" "}
          {reserva.filters.endDate}
        </p>
        <p>
          <strong>Días:</strong> {reserva.dias}
        </p>
        <p className="text-lg font-semibold text-primary">
          <strong>Total:</strong> ${reserva.total}
        </p>
      </div>

      <div className="mt-8 flex flex-col sm:flex-row justify-center gap-4">
        <Link
          to="/reservas"
          className="px-4 py-2 bg-secondary text-white rounded hover:bg-secondary-dark transition"
        >
          Ver mis reservas
        </Link>
        <Link
          to="/"
          className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition"
        >
          Volver al inicio
        </Link>
      </div>
    </div>
  );
}
