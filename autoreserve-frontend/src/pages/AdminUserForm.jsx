import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { mockUsers } from "../data/mockUsers";

export default function AdminUserForm() {
  const navigate = useNavigate();
  const { id } = useParams(); // si existe, es edición
  const editing = Boolean(id);

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    role: "cliente",
  });

  useEffect(() => {
    if (editing) {
      const user = mockUsers.find((u) => String(u.id) === String(id));
      if (user) {
        setFormData({
          name: user.name,
          email: user.email,
          role: user.role,
        });
      }
    }
  }, [id, editing]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (editing) {
      console.log("🔄 Editando usuario:", { id, ...formData });
      // Aquí se actualizaría el usuario en la API
    } else {
      console.log("➕ Creando nuevo usuario:", formData);
      // Aquí se crearía el usuario en la API
    }

    navigate("/admin/usuarios");
  };

  return (
    <div className="max-w-2xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <h1 className="text-2xl font-bold mb-6">
        {editing ? "✏️ Editar Usuario" : "➕ Nuevo Usuario"}
      </h1>

      <form
        onSubmit={handleSubmit}
        className="bg-white shadow rounded-lg p-6 space-y-6"
      >
        {/* Nombre */}
        <div>
          <label htmlFor="name" className="block font-medium text-gray-700">
            Nombre completo
          </label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded px-3 py-2"
            required
          />
        </div>

        {/* Email */}
        <div>
          <label htmlFor="email" className="block font-medium text-gray-700">
            Correo electrónico
          </label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded px-3 py-2"
            required
          />
        </div>

        {/* Rol */}
        <div>
          <label htmlFor="role" className="block font-medium text-gray-700">
            Rol
          </label>
          <select
            id="role"
            name="role"
            value={formData.role}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded px-3 py-2"
          >
            <option value="cliente">Cliente</option>
            <option value="admin">Administrador</option>
          </select>
        </div>

        {/* Botones */}
        <div className="flex justify-end gap-4">
          <button
            type="button"
            onClick={() => navigate("/admin/usuarios")}
            className="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300"
          >
            Cancelar
          </button>
          <button
            type="submit"
            className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
          >
            {editing ? "Guardar cambios" : "Crear usuario"}
          </button>
        </div>
      </form>
    </div>
  );
}
