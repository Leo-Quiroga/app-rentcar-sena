// Pantalla de formulario para que el administrador cree o edite un auto
import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { mockAdminAutos } from "../data/mockAdminAutos";

export default function AdminAutoForm() {
  const navigate = useNavigate();
  const { id } = useParams();

  // Si hay id, estamos editando
  const existingAuto = mockAdminAutos.find((a) => a.id === id);
  // Estado del formulario
  const [formData, setFormData] = useState(
    existingAuto || {
      brand: "",
      model: "",
      year: "",
      category: "",
      pricePerDay: "",
      status: "Disponible",
    }
  );
  // Manejar cambios en los campos del formulario
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };
  // Manejar envío del formulario
  const handleSubmit = (e) => {
    e.preventDefault();

    if (id) {
      // Lógica futura: actualizar auto
      console.log("Actualizar auto:", formData);
      alert(`Auto "${formData.model}" actualizado correctamente`);
    } else {
      // Lógica futura: crear auto
      console.log("Nuevo auto:", formData);
      alert(`Auto "${formData.model}" creado correctamente`);
    }

    navigate("/admin/autos");
  };
  // Renderizar formulario
  return (
    <div className="max-w-3xl mx-auto bg-white shadow rounded-lg p-6 mt-10">
      <h1 className="text-2xl font-bold mb-6 text-neutral-dark">
        {id ? "✏️ Editar Auto" : "➕ Nuevo Auto"}
      </h1>

      <form onSubmit={handleSubmit} className="space-y-5">
        {/* Marca */}
        <div>
          <label htmlFor="brand" className="block font-medium text-sm text-gray-700">
            Marca
          </label>
          <input
            type="text"
            id="brand"
            name="brand"
            value={formData.brand}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary"
            required
          />
        </div>

        {/* Modelo */}
        <div>
          <label htmlFor="model" className="block font-medium text-sm text-gray-700">
            Modelo
          </label>
          <input
            type="text"
            id="model"
            name="model"
            value={formData.model}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary"
            required
          />
        </div>

        {/* Año */}
        <div>
          <label htmlFor="year" className="block font-medium text-sm text-gray-700">
            Año
          </label>
          <input
            type="number"
            id="year"
            name="year"
            value={formData.year}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary"
            required
          />
        </div>

        {/* Categoría */}
        <div>
          <label htmlFor="category" className="block font-medium text-sm text-gray-700">
            Categoría
          </label>
          <select
            id="category"
            name="category"
            value={formData.category}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary"
            required
          >
            <option value="">Selecciona una categoría</option>
            <option value="Sedán">Sedán</option>
            <option value="SUV">SUV</option>
            <option value="Hatchback">Hatchback</option>
            <option value="Camioneta">Camioneta</option>
          </select>
        </div>

        {/* Precio por día */}
        <div>
          <label htmlFor="pricePerDay" className="block font-medium text-sm text-gray-700">
            Precio por día
          </label>
          <input
            type="number"
            id="pricePerDay"
            name="pricePerDay"
            value={formData.pricePerDay}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary"
            required
          />
        </div>

        {/* Estado */}
        <div>
          <label htmlFor="status" className="block font-medium text-sm text-gray-700">
            Estado
          </label>
          <select
            id="status"
            name="status"
            value={formData.status}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary"
          >
            <option value="Disponible">Disponible</option>
            <option value="En mantenimiento">En mantenimiento</option>
            <option value="No disponible">No disponible</option>
          </select>
        </div>

        {/* Botones */}
        <div className="flex justify-end gap-4 pt-4">
          <button
            type="button"
            onClick={() => navigate("/admin/autos")}
            className="px-4 py-2 bg-gray-300 rounded-lg hover:bg-gray-400 transition"
          >
            Cancelar
          </button>
          <button
            type="submit"
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
          >
            {id ? "Actualizar" : "Crear"}
          </button>
        </div>
      </form>
    </div>
  );
}
