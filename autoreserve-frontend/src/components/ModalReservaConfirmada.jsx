// Componente para mostrar el modal de reserva confirmada
// src/components/ModalReservaConfirmada.jsx
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export default function ModalReservaConfirmada({ reserva, onClose }) {
  const navigate = useNavigate();
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  
  if (!reserva?.reservation) return null;

  const reservation = reserva.reservation;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
      <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-lg text-center">
        <h2 className="text-2xl font-bold text-green-600 mb-4">
          ¡Reserva creada exitosamente! 🎉
        </h2>

        <div className="text-left space-y-2 mb-6">
          <p><strong>ID Reserva:</strong> #{reservation.id}</p>
          {isAdmin && reservation.userFirstName && (
            <p><strong>Cliente:</strong> {reservation.userFirstName} {reservation.userLastName}</p>
          )}
          <p><strong>Auto:</strong> {reservation.carBrand} {reservation.carModel} ({reservation.carYear})</p>
          <p><strong>Categoría:</strong> {reservation.categoryName}</p>
          <p><strong>Sede Retiro:</strong> {reservation.pickupBranchName}</p>
          <p><strong>Sede Entrega:</strong> {reservation.dropoffBranchName}</p>
          <p><strong>Fechas:</strong> {reservation.startDate} → {reservation.endDate}</p>
          <p><strong>Días:</strong> {reservation.totalDays}</p>
          <p><strong>Precio/día:</strong> ${reservation.pricePerDay}</p>
          <p className="text-lg font-bold text-primary"><strong>Total:</strong> ${reservation.totalAmount}</p>
          <p className="text-sm text-orange-600">
            <strong>Estado Pago:</strong> {
              reservation.paymentStatus === 'NO_PAYMENT' ? 'Pendiente de pago' :
              reservation.paymentStatus === 'PAID' ? 'Pagado' :
              reservation.paymentStatus === 'REFUND_PENDING' ? 'Reembolso en proceso' :
              reservation.paymentStatus === 'REFUNDED' ? 'Reembolsado' :
              reservation.paymentStatus
            }
          </p>
        </div>

        <div className="flex justify-center gap-3 mt-6">
          {isAdmin ? (
            // Botones para administrador
            <>
              <button
                onClick={() => {
                  onClose && onClose();
                  navigate("/admin/reservas");
                }}
                className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"
              >
                Ir a Gestor de Reservas
              </button>
              <button
                onClick={() => {
                  onClose && onClose();
                  navigate("/");
                }}
                className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
              >
                Crear Otra Reserva
              </button>
            </>
          ) : (
            // Botones para cliente
            <>
              <button
                onClick={() => {
                  onClose && onClose();
                  navigate("/reservas");
                }}
                className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"
              >
                Ir a Mis Reservas
              </button>
              <button
                onClick={() => {
                  onClose && onClose();
                  navigate(`/reservas/${reservation.id}`);
                }}
                className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
              >
                Ver Detalle
              </button>
              {reservation.paymentStatus === 'NO_PAYMENT' && (
                <button
                  onClick={() => {
                    onClose && onClose();
                    navigate("/reservas/checkout", { 
                      state: { 
                        reservationData: {
                          reservationId: reservation.id,
                          carBrand: reservation.carBrand,
                          carModel: reservation.carModel,
                          startDate: reservation.startDate,
                          endDate: reservation.endDate,
                          pickupBranchName: reservation.pickupBranchName,
                          dropoffBranchName: reservation.dropoffBranchName,
                          estimatedTotal: reservation.totalAmount
                        }
                      } 
                    });
                  }}
                  className="px-4 py-2 bg-orange-500 text-white rounded hover:bg-orange-600 font-semibold"
                >
                  💳 Pagar Ahora
                </button>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}
