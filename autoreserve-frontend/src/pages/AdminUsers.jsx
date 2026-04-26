// Gestión de usuarios con filtros y ordenadores
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getUsers, deleteUser } from "../api/adminUsersApi";

const normalize = (str) =>
  String(str ?? "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();

function SortIcon({ col, sortCol, sortDir }) {
  if (sortCol !== col) return <span className="ml-1 text-gray-300">↕</span>;
  return <span className="ml-1">{sortDir === "asc" ? "↑" : "↓"}</span>;
}

export default function AdminUsers() {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filtros
  const [search, setSearch] = useState("");
  const [filterRole, setFilterRole] = useState("");

  // Ordenamiento (por defecto: apellido)
  const [sortCol, setSortCol] = useState("lastName");
  const [sortDir, setSortDir] = useState("asc");

  useEffect(() => { loadUsers(); }, []);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const data = await getUsers();
      setUsers(data.content || data.users || []);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSort = (col) => {
    if (sortCol === col) setSortDir(d => d === "asc" ? "desc" : "asc");
    else { setSortCol(col); setSortDir("asc"); }
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`¿Eliminar al usuario ${name}?`)) return;
    try {
      await deleteUser(id);
      setUsers(prev => prev.filter(u => u.id !== id));
    } catch (err) {
      alert("No se pudo eliminar: " + err.message);
    }
  };

  const filtered = users.filter(u => {
    const term = normalize(search);
    const matchSearch = !search ||
      normalize(u.firstName).includes(term) ||
      normalize(u.lastName).includes(term) ||
      normalize(u.email).includes(term) ||
      normalize(u.phone).includes(term) ||
      String(u.id).includes(term);
    const matchRole = !filterRole || u.role === filterRole;
    return matchSearch && matchRole;
  });

  const sorted = [...filtered].sort((a, b) => {
    const va = normalize(a[sortCol] ?? "");
    const vb = normalize(b[sortCol] ?? "");
    return sortDir === "asc" ? va.localeCompare(vb) : vb.localeCompare(va);
  });

  const clearFilters = () => { setSearch(""); setFilterRole(""); };
  const hasFilters = search || filterRole;

  if (loading) return (
    <div className="max-w-7xl mx-auto py-10 px-4 text-center">
      <p className="text-gray-600">Cargando usuarios...</p>
    </div>
  );

  if (error) return (
    <div className="max-w-7xl mx-auto py-10 px-4 text-center">
      <p className="text-red-600">Error: {error}</p>
      <button onClick={loadUsers} className="mt-4 px-4 py-2 bg-primary text-white rounded">Reintentar</button>
    </div>
  );

  return (
    <div className="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      {/* Encabezado */}
      <div className="mb-6">
        <button onClick={() => navigate("/admin")} className="text-primary hover:underline text-sm mb-2">
          ← Volver al Dashboard
        </button>
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-800">👥 Gestión de Usuarios</h1>
            <p className="text-sm text-gray-500 mt-1">{sorted.length} usuario(s)</p>
          </div>
          <button onClick={() => navigate("/admin/usuarios/nuevo")}
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition text-sm">
            ➕ Nuevo Usuario
          </button>
        </div>
      </div>

      {/* Filtros */}
      <div className="bg-white shadow-sm rounded-lg p-4 mb-6">
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
          <input type="text" placeholder="Buscar nombre, email, teléfono, ID..."
            value={search} onChange={e => setSearch(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary col-span-2" />
          <select value={filterRole} onChange={e => setFilterRole(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary">
            <option value="">Todos los roles</option>
            <option value="CLIENT">Cliente</option>
            <option value="ADMIN">Administrador</option>
          </select>
        </div>
        {hasFilters && (
          <button onClick={clearFilters} className="mt-3 text-xs text-gray-500 hover:text-red-500 underline">
            Limpiar filtros
          </button>
        )}
      </div>

      {/* Tabla */}
      <div className="bg-white shadow rounded-lg overflow-x-auto">
        <table className="w-full table-auto text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              {[
                { col: "id",        label: "ID" },
                { col: "lastName",  label: "Nombre" },
                { col: "email",     label: "Email" },
                { col: "role",      label: "Rol" },
                { col: "phone",     label: "Teléfono" },
                { col: "createdAt", label: "Registro" },
              ].map(({ col, label }) => (
                <th key={col} onClick={() => handleSort(col)}
                  className="px-4 py-3 text-left font-semibold text-gray-700 cursor-pointer hover:bg-gray-100 select-none whitespace-nowrap">
                  {label}<SortIcon col={col} sortCol={sortCol} sortDir={sortDir} />
                </th>
              ))}
              <th className="px-4 py-3 text-center font-semibold text-gray-700">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {sorted.map(user => (
              <tr key={user.id} className="border-t hover:bg-gray-50 transition">
                <td className="px-4 py-3 text-xs font-mono text-gray-500">#{user.id}</td>
                <td className="px-4 py-3">
                  <p className="font-medium text-gray-900">{user.firstName} {user.lastName}</p>
                </td>
                <td className="px-4 py-3 text-sm text-gray-600">{user.email}</td>
                <td className="px-4 py-3">
                  <span className={`inline-block px-2 py-0.5 rounded-full text-xs font-semibold ${
                    user.role === "ADMIN"
                      ? "bg-purple-100 text-purple-700"
                      : "bg-gray-100 text-gray-600"
                  }`}>
                    {user.role === "ADMIN" ? "Administrador" : "Cliente"}
                  </span>
                </td>
                <td className="px-4 py-3 text-sm text-gray-600">{user.phone || "—"}</td>
                <td className="px-4 py-3 text-xs text-gray-500">
                  {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : "—"}
                </td>
                <td className="px-4 py-3">
                  <div className="flex gap-1 justify-center">
                    <button onClick={() => navigate(`/admin/usuarios/${user.id}/editar`)}
                      className="text-xs px-3 py-1 border border-primary text-primary rounded hover:bg-primary hover:text-white transition">
                      Editar
                    </button>
                    <button onClick={() => handleDelete(user.id, `${user.firstName} ${user.lastName}`)}
                      className="text-xs px-3 py-1 border border-red-500 text-red-500 rounded hover:bg-red-500 hover:text-white transition">
                      Eliminar
                    </button>
                  </div>
                </td>
              </tr>
            ))}
            {sorted.length === 0 && (
              <tr>
                <td colSpan="7" className="text-center py-10 text-gray-500">
                  {hasFilters ? "No hay usuarios con los filtros aplicados." : "No hay usuarios registrados."}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
