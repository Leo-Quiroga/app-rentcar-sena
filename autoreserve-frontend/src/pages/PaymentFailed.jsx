// Pantalla que muestra el estado de pago fallido
import { useLocation, useNavigate } from "react-router-dom";

export default function PaymentFailed() {
  const location = useLocation();
  const navigate = useNavigate();
  const { reason, reservationData } = location.state || {};

  const handleRetry = () => {
    navigate("/reservas/checkout", { state: { reservationData } });
  };

  return (
    <div className="max-w-2xl mx-auto py-16 px-4 sm:px-6 lg:px-8 text-center">
      <div className="mb-6">
        <span className="text-6xl">❌</span>
      </div>

      <h1 className="text-2xl font-bold text-red-600 mb-4">
        El pago no se pudo procesar
      </h1>

      {reason && (
        <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg p-4 mb-6 text-sm">
          <strong>Motivo:</strong> {reason}
        </div>
      )}

      <p className="text-gray-600 mb-4">
        Tu reserva permanece en estado <strong>pendiente de pago</strong> por 24 horas.
        Puedes intentar el pago nuevamente con otra tarjeta.
      </p>

      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-8 text-xs text-blue-800 text-left">
        <p className="font-semibold mb-2">🧪 Recuerda las tarjetas de prueba:</p>
        <ul className="space-y-1">
          <li><span className="font-mono">4111 1111 1111 1111</span> — ✅ Pago exitoso</li>
          <li><span className="font-mono">4000 0000 0000 0002</span> — ❌ Tarjeta declinada</li>
          <li><span className="font-mono">4000 0000 0000 9995</span> — ❌ Fondos insuficientes</li>
        </ul>
      </div>

      <div className="flex justify-center gap-4">
        {reservationData && (
          <button
            onClick={handleRetry}
            className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition"
          >
            Reintentar pago
          </button>
        )}
        <button
          onClick={() => navigate("/reservas")}
          className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 transition"
        >
          Ir a Mis Reservas
        </button>
      </div>
    </div>
  );
}
