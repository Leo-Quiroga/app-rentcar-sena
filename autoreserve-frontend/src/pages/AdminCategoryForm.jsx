import { useParams, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { mockCategories } from "../data/mockCategories";

export default function AdminCategoryForm() {
  const { id } = useParams();
  const navigate = useNavigate();

  // Si hay id, es edición; si no, es creación
  const isEditing = Boolean(id);
  const [category, setCategory] = useState({ name: "", image: "" });

  useEffect(() => {
    if (isEditing) {
      const found = mockCategories.find((c) => String(c.id) === String(id));
      if (found) {
        setCategory(found);
      }
    }
  }, [id, isEditing]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCategory((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!category.name.trim()) {
      alert("El nombre es obligatorio");
      return;
    }
    if (!category.image.trim()) {
      alert("La imagen es obligatoria");
      return;
    }

    if (isEditing) {
      console.log("✅ Categoría actualizada:", category);
    } else {
      console.log("✅ Categoría creada:", {
        ...category,
        id: Date.now().toString(),
      });
    }

    navigate("/admin/categorias");
  };

  return (
    <div className="max-w-lg mx-auto bg-white shadow rounded-lg p-6 mt-10">
      <h1 className="text-2xl font-bold mb-6 text-neutral-dark">
        {isEditing ? "✏️ Editar Categoría" : "➕ Nueva Categoría"}
      </h1>

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Nombre */}
        <div>
          <label
            htmlFor="name"
            className="block text-sm font-medium text-gray-700 mb-1"
          >
            Nombre
          </label>
          <input
            id="name"
            name="name"
            type="text"
            value={category.name}
            onChange={handleChange}
            className="w-full border rounded-lg px-3 py-2 focus:ring-primary focus:border-primary"
            placeholder="Ej: SUV"
            required
          />
        </div>

        {/* Imagen */}
        <div>
          <label
            htmlFor="image"
            className="block text-sm font-medium text-gray-700 mb-1"
          >
            URL de Imagen
          </label>
          <input
            id="image"
            name="image"
            type="url"
            value={category.image}
            onChange={handleChange}
            className="w-full border rounded-lg px-3 py-2 focus:ring-primary focus:border-primary"
            placeholder="https://ejemplo.com/imagen.jpg"
            required
          />
          {category.image && (
            <img
              src={category.image}
              alt="Vista previa"
              className="h-24 w-full object-cover rounded mt-2"
            />
          )}
        </div>

        {/* Botones */}
        <div className="flex justify-end gap-3">
          <button
            type="button"
            onClick={() => navigate("/admin/categorias")}
            className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"
          >
            Cancelar
          </button>
          <button
            type="submit"
            className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
          >
            {isEditing ? "Guardar cambios" : "Crear Categoría"}
          </button>
        </div>
      </form>
    </div>
  );
}
