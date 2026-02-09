// Página de sedes
import { useState, useEffect } from "react";
import { branchesApi } from "../api/branchesApi";
import { getImageUrl } from "../utils/imageUtils";

export default function Sedes() {
  const [sedes, setSedes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchSedes = async () => {
      try {
        const data = await branchesApi.getAll();
        setSedes(data);
      } catch (err) {
        setError('Error al cargar las sedes');
        console.error('Error:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchSedes();
  }, []);

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
        <div className="text-center">Cargando sedes...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
        <div className="text-center text-red-600">{error}</div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <h1 className="text-2xl font-bold mb-8">🏢 Nuestras Sedes</h1>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
        {sedes.map((sede) => (
          <div
            key={sede.id}
            className="bg-white shadow rounded-lg overflow-hidden flex flex-col"
          >
            <img
              src={getImageUrl(sede.image, 'branch')}
              alt={sede.name}
              className="h-48 w-full object-cover"
              onError={(e) => {
                e.target.src = getImageUrl(null, 'branch');
              }}
            />
            <div className="p-6 flex flex-col flex-grow">
              <h2 className="text-lg font-semibold mb-2">{sede.name}</h2>
              <p className="text-sm text-gray-600 mb-1">
                <strong>Dirección:</strong> {sede.address}
              </p>
              <p className="text-sm text-gray-600 mb-4">
                <strong>Teléfono:</strong> {sede.phone}
              </p>

              <button
                className="mt-auto px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
                onClick={() => alert(`📍 Ver ubicación de ${sede.name}`)}
              >
                Ver ubicación
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
