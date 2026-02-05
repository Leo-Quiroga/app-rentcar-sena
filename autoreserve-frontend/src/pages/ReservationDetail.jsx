// Página de detalle de reserva
import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getReservationById, cancelReservation } from "../api/reservationsApi";

export default function ReservationDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [reservation, setReservation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadReservation = async () => {
      try {
        setLoading(true);
        const data = await getReservationById(id);
        setReservation(data);
        setError(null);
      } catch (err) {
        setError(err.message);
        console.error('Error cargando reserva:', err);
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      loadReservation();
    }
  }, [id]);

  const handleCancel = async () => {
    if (!window.confirm('¿Estás seguro de cancelar esta reserva?')) return;
    
    try {
      await cancelReservation(id);
      setReservation(prev => ({ ...prev, status: 'CANCELLED' }));
      alert('Reserva cancelada exitosamente');
    } catch (error) {
      alert('Error cancelando reserva: ' + error.message);
    }
  };

  if (loading) {
    return (
      <div className="max-w-4xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
        <div className="text-center py-12">
          <p className="text-gray-600">Cargando detalle de reserva...</p>
        </div>
      </div>
    );
  }

  if (error || !reservation) {
    return (
      <div className="max-w-3xl mx-auto py-10 px-4 sm:px-6 lg:px-8 text-center">
        <h1 className="text-2xl font-bold mb-6">Reserva no encontrada</h1>
        <p className="text-red-600 mb-4">{error}</p>
        <button
          onClick={() => navigate("/reservas")}
          className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
        >
          Volver a reservas
        </button>
      </div>
    );
  }
  // Renderizar detalles de la reserva
  return (
    <div className="max-w-4xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <div className="mb-6">
        <button 
          onClick={() => navigate('/reservas')}
          className="text-primary hover:underline mb-4"
        >
          ← Volver a Mis Reservas
        </button>
        <h1 className="text-2xl font-bold">Detalle de Reserva #{reservation.id}</h1>
      </div>

      <div className="bg-white shadow rounded-lg overflow-hidden">
        {/* Header con estado */}
        <div className="px-6 py-4 border-b">
          <div className="flex justify-between items-center">
            <div>
              <h2 className="text-xl font-semibold">{reservation.carBrand} {reservation.carModel}</h2>
              <p className="text-gray-600">{reservation.categoryName} • {reservation.carYear}</p>
            </div>
            <div className="text-right">
              <span className={`inline-block px-3 py-1 rounded-full text-sm font-medium ${
                reservation.status === 'CONFIRMED' ? 'bg-green-100 text-green-800' :
                reservation.status === 'CANCELLED' ? 'bg-red-100 text-red-800' :
                'bg-yellow-100 text-yellow-800'
              }`}>
                {reservation.status === 'CONFIRMED' ? 'Confirmada' :
                 reservation.status === 'CANCELLED' ? 'Cancelada' : reservation.status}
              </span>
              <br />
              <span className={`inline-block px-3 py-1 rounded-full text-sm font-medium mt-2 ${
                reservation.paymentStatus === 'PAID' ? 'bg-green-100 text-green-800' :
                reservation.paymentStatus === 'PENDING' ? 'bg-orange-100 text-orange-800' :
                'bg-gray-100 text-gray-800'
              }`}>
                {reservation.paymentStatus === 'PAID' ? 'Pagado' :
                 reservation.paymentStatus === 'PENDING' ? 'Pendiente de Pago' : reservation.paymentStatus}
              </span>
            </div>
          </div>
        </div>

        {/* Imagen del auto */}
        {reservation.carImage && (
          <div className="px-6 py-4">
            <img 
              src={reservation.carImage} 
              alt={`${reservation.carBrand} ${reservation.carModel}`}
              className="w-full h-64 object-cover rounded-lg"
            />
          </div>
        )}

        {/* Detalles */}
        <div className="px-6 py-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="font-semibold text-lg mb-3">Información del Vehículo</h3>
              <div className="space-y-2 text-sm">
                <p><span className="font-medium">Marca:</span> {reservation.carBrand}</p>
                <p><span className="font-medium">Modelo:</span> {reservation.carModel}</p>
                <p><span className="font-medium">Año:</span> {reservation.carYear}</p>
                <p><span className="font-medium">Categoría:</span> {reservation.categoryName}</p>
              </div>
            </div>

            <div>
              <h3 className="font-semibold text-lg mb-3">Detalles de la Reserva</h3>
              <div className="space-y-2 text-sm">
                <p><span className="font-medium">Fecha Inicio:</span> {reservation.startDate}</p>
                <p><span className="font-medium">Fecha Fin:</span> {reservation.endDate}</p>
                <p><span className="font-medium">Total Días:</span> {reservation.totalDays}</p>
                <p><span className="font-medium">Sede Retiro:</span> {reservation.pickupBranchName}</p>
                <p><span className="font-medium">Sede Entrega:</span> {reservation.dropoffBranchName}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Costos */}
        <div className="px-6 py-4 bg-gray-50">
          <h3 className="font-semibold text-lg mb-3">Resumen de Costos</h3>
          <div className="space-y-2">
            <div className="flex justify-between">
              <span>Precio por día:</span>
              <span>${reservation.pricePerDay}</span>
            </div>
            <div className="flex justify-between">
              <span>Número de días:</span>
              <span>{reservation.totalDays}</span>
            </div>
            <div className="flex justify-between font-bold text-lg border-t pt-2">
              <span>Total:</span>
              <span className="text-primary">${reservation.totalAmount}</span>
            </div>
          </div>
        </div>

        {/* Acciones */}
        <div className="px-6 py-4 border-t">
          <div className="flex gap-3">
            {reservation.status === 'CONFIRMED' && reservation.paymentStatus === 'PENDING' && (
              <button
                onClick={() => navigate('/reservas/checkout', {
                  state: {
                    reservationData: {
                      reservationId: reservation.id,
                      carBrand: reservation.carBrand,
                      carModel: reservation.carModel,
                      startDate: reservation.startDate,
                      endDate: reservation.endDate,
                      branchName: reservation.pickupBranchName,
                      estimatedTotal: reservation.totalAmount
                    }
                  }
                })}
                className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
              >
                Pagar Ahora
              </button>
            )}
            {reservation.status === 'CONFIRMED' && (
              <button
                onClick={handleCancel}
                className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition"
              >
                Cancelar Reserva
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
