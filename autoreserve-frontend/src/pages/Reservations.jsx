// Página de reservas
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getMyReservations } from "../api/reservationsApi";

export default function Reservations() {
  const navigate = useNavigate();
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Cargar reservas
  useEffect(() => {
    const loadReservations = async () => {
      try {
        setLoading(true);
        const data = await getMyReservations();
        setReservations(data);
        setError(null);
      } catch (err) {
        setError(err.message);
        console.error('Error cargando reservas:', err);
      } finally {
        setLoading(false);
      }
    };

    loadReservations();
  }, []);

  if (loading) {
    return (
      <div className="max-w-5xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
        <div className="text-center py-12">
          <p className="text-gray-600">Cargando reservas...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-5xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
        <div className="text-center py-12">
          <p className="text-red-600">Error: {error}</p>
        </div>
      </div>
    );
  }

  // Renderizar lista de reservas
  return (
    <div className="max-w-5xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <div className="mb-6">
        <button 
          onClick={() => navigate('/perfil')}
          className="text-primary hover:underline mb-4"
        >
          ← Volver al Perfil
        </button>
        <h1 className="text-2xl font-bold">📅 Mis Reservas</h1>
      </div>

      {reservations.length === 0 ? (
        <p className="text-gray-600">No tienes reservas todavía.</p>
      ) : (
        <div className="space-y-4">
          {reservations.map((reserva) => (
            <div
              key={reserva.id}
              className="bg-white shadow rounded-lg p-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4"
            >
              <div>
                <h2 className="text-lg font-semibold text-neutral-dark">
                  {reserva.carBrand} {reserva.carModel}
                </h2>
                <p className="text-sm text-gray-600">
                  Sede: {reserva.branchName}
                </p>
                <p className="text-sm text-gray-600">
                  {reserva.startDate} - {reserva.endDate}
                </p>
                <p className="text-sm font-medium text-primary">
                  Total: ${reserva.totalAmount}
                </p>
                <p className={`text-sm font-medium ${
                  reserva.status === 'CONFIRMED' ? 'text-green-600' : 
                  reserva.status === 'CANCELLED' ? 'text-red-600' : 'text-yellow-600'
                }`}>
                  Estado: {reserva.status === 'CONFIRMED' ? 'Confirmada' : 
                          reserva.status === 'CANCELLED' ? 'Cancelada' : reserva.status}
                </p>
              </div>

              <button
                onClick={() => navigate(`/reservas/${reserva.id}`)}
                className="text-sm px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition"
              >
                Ver detalle
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
