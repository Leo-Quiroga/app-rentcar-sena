// src/pages/ReservationDetail.jsx
import { useParams, useNavigate } from "react-router-dom";
import { mockReservations } from "../data/mockReservations";

export default function ReservationDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const reserva = mockReservations.find((r) => String(r.id) === String(id));

  if (!reserva) {
    return (
      <div className="max-w-3xl mx-auto py-10 px-4 sm:px-6 lg:px-8 text-center">
        <h1 className="text-2xl font-bold mb-6">Reserva no encontrada</h1>
        <button
          onClick={() => navigate("/reservas")}
          className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
        >
          Volver a reservas
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <h1 className="text-2xl font-bold mb-6">Detalle de la Reserva</h1>

      <div className="bg-white shadow rounded-lg p-6 space-y-4">
        <p><strong>ID de reserva:</strong> {reserva.id}</p>
        <p><strong>Auto:</strong> {reserva.car.name}</p>
        <p><strong>Ciudad de retiro:</strong> {reserva.pickupCity}</p>
        <p><strong>Ciudad de entrega:</strong> {reserva.dropoffCity}</p>
        <p><strong>Fechas:</strong> {reserva.startDate} - {reserva.endDate}</p>
        <p><strong>Precio total:</strong> ${reserva.total}</p>
      </div>

      {/* Botón hacia Checkout */}
      <div className="flex justify-end mt-6">
        <button
          onClick={() =>
            navigate("/reservas/checkout", { state: { reserva } })
          }
          className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
        >
          Ir a Checkout
        </button>
      </div>
    </div>
  );
}
