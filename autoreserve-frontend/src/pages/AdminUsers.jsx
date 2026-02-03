// Página de administración de usuarios
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getUsers, deleteUser } from "../api/adminUsersApi";

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Cargar usuarios al montar el componente
  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const data = await getUsers();
      // El backend ahora devuelve {success: true, data: PagedUserResponse}
      setUsers(data.content || data.users || []);
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error('Error cargando usuarios:', err);
    } finally {
      setLoading(false);
    }
  };

  // Manejar eliminación de usuario
  const handleDelete = async (id, userName) => {
    if (window.confirm(`¿Seguro que deseas eliminar al usuario ${userName}?`)) {
      try {
        await deleteUser(id);
        setUsers((prev) => prev.filter((u) => u.id !== id));
        alert("Usuario eliminado correctamente");
      } catch (error) {
        alert("No se pudo eliminar: " + error.message);
      }
    }
  };

  if (loading) {
    return (
      <div className="w-full max-w-[1400px] mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div className="text-center py-12">
          <p className="text-gray-600">Cargando usuarios...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="w-full max-w-[1400px] mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div className="text-center py-12">
          <p className="text-red-600">Error: {error}</p>
          <button 
            onClick={loadUsers}
            className="mt-4 px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
          >
            Reintentar
          </button>
        </div>
      </div>
    );
  }

  // Renderizar tabla de usuarios
  return (
    <div className="w-full max-w-[1400px] mx-auto py-6 px-4 sm:px-6 lg:px-8 space-y-6">
      <header className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <h1 className="text-2xl font-bold text-gray-800 tracking-tight">👥 Gestión de Usuarios</h1>
        <Link
          to="/admin/usuarios/nuevo"
          className="w-full sm:w-auto text-center px-6 py-2.5 bg-primary text-white font-semibold rounded shadow-sm hover:bg-primary-dark transition-all"
        >
          ➕ Nuevo Usuario
        </Link>
      </header>

      {/* --- VISTA MÓVIL (Cards) --- */}
      <div className="grid grid-cols-1 gap-4 md:hidden">
        {users.map((user) => (
          <div key={user.id} className="bg-white p-5 rounded-lg border border-gray-200 shadow-sm space-y-4">
            <div className="flex justify-between items-start">
              <div>
                <p className="text-[10px] font-black text-primary uppercase tracking-widest">ID: #{user.id}</p>
                <h3 className="text-lg font-bold text-gray-900 leading-tight">{user.firstName} {user.lastName}</h3>
                <p className="text-sm text-gray-500 font-medium">{user.email}</p>
              </div>
              <span className="px-2 py-1 text-[10px] font-bold rounded bg-gray-100 text-gray-700 uppercase">
                {user.role}
              </span>
            </div>
            <div className="grid grid-cols-2 gap-2 text-[11px] border-t border-gray-50 pt-3">
              <div>
                <p className="text-gray-400 font-bold uppercase">Teléfono</p>
                <p className="text-gray-700">{user.phone || "N/A"}</p>
              </div>
              <div>
                <p className="text-gray-400 font-bold uppercase">Creado</p>
                <p className="text-gray-700">{user.createdAt ? new Date(user.createdAt).toLocaleDateString() : "N/A"}</p>
              </div>
            </div>
            <div className="flex gap-2 pt-2">
              <Link to={`/admin/usuarios/${user.id}/editar`} className="flex-1 text-center py-2 border border-primary text-primary rounded font-bold text-sm hover:bg-primary hover:text-white transition-colors">Editar</Link>
              <button onClick={() => handleDelete(user.id, `${user.firstName} ${user.lastName}`)} className="flex-1 py-2 border border-red-600 text-red-600 rounded font-bold text-sm hover:bg-red-600 hover:text-white transition-colors">Eliminar</button>
            </div>
          </div>
        ))}
      </div>

      {/* --- VISTA TABLET/DESKTOP --- */}
      <div className="hidden md:block bg-white rounded-lg border border-gray-200 shadow-sm overflow-hidden">
        <table className="w-full table-fixed divide-y divide-gray-200 text-left">
          <thead className="bg-gray-50">
            <tr>
              <th className="w-[8%] px-4 py-4 text-[11px] font-bold text-gray-500 uppercase">ID</th>
              <th className="w-[27%] lg:w-[20%] px-4 py-4 text-[11px] font-bold text-gray-500 uppercase">Usuario</th>
              {/* Email: Oculto en tablet (se muestra bajo el nombre), Visible en LG (independiente) */}
              <th className="hidden lg:table-cell w-[20%] px-4 py-4 text-[11px] font-bold text-gray-500 uppercase">Email</th>
              <th className="w-[12%] px-4 py-4 text-[11px] font-bold text-gray-500 uppercase">Rol</th>
              <th className="w-[28%] lg:w-[25%] px-4 py-4 text-[11px] font-bold text-gray-500 uppercase">Contacto / Registro</th>
              <th className="w-[25%] lg:w-[15%] px-4 py-4 text-center text-[11px] font-bold text-gray-500 uppercase">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {users.map((user) => (
              <tr key={user.id} className="hover:bg-gray-50/50 transition-colors">
                <td className="px-4 py-4 text-xs font-mono text-gray-400">#{user.id}</td>
                
                <td className="px-4 py-4">
                  <div className="flex flex-col">
                    <span className="text-sm lg:text-base font-bold text-gray-900 truncate">
                      {user.firstName} {user.lastName}
                    </span>
                    {/* Solo aparece bajo el nombre si la pantalla es menor a 1024px */}
                    <span className="text-[11px] text-gray-500 lg:hidden truncate">{user.email}</span>
                  </div>
                </td>

                <td className="hidden lg:table-cell px-4 py-4 text-sm text-gray-600 truncate">
                  {user.email}
                </td>

                <td className="px-4 py-4">
                  <span className="inline-block px-2 py-0.5 text-[10px] lg:text-xs font-semibold rounded bg-gray-100 text-gray-700 uppercase">
                    {user.role}
                  </span>
                </td>

                <td className="px-4 py-4">
                  <div className="flex flex-col text-[11px] lg:text-sm text-gray-600 leading-tight">
                    <span className="font-medium">{user.phone || "Sin teléfono"}</span>
                    <span className="text-gray-400 text-[10px] lg:text-xs">
                      Reg: {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : "N/A"}
                    </span>
                  </div>
                </td>

                <td className="px-4 py-4">
                  <div className="flex flex-col lg:flex-row justify-center items-center gap-2">
                    <Link to={`/admin/usuarios/${user.id}/editar`} className="w-full lg:w-20 py-1.5 text-center border border-primary text-primary rounded text-[11px] font-bold hover:bg-primary hover:text-white transition-all shadow-sm uppercase">Editar</Link>
                    <button onClick={() => handleDelete(user.id, `${user.firstName} ${user.lastName}`)} className="w-full lg:w-20 py-1.5 text-center border border-red-600 text-red-600 rounded text-[11px] font-bold hover:bg-red-600 hover:text-white transition-all shadow-sm uppercase">Borrar</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {users.length === 0 && (
        <div className="text-center py-12 bg-white rounded-lg border border-dashed border-gray-300 italic text-gray-400">
          No hay usuarios registrados.
        </div>
      )}
    </div>
  );
}