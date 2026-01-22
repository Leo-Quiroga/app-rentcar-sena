import { useState } from "react";
import { Link } from "react-router-dom";
import { mockAdminAutos } from "../data/mockAdminAutos";

export default function AdminAutos() {
  const [autos] = useState(mockAdminAutos);

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
                  <td className="px-4 py-3">{auto.category}</td>
                  <td className="px-4 py-3">${auto.pricePerDay}</td>
                  <td
                    className={`px-4 py-3 font-medium ${
                      auto.status === "Disponible" ? "text-green-600" : "text-red-600"
                    }`}
                  >
                    {auto.status}
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
                      onClick={() => alert(`Eliminar auto ${auto.model}`)}
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
