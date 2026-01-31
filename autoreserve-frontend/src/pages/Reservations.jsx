// Página de reservas
import { Link } from "react-router-dom";
import { mockReservations } from "../data/mockReservations";

export default function Reservations() {
  const reservations = mockReservations;
  // Renderizar lista de reservas
  return (
    <div className="max-w-5xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <h1 className="text-2xl font-bold mb-6">📅 Mis Reservas</h1>

      {reservations.length === 0 ? (
        <p className="text-gray-600">No tienes reservas todavía.</p>
      ) : (
        <div className="space-y-4">
          {reservations.map((reserva) => (
            <div
              key={reserva.id}
              className="bg-white shadow rounded-lg p-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4"
            >
              <div>
                <h2 className="text-lg font-semibold text-neutral-dark">
                  {reserva.car.name}
                </h2>
                <p className="text-sm text-gray-600">
                  {reserva.filters.pickupCity} → {reserva.filters.dropoffCity}
                </p>
                <p className="text-sm text-gray-600">
                  {reserva.filters.startDate} - {reserva.filters.endDate}
                </p>
                <p className="text-sm font-medium text-primary">
                  Total: ${reserva.total}
                </p>
              </div>

              <Link
                to={`/reservas/${reserva.id}`}
                className="text-sm px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition"
              >
                Ver detalle
              </Link>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
