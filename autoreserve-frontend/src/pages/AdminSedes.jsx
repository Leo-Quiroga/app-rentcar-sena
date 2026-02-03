// Página de administración de sedes
import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { getAdminBranches, deleteBranch } from "../api/adminBranchesApi";

// Renderizar lista de sedes con opciones para editar o eliminar
export default function AdminSedes() {
  const [sedes, setSedes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Cargar sedes al montar el componente
  useEffect(() => {
    loadSedes();
  }, []);

  const loadSedes = async () => {
    try {
      setLoading(true);
      const data = await getAdminBranches();
      setSedes(data);
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error('Error cargando sedes:', err);
    } finally {
      setLoading(false);
    }
  };

  // Manejar eliminación de sede
  const handleDelete = async (id, sedeName) => {
    if (window.confirm(`¿Seguro que quieres eliminar la sede "${sedeName}"?`)) {
      try {
        await deleteBranch(id);
        // Recargar la lista después de eliminar
        await loadSedes();
        alert('Sede eliminada exitosamente');
      } catch (error) {
        alert('Error al eliminar: ' + error.message);
      }
    }
  };

  if (loading) {
    return (
      <div className="max-w-6xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
        <div className="text-center py-12">
          <p className="text-gray-600">Cargando sedes...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-6xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
        <div className="text-center py-12">
          <p className="text-red-600">Error: {error}</p>
          <button 
            onClick={loadSedes}
            className="mt-4 px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
          >
            Reintentar
          </button>
        </div>
      </div>
    );
  }

  // Renderizar tabla de sedes
  return (
    <div className="max-w-6xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">🏢 Gestión de Sedes</h1>
        <Link
          to="/admin/sedes/nuevo"
          className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition"
        >
          + Nueva Sede
        </Link>
      </div>
      
      {/* Si no hay sedes, mostrar mensaje */}
      {sedes.length === 0 ? (
        <p className="text-gray-600">No hay sedes registradas.</p>
      ) : (
        <div className="overflow-x-auto bg-white shadow rounded-lg">
          <table className="min-w-full divide-y divide-gray-200 text-sm">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left font-semibold text-gray-700">ID</th>
                <th className="px-4 py-3 text-left font-semibold text-gray-700">Nombre</th>
                <th className="px-4 py-3 text-left font-semibold text-gray-700">Dirección</th>
                <th className="px-4 py-3 text-left font-semibold text-gray-700">Ciudad</th>
                <th className="px-4 py-3 text-left font-semibold text-gray-700">Teléfono</th>
                <th className="px-4 py-3 text-left font-semibold text-gray-700">Autos</th>
                <th className="px-4 py-3 text-center font-semibold text-gray-700">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {sedes.map((sede) => (
                <tr key={sede.id} className="hover:bg-gray-50">
                  <td className="px-4 py-2 text-gray-500">#{sede.id}</td>
                  <td className="px-4 py-2 font-medium">{sede.name}</td>
                  <td className="px-4 py-2">{sede.address || 'Sin dirección'}</td>
                  <td className="px-4 py-2">{sede.city || 'Sin ciudad'}</td>
                  <td className="px-4 py-2">{sede.phone || 'Sin teléfono'}</td>
                  <td className="px-4 py-2">
                    <span className="inline-block px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded">
                      {sede.carCount || 0} autos
                    </span>
                  </td>
                  <td className="px-4 py-2 text-center space-x-2">
                    <Link
                      to={`/admin/sedes/${sede.id}/editar`}
                      className="px-3 py-1 text-sm bg-secondary text-gray-900 rounded hover:bg-secondary-dark transition"
                    >
                      Editar
                    </Link>
                    <button
                      onClick={() => handleDelete(sede.id, sede.name)}
                      className="px-3 py-1 text-sm bg-red-500 text-white rounded hover:bg-red-600 transition"
                    >
                      Eliminar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
