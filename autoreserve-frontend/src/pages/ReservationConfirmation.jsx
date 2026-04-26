// Página de confirmación de reserva
import { Link, useLocation, useNavigate } from "react-router-dom";

export default function ReservationConfirmation() {
  const location = useLocation();
  const navigate = useNavigate();
  const reservation = location.state?.reservation;
  const reservationData = location.state?.reservationData;

  if (!reservation) {
    return (
      <div className="text-center py-12">
        <h2 className="text-2xl font-bold">No hay reserva confirmada</h2>
        <p className="mt-4 text-gray-600">Parece que aún no has confirmado ninguna reserva.</p>
        <Link to="/" className="mt-6 inline-block px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition">
          Volver al inicio
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      {/* Encabezado */}
      <div className="text-center mb-8">
        <div className="text-6xl mb-4">✅</div>
        <h1 className="text-2xl font-bold text-green-700">¡Pago exitoso! Reserva confirmada</h1>
        <p className="text-gray-600 mt-2">Tu reserva ha sido confirmada y el pago procesado correctamente.</p>
      </div>

      {/* Detalle de la reserva */}
      <div className="bg-white shadow rounded-lg overflow-hidden mb-6">
        <div className="bg-green-50 px-6 py-4 border-b border-green-100">
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-600">Número de reserva</span>
            <span className="font-bold text-lg text-primary">#{reservation.id}</span>
          </div>
        </div>

        <div className="px-6 py-4 space-y-3 text-sm text-gray-700">
          <div className="flex justify-between">
            <span className="font-medium">Vehículo</span>
            <span>{reservation.carBrand} {reservation.carModel} {reservation.carYear}</span>
          </div>
          <div className="flex justify-between">
            <span className="font-medium">Categoría</span>
            <span>{reservation.categoryName}</span>
          </div>
          <div className="flex justify-between">
            <span className="font-medium">Fecha de retiro</span>
            <span>{reservation.startDate}</span>
          </div>
          <div className="flex justify-between">
            <span className="font-medium">Fecha de entrega</span>
            <span>{reservation.endDate}</span>
          </div>
          <div className="flex justify-between">
            <span className="font-medium">Sede de retiro</span>
            <span>{reservation.pickupBranchName}</span>
          </div>
          <div className="flex justify-between">
            <span className="font-medium">Sede de entrega</span>
            <span>{reservation.dropoffBranchName}</span>
          </div>
          <div className="flex justify-between">
            <span className="font-medium">Días</span>
            <span>{reservation.totalDays}</span>
          </div>
          <div className="flex justify-between">
            <span className="font-medium">Precio por día</span>
            <span>${reservation.pricePerDay?.toLocaleString()}</span>
          </div>
        </div>

        <div className="px-6 py-4 bg-gray-50 border-t">
          <div className="flex justify-between items-center">
            <span className="font-bold text-base">Total pagado</span>
            <span className="font-bold text-xl text-primary">${reservation.totalAmount?.toLocaleString()}</span>
          </div>
          <div className="flex justify-between items-center mt-2">
            <span className="text-sm text-gray-600">Estado de pago</span>
            <span className="text-sm font-medium text-green-600">✓ Pagado</span>
          </div>
          <div className="flex justify-between items-center mt-1">
            <span className="text-sm text-gray-600">Estado de reserva</span>
            <span className="text-sm font-medium text-green-600">✓ Confirmada</span>
          </div>
        </div>
      </div>

      {/* Acciones */}
      <div className="flex flex-col sm:flex-row justify-center gap-4">
        <button
          onClick={() => navigate("/reservas")}
          className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition"
        >
          Ver mis reservas
        </button>
        <Link to="/" className="px-4 py-2 border border-gray-300 rounded hover:bg-gray-100 transition text-center">
          Volver al inicio
        </Link>
      </div>
    </div>
  );
}
