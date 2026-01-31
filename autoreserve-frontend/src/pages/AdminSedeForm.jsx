// Formulario para crear o editar una sede
import { useParams, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { mockSedes } from "../data/mockSedes";

export default function AdminSedeForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);
 // Estado del formulario
  const [form, setForm] = useState({
    nombre: "",
    direccion: "",
    ciudad: "",
    telefono: "",
  });

  // Si estamos en modo edición, cargamos datos del mock
  useEffect(() => {
    if (isEdit) {
      const sede = mockSedes.find((s) => String(s.id) === id);
      if (sede) {
        setForm({
          nombre: sede.nombre,
          direccion: sede.direccion,
          ciudad: sede.ciudad,
          telefono: sede.telefono,
        });
      }
    }
  }, [id, isEdit]);
  // Manejar cambios en los campos del formulario
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };
  // Manejar envío del formulario
  const handleSubmit = (e) => {
    e.preventDefault();

    if (isEdit) {
      console.log("✏️ Actualizando sede:", { id, ...form });
      alert(`Sede ${form.nombre} actualizada correctamente`);
    } else {
      const newSede = { id: Date.now(), ...form };
      console.log("➕ Creando nueva sede:", newSede);
      alert(`Sede ${form.nombre} creada correctamente`);
    }

    navigate("/admin/sedes");
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
            name="nombre"
            value={form.nombre}
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
            name="direccion"
            value={form.direccion}
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
            name="ciudad"
            value={form.ciudad}
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
            name="telefono"
            value={form.telefono}
            onChange={handleChange}
            required
            className="mt-1 block w-full border border-gray-300 rounded px-3 py-2"
          />
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
            className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition"
          >
            {isEdit ? "Guardar Cambios" : "Crear Sede"}
          </button>
        </div>
      </form>
    </div>
  );
}
