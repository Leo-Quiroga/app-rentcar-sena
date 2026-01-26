import { useState } from "react";
import { Link } from "react-router-dom";
import { mockUsers } from "../data/mockUsers";

export default function AdminUsers() {
  const [users, setUsers] = useState(mockUsers);

  const handleDelete = (id) => {
    if (window.confirm("¿Seguro que deseas eliminar este usuario?")) {
      setUsers((prev) => prev.filter((u) => u.id !== id));
    }
  };

  return (
    <div className="max-w-5xl mx-auto py-10 px-4 sm:px-6 lg:px-8 space-y-6">
      <header className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">👥 Gestión de Usuarios</h1>
        <Link
          to="/admin/usuarios/nuevo"
          className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
        >
          ➕ Nuevo Usuario
        </Link>
      </header>

      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-200 shadow-sm rounded-lg">
          <thead className="bg-gray-100">
            <tr>
              <th className="px-4 py-2 text-left">ID</th>
              <th className="px-4 py-2 text-left">Nombre</th>
              <th className="px-4 py-2 text-left">Email</th>
              <th className="px-4 py-2 text-left">Rol</th>
              <th className="px-4 py-2 text-left">Creado</th>
              <th className="px-4 py-2 text-center">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {users.length === 0 ? (
              <tr>
                <td colSpan="6" className="text-center py-6 text-gray-500">
                  No hay usuarios registrados.
                </td>
              </tr>
            ) : (
              users.map((user) => (
                <tr key={user.id} className="border-t">
                  <td className="px-4 py-2">{user.id}</td>
                  <td className="px-4 py-2">{user.name}</td>
                  <td className="px-4 py-2">{user.email}</td>
                  <td className="px-4 py-2 capitalize">{user.role}</td>
                  <td className="px-4 py-2">{user.createdAt}</td>
                  <td className="px-4 py-2 text-center space-x-2">
                    <Link
                      to={`/admin/usuarios/${user.id}/editar`}
                      className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 text-sm"
                    >
                      Editar
                    </Link>
                    <button
                      onClick={() => handleDelete(user.id)}
                      className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 text-sm"
                    >
                      Eliminar
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
