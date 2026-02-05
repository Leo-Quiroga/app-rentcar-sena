// Página para gestionar TODAS las reservas de la aplicación (Admin)
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getAdminReservations, updateReservationStatus } from "../api/adminReservationsApi";

export default function AllReservations() {
  const navigate = useNavigate();
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState("");

  // Cargar todas las reservas
  useEffect(() => {
    loadReservations();
  }, []);

  const loadReservations = async () => {
    try {
      setLoading(true);
      const data = await getAdminReservations();
      setReservations(data);
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error('Error cargando reservas:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (reservationId, newStatus) => {
    try {
      await updateReservationStatus(reservationId, newStatus);
      setReservations(prev => 
        prev.map(res => 
          res.id === reservationId 
            ? { ...res, status: newStatus.toUpperCase() }
            : res
        )
      );
      alert('Estado actualizado exitosamente');
    } catch (error) {
      alert('Error actualizando estado: ' + error.message);
    }
  };

  const handlePaymentStatusChange = async (reservationId, newPaymentStatus) => {
    try {
      // Aquí necesitarías crear un endpoint para cambiar estado de pago
      // Por ahora solo actualizamos localmente
      setReservations(prev => 
        prev.map(res => 
          res.id === reservationId 
            ? { ...res, paymentStatus: newPaymentStatus.toUpperCase() }
            : res
        )
      );
      alert('Estado de pago actualizado exitosamente');
    } catch (error) {
      alert('Error actualizando estado de pago: ' + error.message);
    }
  };

  // Filtrar reservas
  const filteredReservations = reservations.filter(reservation => {
    if (!filter) return true;
    const searchTerm = filter.toLowerCase();
    return (
      reservation.id.toString().includes(searchTerm) ||
      reservation.carBrand.toLowerCase().includes(searchTerm) ||
      reservation.carModel.toLowerCase().includes(searchTerm) ||
      reservation.status.toLowerCase().includes(searchTerm) ||
      reservation.paymentStatus.toLowerCase().includes(searchTerm)
    );
  });

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
        <div className="text-center py-12">
          <p className="text-gray-600">Cargando reservas...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
        <div className="text-center py-12">
          <p className="text-red-600">Error: {error}</p>
          <button 
            onClick={loadReservations}
            className="mt-4 px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
          >
            Reintentar
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <div className="mb-8">
        <button 
          onClick={() => navigate('/admin')}
          className="text-primary hover:underline mb-2 text-sm"
        >
          ← Volver al Dashboard
        </button>
        <h1 className="text-2xl font-bold text-neutral-dark">📋 Todas las Reservas</h1>
        <p className="text-gray-600 text-sm mt-1">Gestiona todas las reservas de la aplicación</p>
      </div>

      {/* Filtro de búsqueda */}
      <div className="mb-6">
        <input
          type="text"
          placeholder="Buscar por ID, auto, estado..."
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
          className="w-full max-w-md px-4 py-2 border border-gray-300 rounded-lg focus:ring-primary focus:border-primary"
        />
      </div>

      {/* Tabla de reservas */}
      <div className="bg-white shadow rounded-lg overflow-x-auto">
        <table className="w-full table-auto text-sm">
          <thead className="bg-gray-100 text-left">
            <tr>
              <th className="px-4 py-3">ID</th>
              <th className="px-4 py-3">Auto</th>
              <th className="px-4 py-3">Cliente</th>
              <th className="px-4 py-3">Fechas</th>
              <th className="px-4 py-3">Días</th>
              <th className="px-4 py-3">Total</th>
              <th className="px-4 py-3">Estado Reserva</th>
              <th className="px-4 py-3">Estado Pago</th>
              <th className="px-4 py-3 text-center">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredReservations.map((reservation) => (
              <tr key={reservation.id} className="border-t hover:bg-gray-50">
                <td className="px-4 py-3 font-medium">#{reservation.id}</td>
                <td className="px-4 py-3">
                  <div>
                    <p className="font-medium">{reservation.carBrand} {reservation.carModel}</p>
                    <p className="text-gray-500 text-xs">{reservation.categoryName}</p>
                  </div>
                </td>
                <td className="px-4 py-3">
                  <div>
                    <p className="font-medium">Cliente #{reservation.userId || 'N/A'}</p>
                  </div>
                </td>
                <td className="px-4 py-3">
                  <div className="text-xs">
                    <p>{reservation.startDate}</p>
                    <p>{reservation.endDate}</p>
                  </div>
                </td>
                <td className="px-4 py-3">{reservation.totalDays}</td>
                <td className="px-4 py-3 font-medium">${reservation.totalAmount}</td>
                <td className="px-4 py-3">
                  <select
                    value={reservation.status}
                    onChange={(e) => handleStatusChange(reservation.id, e.target.value)}
                    className={`px-2 py-1 rounded text-xs font-medium border-0 ${
                      reservation.status === 'CONFIRMED' ? 'bg-green-100 text-green-800' :
                      reservation.status === 'CANCELLED' ? 'bg-red-100 text-red-800' :
                      'bg-yellow-100 text-yellow-800'
                    }`}
                  >
                    <option value="CONFIRMED">Confirmada</option>
                    <option value="CANCELLED">Cancelada</option>
                    <option value="COMPLETED">Completada</option>
                  </select>
                </td>
                <td className="px-4 py-3">
                  <select
                    value={reservation.paymentStatus}
                    onChange={(e) => handlePaymentStatusChange(reservation.id, e.target.value)}
                    className={`px-2 py-1 rounded text-xs font-medium border-0 ${
                      reservation.paymentStatus === 'PAID' ? 'bg-green-100 text-green-800' :
                      reservation.paymentStatus === 'PENDING' ? 'bg-orange-100 text-orange-800' :
                      'bg-gray-100 text-gray-800'
                    }`}
                  >
                    <option value="PENDING">Pendiente</option>
                    <option value="PAID">Pagado</option>
                    <option value="REFUNDED">Reembolsado</option>
                  </select>
                </td>
                <td className="px-4 py-3 text-center">
                  <div className="flex gap-2 justify-center">
                    <button
                      onClick={() => window.open(`/reservas/${reservation.id}`, '_blank')}
                      className="text-blue-600 hover:underline text-xs"
                    >
                      👁️ Ver
                    </button>
                  </div>
                </td>
              </tr>
            ))}
            {filteredReservations.length === 0 && (
              <tr>
                <td colSpan="9" className="text-center py-6 text-gray-500">
                  {filter ? 'No se encontraron reservas con ese filtro.' : 'No hay reservas registradas.'}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}