import { useState } from "react";
import { Link } from "react-router-dom";
import { mockSedes } from "../data/mockSedes";

export default function AdminSedes() {
  const [sedes] = useState(mockSedes);

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

      {sedes.length === 0 ? (
        <p className="text-gray-600">No hay sedes registradas.</p>
      ) : (
        <div className="overflow-x-auto bg-white shadow rounded-lg">
          <table className="min-w-full divide-y divide-gray-200 text-sm">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left font-semibold text-gray-700">Nombre</th>
                <th className="px-4 py-3 text-left font-semibold text-gray-700">Dirección</th>
                <th className="px-4 py-3 text-left font-semibold text-gray-700">Ciudad</th>
                <th className="px-4 py-3 text-left font-semibold text-gray-700">Teléfono</th>
                <th className="px-4 py-3 text-center font-semibold text-gray-700">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {sedes.map((sede) => (
                <tr key={sede.id} className="hover:bg-gray-50">
                  <td className="px-4 py-2">{sede.nombre}</td>
                  <td className="px-4 py-2">{sede.direccion}</td>
                  <td className="px-4 py-2">{sede.ciudad}</td>
                  <td className="px-4 py-2">{sede.telefono}</td>
                  <td className="px-4 py-2 text-center space-x-2">
                    <Link
                      to={`/admin/sedes/${sede.id}/editar`}
                      className="px-3 py-1 text-sm bg-secondary text-gray-900 rounded hover:bg-secondary-dark transition"
                    >
                      Editar
                    </Link>
                    <button
                      onClick={() => alert("Eliminar sede aún no implementado")}
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
