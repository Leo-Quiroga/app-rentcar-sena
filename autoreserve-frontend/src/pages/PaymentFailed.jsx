// Pantalla que muestra el estado de pago fallido
import { Link } from "react-router-dom";

export default function PaymentFailed() {
  return (
    <div className="max-w-2xl mx-auto py-16 px-4 sm:px-6 lg:px-8 text-center">
      {/* Icono / estado */}
      <div className="mb-6">
        <span className="text-6xl">❌</span>
      </div>

      {/* Mensaje principal */}
      <h1 className="text-2xl font-bold text-red-600 mb-4">
        El pago no se pudo procesar
      </h1>
      <p className="text-gray-700 mb-8">
        Hubo un problema al procesar tu pago. No te preocupes, tu reserva aún no ha sido confirmada.
        Puedes intentarlo de nuevo o revisar tus reservas para más información.
      </p>

      {/* Acciones */}
      <div className="flex justify-center gap-4">
        <Link
          to="/reservas/checkout"
          className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition"
        >
          Reintentar Pago
        </Link>
        <Link
          to="/reservas"
          className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 transition"
        >
          Ir a Mis Reservas
        </Link>
      </div>
    </div>
  );
}
