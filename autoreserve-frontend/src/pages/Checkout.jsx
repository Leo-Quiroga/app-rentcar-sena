// Página de checkout para procesar pagos de reservas
import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { createReservation } from "../api/reservationsApi";

export default function Checkout() {
  const navigate = useNavigate();
  const location = useLocation();

  // obtenemos los datos de reserva desde la navegación
  const reservationData = location.state?.reservationData;
  const [loading, setLoading] = useState(false);
  // Estado del formulario de pago
  const [paymentData, setPaymentData] = useState({
    name: "",
    cardNumber: "",
    expiry: "",
    cvv: "",
  });
  // Manejar cambios en los campos del formulario
  const handleChange = (e) => {
    setPaymentData({ ...paymentData, [e.target.name]: e.target.value });
  };
  // Manejar envío del formulario
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!reservationData) {
      alert("No hay datos de reserva.");
      return;
    }

    try {
      setLoading(true);
      
      // Crear la reserva en el backend
      const response = await createReservation({
        carId: reservationData.carId,
        startDate: reservationData.startDate,
        endDate: reservationData.endDate
      });

      // Simular procesamiento de pago
      if (paymentData.cardNumber.startsWith("4")) {
        navigate("/reservas/confirmacion", { 
          state: { 
            reservation: response,
            paymentSuccess: true 
          } 
        });
      } else {
        navigate("/reservas/pago-fallido", { 
          state: { 
            reservation: response,
            paymentFailed: true 
          } 
        });
      }
    } catch (error) {
      alert('Error creando reserva: ' + error.message);
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  // Si no hay datos de reserva, mostramos un mensaje
  if (!reservationData) {
    return (
      <div className="max-w-3xl mx-auto py-10 px-4 sm:px-6 lg:px-8 text-center">
        <h1 className="text-2xl font-bold mb-4">Checkout</h1>
        <p className="text-gray-600">No hay datos de reserva.</p>
        <button
          onClick={() => navigate("/")}
          className="mt-6 px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
        >
          Volver al inicio
        </button>
      </div>
    );
  }
  // Renderizar formulario de checkout
  return (
    <div className="max-w-3xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <h1 className="text-2xl font-bold mb-6">💳 Checkout</h1>

      {/* Resumen de la reserva */}
      <div className="bg-white shadow rounded-lg p-6 mb-8">
        <h2 className="text-lg font-semibold mb-4">Resumen de la Reserva</h2>
        <p><strong>Auto:</strong> {reservationData.carBrand} {reservationData.carModel}</p>
        <p><strong>Fechas:</strong> {reservationData.startDate} - {reservationData.endDate}</p>
        <p><strong>Sede:</strong> {reservationData.branchName}</p>
        <p><strong>Total estimado:</strong> ${reservationData.estimatedTotal}</p>
      </div>

      {/* Formulario de pago */}
      <form
        onSubmit={handleSubmit}
        className="bg-white shadow rounded-lg p-6 space-y-4"
      >
        <h2 className="text-lg font-semibold">Datos de Pago</h2>

        <div>
          <label className="block text-sm font-medium">Nombre en la tarjeta</label>
          <input
            type="text"
            name="name"
            value={paymentData.name}
            onChange={handleChange}
            required
            className="mt-1 w-full border rounded p-2"
          />
        </div>

        <div>
          <label className="block text-sm font-medium">Número de tarjeta</label>
          <input
            type="text"
            name="cardNumber"
            value={paymentData.cardNumber}
            onChange={handleChange}
            required
            className="mt-1 w-full border rounded p-2"
            placeholder="Ej: 4111111111111111"
          />
        </div>

        <div className="flex gap-4">
          <div className="flex-1">
            <label className="block text-sm font-medium">Expiración</label>
            <input
              type="text"
              name="expiry"
              value={paymentData.expiry}
              onChange={handleChange}
              required
              className="mt-1 w-full border rounded p-2"
              placeholder="MM/AA"
            />
          </div>
          <div className="flex-1">
            <label className="block text-sm font-medium">CVV</label>
            <input
              type="text"
              name="cvv"
              value={paymentData.cvv}
              onChange={handleChange}
              required
              className="mt-1 w-full border rounded p-2"
              placeholder="123"
            />
          </div>
        </div>

        {/* Botones */}
        <div className="flex justify-end gap-4 mt-6">
          <button
            type="button"
            onClick={() => navigate("/reservas")}
            className="px-4 py-2 border rounded hover:bg-gray-100"
          >
            Cancelar
          </button>
          <button
            type="submit"
            disabled={loading}
            className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark disabled:opacity-50"
          >
            {loading ? "Procesando..." : "Pagar"}
          </button>
        </div>
      </form>
    </div>
  );
}
