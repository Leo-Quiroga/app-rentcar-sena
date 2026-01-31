// Página de formulario para crear o editar usuarios
import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { createUser, updateUser, getUserById } from "../api/adminUsersApi";

export default function AdminUserForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const editing = Boolean(id);
  // Estado del formulario
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    password: "",
    confirmPassword: "", // <-- Nuevo campo
    role: "cliente",
  });
  // Cargar datos del usuario si estamos editando
  useEffect(() => {
    if (editing) {
      getUserById(id)
        .then((user) => {
          setFormData({
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            phone: user.phone || "",
            role: user.role,
            password: "",
            confirmPassword: "", // <-- Inicializar vacío en edición
          });
        })
        .catch(() => {
          alert("Error cargando el usuario");
          navigate("/admin/usuarios");
        });
    }
  }, [id, editing, navigate]);
  // Manejar cambios en los campos del formulario
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };
  // Manejar envío del formulario
  const handleSubmit = async (e) => {
    e.preventDefault();

    // 1. Validación de coincidencia de contraseñas
    if (formData.password !== formData.confirmPassword) {
      alert("Las contraseñas no coinciden.");
      return;
    }

    // 2. Limpiar el payload para el Back (quitar confirmPassword y password vacío si es edición)
    const payload = { ...formData };
    delete payload.confirmPassword; // Nunca se envía al back
    // Si estamos editando y la contraseña está vacía, no la enviamos
    if (editing && !payload.password) {
      delete payload.password;
    }
    // 3. Llamar a la API correspondiente
    try {
      if (editing) {
        await updateUser(id, payload);
      } else {
        await createUser(payload);
      }
      navigate("/admin/usuarios");
    } catch (error) {
      alert("Error al guardar el usuario " + error.message);
    }
  };
  // Renderizar formulario
  return (
    <div className="max-w-2xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <h1 className="text-2xl font-bold mb-6">
        {editing ? "✏️ Editar Usuario" : "➕ Nuevo Usuario"}
      </h1>

      <form onSubmit={handleSubmit} className="bg-white shadow rounded-lg p-6 space-y-6">
        {/* Nombres y Apellidos */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label htmlFor="firstName" className="block font-medium text-gray-700">Nombre</label>
            <input
              type="text"
              id="firstName"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded px-3 py-2"
              required
            />
          </div>
          <div>
            <label htmlFor="lastName" className="block font-medium text-gray-700">Apellido</label>
            <input
              type="text"
              id="lastName"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded px-3 py-2"
              required
            />
          </div>
        </div>

        {/* Email y Teléfono */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label htmlFor="email" className="block font-medium text-gray-700">Email</label>
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
          <div>
            <label htmlFor="phone" className="block font-medium text-gray-700">Teléfono</label>
            <input
              type="text"
              id="phone"
              name="phone"
              value={formData.phone}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded px-3 py-2"
              required
            />
          </div>
        </div>

        {/* Password y Confirmación */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label htmlFor="password" className="block font-medium text-gray-700">
              Contraseña {editing && <span className="text-xs text-gray-400">(opcional)</span>}
            </label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded px-3 py-2"
              required={!editing}
              placeholder={editing ? "********" : "Asigna una contraseña"}
            />
          </div>
          <div>
            <label htmlFor="confirmPassword" className="block font-medium text-gray-700">
              Confirmar Contraseña
            </label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded px-3 py-2"
              required={!editing || formData.password.length > 0}
              placeholder={editing ? "********" : "Repite la contraseña"}
            />
          </div>
        </div>

        {/* Rol */}
        <div>
          <label htmlFor="role" className="block font-medium text-gray-700">Rol de Usuario</label>
          <select
            id="role"
            name="role"
            value={formData.role}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded px-3 py-2 bg-gray-50"
          >
            <option value="ADMIN">Administrador</option>
            <option value="CLIENT">Cliente</option>
          </select>
        </div>

        {/* Botones */}
        <div className="flex justify-end gap-4 pt-4">
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