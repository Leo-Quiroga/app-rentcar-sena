// Formulario para crear o editar una sede
import { useParams, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { getAdminBranchById, createBranch, updateBranch } from "../api/adminBranchesApi";

export default function AdminSedeForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);

  // Estado del formulario
  const [form, setForm] = useState({
    name: "",
    address: "",
    city: "",
    phone: "",
    image: ""
  });
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Si estamos en modo edición, cargamos datos de la API
  useEffect(() => {
    if (isEdit) {
      loadBranch();
    }
  }, [id, isEdit]);

  const loadBranch = async () => {
    try {
      setLoading(true);
      const data = await getAdminBranchById(id);
      setForm({
        name: data.name || "",
        address: data.address || "",
        city: data.city || "",
        phone: data.phone || "",
        image: data.image || ""
      });
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error('Error cargando sede:', err);
    } finally {
      setLoading(false);
    }
  };
  // Manejar cambios en los campos del formulario
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };
  // Manejar envío del formulario
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!form.name || !form.address || !form.city || !form.phone) {
      alert("Todos los campos son obligatorios");
      return;
    }

    try {
      setLoading(true);
      if (isEdit) {
        await updateBranch(id, form);
        alert(`Sede ${form.name} actualizada correctamente`);
      } else {
        await createBranch(form);
        alert(`Sede ${form.name} creada correctamente`);
      }
      navigate("/admin/sedes");
    } catch (err) {
      alert('Error: ' + err.message);
      console.error('Error guardando sede:', err);
    } finally {
      setLoading(false);
    }
  };
  // Renderizar formulario
  return (
    <div className="max-w-2xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <h1 className="text-2xl font-bold mb-6">
        {isEdit ? "✏️ Editar Sede" : "➕ Nueva Sede"}
      </h1>

      <form
        onSubmit={handleSubmit}
        className="bg-white shadow rounded-lg p-6 space-y-4"
      >
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Nombre
          </label>
          <input
            type="text"
            name="name"
            value={form.name}
            onChange={handleChange}
            required
            className="mt-1 block w-full border border-gray-300 rounded px-3 py-2"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Dirección
          </label>
          <input
            type="text"
            name="address"
            value={form.address}
            onChange={handleChange}
            required
            className="mt-1 block w-full border border-gray-300 rounded px-3 py-2"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Ciudad
          </label>
          <input
            type="text"
            name="city"
            value={form.city}
            onChange={handleChange}
            required
            className="mt-1 block w-full border border-gray-300 rounded px-3 py-2"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Teléfono
          </label>
          <input
            type="text"
            name="phone"
            value={form.phone}
            onChange={handleChange}
            required
            className="mt-1 block w-full border border-gray-300 rounded px-3 py-2"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            URL de Imagen
          </label>
          <input
            type="url"
            name="image"
            value={form.image}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded px-3 py-2"
            placeholder="https://ejemplo.com/imagen.jpg o ruta local"
          />
          {form.image && (
            <img
              src={form.image}
              alt="Vista previa"
              className="h-24 w-full object-cover rounded mt-2"
              onError={(e) => {
                e.target.style.display = 'none';
              }}
            />
          )}
        </div>

        <div className="flex justify-end gap-3 mt-6">
          <button
            type="button"
            onClick={() => navigate("/admin/sedes")}
            className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400 transition"
          >
            Cancelar
          </button>
          <button
            type="submit"
            disabled={loading}
            className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition disabled:opacity-50"
          >
            {loading ? "Guardando..." : (isEdit ? "Guardar Cambios" : "Crear Sede")}
          </button>
        </div>
      </form>
    </div>
  );
}
