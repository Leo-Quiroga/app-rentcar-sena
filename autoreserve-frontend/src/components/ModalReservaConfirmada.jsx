// Componente para mostrar el modal de reserva confirmada
// src/components/ModalReservaConfirmada.jsx
import { useNavigate } from "react-router-dom";

export default function ModalReservaConfirmada({ reserva, onClose }) {
  const navigate = useNavigate();
  if (!reserva) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
      <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-lg text-center">
        <h2 className="text-2xl font-bold text-green-600 mb-4">¡Reserva confirmada! 🎉</h2>

        <p><strong>Auto:</strong> {reserva.car.name}</p>
        <p><strong>Categoría:</strong> {reserva.car.category}</p>
        <p><strong>Ciudad Retiro:</strong> {reserva.filters.pickupCity}</p>
        <p><strong>Ciudad Entrega:</strong> {reserva.filters.dropoffCity}</p>
        <p><strong>Fechas:</strong> {reserva.filters.startDate} → {reserva.filters.endDate}</p>
        <p><strong>Total Pagado:</strong> ${reserva.total}</p>

        <div className="flex justify-center gap-3 mt-6">
          <button
            onClick={onClose}
            className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"
          >
            Cerrar
          </button>
          <button
            onClick={() => {
              onClose && onClose();
              navigate(`/reservas/${reserva.id}`);
            }}
            className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
          >
            Ver Reserva
          </button>
          <button
            onClick={() => {
              onClose && onClose();
              navigate("/reservas/checkout", { state: { reserva } });
            }}
            className="px-4 py-2 bg-secondary text-gray-900 rounded hover:bg-secondary-dark"
          >
            Pagar ahora
          </button>
        </div>
      </div>
    </div>
  );
}
