// Pantalla de administración de autos para el administrador
import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { getAdminCars, deleteCar } from "../api/adminCarsApi";

export default function AdminAutos() {
  const [autos, setAutos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Cargar autos desde la API
  useEffect(() => {
    loadAutos();
  }, []);

  const loadAutos = async () => {
    try {
      setLoading(true);
      const data = await getAdminCars();
      setAutos(data);
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error('Error cargando autos:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteCar = async (carId, carModel) => {
    if (!window.confirm(`¿Estás seguro de eliminar el auto ${carModel}?`)) {
      return;
    }

    try {
      await deleteCar(carId);
      // Recargar la lista
      await loadAutos();
      alert('Auto eliminado exitosamente');
    } catch (err) {
      alert(`Error al eliminar: ${err.message}`);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-neutral-light px-4 py-10 sm:px-6 lg:px-8">
        <div className="max-w-6xl mx-auto">
          <div className="text-center py-12">
            <p className="text-gray-600">Cargando autos...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-neutral-light px-4 py-10 sm:px-6 lg:px-8">
        <div className="max-w-6xl mx-auto">
          <div className="text-center py-12">
            <p className="text-red-600">Error: {error}</p>
            <button 
              onClick={loadAutos}
              className="mt-4 px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
            >
              Reintentar
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Renderizar tabla de autos con acciones
  return (
    <div className="min-h-screen bg-neutral-light px-4 py-10 sm:px-6 lg:px-8">
      <div className="max-w-6xl mx-auto">
        {/* Encabezado */}
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-2xl font-bold text-neutral-dark">🚗 Gestión de Autos</h1>
          <Link
            to="/admin/autos/nuevo"
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
          >
            + Nuevo Auto
          </Link>
        </div>

        {/* Tabla de autos */}
        <div className="bg-white shadow rounded-lg overflow-x-auto">
          <table className="w-full table-auto text-sm">
            <thead className="bg-gray-100 text-left">
              <tr>
                <th className="px-4 py-3">Marca</th>
                <th className="px-4 py-3">Modelo</th>
                <th className="px-4 py-3">Año</th>
                <th className="px-4 py-3">Categoría</th>
                <th className="px-4 py-3">Precio/día</th>
                <th className="px-4 py-3">Estado</th>
                <th className="px-4 py-3 text-center">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {autos.map((auto) => (
                <tr key={auto.id} className="border-t hover:bg-gray-50">
                  <td className="px-4 py-3">{auto.brand}</td>
                  <td className="px-4 py-3">{auto.model}</td>
                  <td className="px-4 py-3">{auto.year}</td>
                  <td className="px-4 py-3">{auto.categoryName}</td>
                  <td className="px-4 py-3">${auto.pricePerDay}</td>
                  <td
                    className={`px-4 py-3 font-medium ${
                      auto.status === "AVAILABLE" ? "text-green-600" : "text-red-600"
                    }`}
                  >
                    {auto.status === "AVAILABLE" ? "Disponible" : auto.status}
                  </td>
                  <td className="px-4 py-3 flex gap-2 justify-center">
                    <Link
                      to={`/admin/autos/${auto.id}/calendario`}
                      className="text-blue-600 hover:underline"
                    >
                      📅 Calendario
                    </Link>
                    <Link
                      to={`/admin/autos/editar/${auto.id}`}
                      className="text-yellow-600 hover:underline"
                    >
                      ✏️ Editar
                    </Link>
                    <button
                      className="text-red-600 hover:underline"
                      onClick={() => handleDeleteCar(auto.id, auto.model)}
                    >
                      🗑️ Eliminar
                    </button>
                  </td>
                </tr>
              ))}
              {autos.length === 0 && (
                <tr>
                  <td colSpan="7" className="text-center py-6 text-gray-500">
                    No hay autos registrados.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
