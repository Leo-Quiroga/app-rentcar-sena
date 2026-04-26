// Página de checkout para procesar pagos de reservas
import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { confirmPayment } from "../api/reservationsApi";

// Tarjetas de prueba simuladas
const TEST_CARDS = {
  "4111111111111111": { result: "success", label: "Visa - Pago exitoso" },
  "4000000000000002": { result: "declined", label: "Visa - Tarjeta declinada" },
  "4000000000009995": { result: "insufficient", label: "Visa - Fondos insuficientes" },
};

function getCardType(number) {
  const clean = number.replace(/\s/g, "");
  if (clean.startsWith("4")) return "VISA";
  if (clean.startsWith("5")) return "MASTERCARD";
  if (clean.startsWith("3")) return "AMEX";
  return "";
}

function formatCardNumber(value) {
  return value.replace(/\D/g, "").slice(0, 16).replace(/(.{4})/g, "$1 ").trim();
}

function formatExpiry(value) {
  const clean = value.replace(/\D/g, "").slice(0, 4);
  if (clean.length >= 3) return clean.slice(0, 2) + "/" + clean.slice(2);
  return clean;
}

export default function Checkout() {
  const navigate = useNavigate();
  const location = useLocation();
  const reservationData = location.state?.reservationData;

  const [paymentData, setPaymentData] = useState({ name: "", cardNumber: "", expiry: "", cvv: "" });
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === "cardNumber") {
      setPaymentData({ ...paymentData, cardNumber: formatCardNumber(value) });
    } else if (name === "expiry") {
      setPaymentData({ ...paymentData, expiry: formatExpiry(value) });
    } else if (name === "cvv") {
      setPaymentData({ ...paymentData, cvv: value.replace(/\D/g, "").slice(0, 4) });
    } else {
      setPaymentData({ ...paymentData, [name]: value });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (!reservationData?.reservationId) {
      setError("No hay datos de reserva válidos.");
      return;
    }

    const cleanCard = paymentData.cardNumber.replace(/\s/g, "");

    // Validaciones básicas
    if (cleanCard.length < 16) { setError("El número de tarjeta debe tener 16 dígitos."); return; }
    if (paymentData.cvv.length < 3) { setError("El CVV debe tener al menos 3 dígitos."); return; }
    if (!paymentData.expiry.includes("/")) { setError("Formato de expiración inválido. Usa MM/AA."); return; }

    // Validar fecha de expiración
    const [mm, yy] = paymentData.expiry.split("/");
    const expDate = new Date(2000 + parseInt(yy), parseInt(mm) - 1);
    if (expDate < new Date()) { setError("La tarjeta está vencida."); return; }

    setProcessing(true);

    // Simular delay de procesamiento (2 segundos)
    await new Promise((resolve) => setTimeout(resolve, 2000));

    const cardResult = TEST_CARDS[cleanCard];

    if (!cardResult) {
      // Tarjeta no reconocida → fallo genérico
      setProcessing(false);
      navigate("/reservas/pago-fallido", {
        state: {
          reservationId: reservationData.reservationId,
          reason: "Tarjeta no reconocida o inválida.",
          reservationData,
        },
      });
      return;
    }

    if (cardResult.result !== "success") {
      setProcessing(false);
      const reasons = {
        declined: "La tarjeta fue declinada por el banco.",
        insufficient: "Fondos insuficientes en la tarjeta.",
      };
      navigate("/reservas/pago-fallido", {
        state: {
          reservationId: reservationData.reservationId,
          reason: reasons[cardResult.result] || "Error en el pago.",
          reservationData,
        },
      });
      return;
    }

    // Pago exitoso → confirmar en el backend
    try {
      const confirmed = await confirmPayment(reservationData.reservationId);
      setProcessing(false);
      navigate("/reservas/confirmacion", {
        state: { reservation: confirmed, reservationData },
      });
    } catch (err) {
      setProcessing(false);
      setError("Error al confirmar el pago: " + err.message);
    }
  };

  if (!reservationData) {
    return (
      <div className="max-w-3xl mx-auto py-10 px-4 text-center">
        <h1 className="text-2xl font-bold mb-4">Checkout</h1>
        <p className="text-gray-600">No hay datos de reserva.</p>
        <button onClick={() => navigate("/")} className="mt-6 px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark">
          Volver al inicio
        </button>
      </div>
    );
  }

  const cardType = getCardType(paymentData.cardNumber);

  return (
    <div className="max-w-3xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <h1 className="text-2xl font-bold mb-6">💳 Checkout</h1>

      {/* Resumen de la reserva */}
      <div className="bg-white shadow rounded-lg p-6 mb-6">
        <h2 className="text-lg font-semibold mb-4">Resumen de la Reserva</h2>
        <div className="space-y-1 text-sm text-gray-700">
          <p><strong>Auto:</strong> {reservationData.carBrand} {reservationData.carModel}</p>
          <p><strong>Fechas:</strong> {reservationData.startDate} → {reservationData.endDate}</p>
          <p><strong>Sede de retiro:</strong> {reservationData.pickupBranchName}</p>
          <p><strong>Sede de entrega:</strong> {reservationData.dropoffBranchName}</p>
          <p className="text-base font-bold text-primary mt-2">Total: ${reservationData.estimatedTotal?.toLocaleString()}</p>
        </div>
      </div>

      {/* Tarjetas de prueba disponibles */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6 text-xs text-blue-800">
        <p className="font-semibold mb-2">🧪 Tarjetas de prueba disponibles:</p>
        <ul className="space-y-1">
          <li><span className="font-mono">4111 1111 1111 1111</span> — ✅ Pago exitoso</li>
          <li><span className="font-mono">4000 0000 0000 0002</span> — ❌ Tarjeta declinada</li>
          <li><span className="font-mono">4000 0000 0000 9995</span> — ❌ Fondos insuficientes</li>
        </ul>
        <p className="mt-2">Usa cualquier CVV de 3 dígitos y fecha futura (ej: 12/26).</p>
      </div>

      {/* Formulario de pago */}
      <form onSubmit={handleSubmit} className="bg-white shadow rounded-lg p-6 space-y-4">
        <h2 className="text-lg font-semibold">Datos de Pago</h2>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded p-3">
            {error}
          </div>
        )}

        <div>
          <label className="block text-sm font-medium text-gray-700">Nombre en la tarjeta</label>
          <input
            type="text"
            name="name"
            value={paymentData.name}
            onChange={handleChange}
            required
            placeholder="Como aparece en la tarjeta"
            className="mt-1 w-full border border-gray-300 rounded p-2 focus:ring-primary focus:border-primary"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Número de tarjeta
            {cardType && <span className="ml-2 text-xs font-bold text-primary">{cardType}</span>}
          </label>
          <input
            type="text"
            name="cardNumber"
            value={paymentData.cardNumber}
            onChange={handleChange}
            required
            placeholder="0000 0000 0000 0000"
            className="mt-1 w-full border border-gray-300 rounded p-2 font-mono focus:ring-primary focus:border-primary"
          />
        </div>

        <div className="flex gap-4">
          <div className="flex-1">
            <label className="block text-sm font-medium text-gray-700">Fecha de expiración</label>
            <input
              type="text"
              name="expiry"
              value={paymentData.expiry}
              onChange={handleChange}
              required
              placeholder="MM/AA"
              className="mt-1 w-full border border-gray-300 rounded p-2 focus:ring-primary focus:border-primary"
            />
          </div>
          <div className="flex-1">
            <label className="block text-sm font-medium text-gray-700">CVV</label>
            <input
              type="password"
              name="cvv"
              value={paymentData.cvv}
              onChange={handleChange}
              required
              placeholder="•••"
              className="mt-1 w-full border border-gray-300 rounded p-2 focus:ring-primary focus:border-primary"
            />
          </div>
        </div>

        <div className="flex justify-end gap-4 mt-6">
          <button
            type="button"
            onClick={() => navigate("/reservas")}
            disabled={processing}
            className="px-4 py-2 border rounded hover:bg-gray-100 disabled:opacity-50"
          >
            Cancelar
          </button>
          <button
            type="submit"
            disabled={processing}
            className="px-6 py-2 bg-primary text-white rounded hover:bg-primary-dark disabled:opacity-50 flex items-center gap-2"
          >
            {processing ? (
              <>
                <svg className="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z" />
                </svg>
                Procesando...
              </>
            ) : (
              "Pagar ahora"
            )}
          </button>
        </div>
      </form>
    </div>
  );
}
