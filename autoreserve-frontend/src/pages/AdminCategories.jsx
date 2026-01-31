// Pantalla de administración de categorías para el administrador
import { useState } from "react";
import { Link } from "react-router-dom";
import { mockCategories } from "../data/mockCategories";

export default function AdminCategories() {
  // Estado de la lista de categorías (mock temporal)
  const [categories, setCategories] = useState(mockCategories);
  // Manejar eliminación de categoría
  const handleDelete = (id) => {
    if (window.confirm("¿Seguro que quieres eliminar esta categoría?")) {
      setCategories(categories.filter((cat) => cat.id !== id));
    }
  };
  // Renderizar tabla de categorías con acciones
  return (
    <div className="max-w-6xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-2xl font-bold">📂 Administración de Categorías</h1>
        <Link
          to="/admin/categorias/nueva"
          className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
        >
          + Nueva Categoría
        </Link>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full border-collapse border border-gray-200 text-sm">
          <thead className="bg-gray-100">
            <tr>
              <th className="border border-gray-200 px-4 py-2 text-left">ID</th>
              <th className="border border-gray-200 px-4 py-2 text-left">Nombre</th>
              <th className="border border-gray-200 px-4 py-2 text-left">Descripción</th>
              <th className="border border-gray-200 px-4 py-2 text-left">Imagen</th>
              <th className="border border-gray-200 px-4 py-2">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {categories.map((cat) => (
              <tr key={cat.id} className="hover:bg-gray-50">
                <td className="border border-gray-200 px-4 py-2">{cat.id}</td>
                <td className="border border-gray-200 px-4 py-2">{cat.name}</td>
                <td className="border border-gray-200 px-4 py-2">
                  {cat.description}
                </td>
                <td className="border border-gray-200 px-4 py-2">
                  <img
                    src={cat.image}
                    alt={cat.name}
                    className="h-12 w-auto rounded"
                  />
                </td>
                <td className="border border-gray-200 px-4 py-2 text-center">
                  <div className="flex justify-center gap-2">
                    <Link
                      to={`/admin/categorias/${cat.id}/editar`}
                      className="px-3 py-1 text-sm bg-yellow-500 text-white rounded hover:bg-yellow-600"
                    >
                      Editar
                    </Link>
                    <button
                      onClick={() => handleDelete(cat.id)}
                      className="px-3 py-1 text-sm bg-red-500 text-white rounded hover:bg-red-600"
                    >
                      Eliminar
                    </button>
                  </div>
                </td>
              </tr>
            ))}

            {categories.length === 0 && (
              <tr>
                <td
                  colSpan="5"
                  className="text-center py-6 text-gray-500 italic"
                >
                  No hay categorías registradas
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
