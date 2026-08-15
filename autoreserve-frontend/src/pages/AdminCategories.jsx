// Pantalla de administración de categorías para el administrador
import { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { getAdminCategories, deleteCategory } from "../api/adminCategoriesApi";

export default function AdminCategories() {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Cargar categorías al montar el componente
  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      setLoading(true);
      const data = await getAdminCategories();
      setCategories(data);
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error('Error cargando categorías:', err);
    } finally {
      setLoading(false);
    }
  };

  // Manejar eliminación de categoría
  const handleDelete = async (id, categoryName) => {
    if (window.confirm(`¿Seguro que quieres eliminar la categoría "${categoryName}"?`)) {
      try {
        await deleteCategory(id);
        // Recargar la lista después de eliminar
        await loadCategories();
        alert('Categoría eliminada exitosamente');
      } catch (error) {
        alert('Error al eliminar: ' + error.message);
      }
    }
  };

  if (loading) {
    return (
      <div className="max-w-6xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
        <div className="text-center py-12">
          <p className="text-gray-600">Cargando categorías...</p>
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
            onClick={loadCategories}
            className="mt-4 px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
          >
            Reintentar
          </button>
        </div>
      </div>
    );
  }

  // Renderizar tabla de categorías con acciones
  return (
    <div className="max-w-6xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <div className="flex items-center justify-between mb-8">
        <div>
          <button 
            onClick={() => navigate('/admin')}
            className="text-primary hover:underline mb-2 text-sm"
          >
            ← Volver al Dashboard
          </button>
          <h1 className="text-2xl font-bold text-neutral-dark">📂 Administración de Categorías</h1>
        </div>
        <Link
          to="/admin/categorias/nueva"
          className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
        >
          + Nueva Categoría
        </Link>
      </div>

      <div className="overflow-hidden">

        {/* Tarjetas móvil (< 640px) */}
        <div className="sm:hidden divide-y divide-gray-200 border border-gray-200 rounded-lg">
          {categories.map((cat) => (
            <div key={cat.id} className="p-4 space-y-2">
              <div className="flex items-start justify-between gap-2">
                <div>
                  <p className="font-semibold text-gray-900">{cat.name}</p>
                  <p className="text-xs text-gray-400">ID: {cat.id}</p>
                </div>
                <span className="inline-block px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded shrink-0">
                  {cat.carCount || 0} autos
                </span>
              </div>
              <p className="text-xs text-gray-600">{cat.description || 'Sin descripción'}</p>
              <div className="flex gap-2">
                <Link to={`/admin/categorias/${cat.id}/editar`}
                  className="text-xs px-3 py-1 bg-yellow-500 text-white rounded hover:bg-yellow-600">
                  Editar
                </Link>
                <button onClick={() => handleDelete(cat.id, cat.name)}
                  className="text-xs px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600">
                  Eliminar
                </button>
              </div>
            </div>
          ))}
          {categories.length === 0 && (
            <p className="text-center py-6 text-gray-500 italic">No hay categorías registradas</p>
          )}
        </div>

        {/* Tabla desktop (≥ 640px) */}
        <table className="hidden sm:table w-full border-collapse border border-gray-200 text-sm">
          <thead className="bg-gray-100">
            <tr>
              <th className="border border-gray-200 px-4 py-2 text-left">ID</th>
              <th className="border border-gray-200 px-4 py-2 text-left">Nombre</th>
              <th className="border border-gray-200 px-4 py-2 text-left">Descripción</th>
              <th className="border border-gray-200 px-4 py-2">Autos</th>
              <th className="border border-gray-200 px-4 py-2">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {categories.map((cat) => (
              <tr key={cat.id} className="hover:bg-gray-50">
                <td className="border border-gray-200 px-4 py-2">{cat.id}</td>
                <td className="border border-gray-200 px-4 py-2 font-medium">{cat.name}</td>
                <td className="border border-gray-200 px-4 py-2">{cat.description || 'Sin descripción'}</td>
                <td className="border border-gray-200 px-4 py-2 text-center">
                  <span className="inline-block px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded">
                    {cat.carCount || 0} autos
                  </span>
                </td>
                <td className="border border-gray-200 px-4 py-2 text-center">
                  <div className="flex justify-center gap-2">
                    <Link to={`/admin/categorias/${cat.id}/editar`}
                      className="px-3 py-1 text-sm bg-yellow-500 text-white rounded hover:bg-yellow-600">
                      Editar
                    </Link>
                    <button onClick={() => handleDelete(cat.id, cat.name)}
                      className="px-3 py-1 text-sm bg-red-500 text-white rounded hover:bg-red-600">
                      Eliminar
                    </button>
                  </div>
                </td>
              </tr>
            ))}
            {categories.length === 0 && (
              <tr>
                <td colSpan="5" className="text-center py-6 text-gray-500 italic">No hay categorías registradas</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
