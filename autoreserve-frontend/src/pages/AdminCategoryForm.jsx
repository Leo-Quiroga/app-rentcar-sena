// Pantalla de formulario para que el administrador cree o edite una categoría
import { useParams, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { getAdminCategoryById, createCategory, updateCategory } from "../api/adminCategoriesApi";

export default function AdminCategoryForm() {
  const { id } = useParams();
  const navigate = useNavigate();

  // Si hay id, es edición; si no, es creación
  const isEditing = Boolean(id);
  const [category, setCategory] = useState({ name: "", description: "", image: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Cargar datos de la categoría si estamos editando
  useEffect(() => {
    if (isEditing) {
      loadCategory();
    }
  }, [id, isEditing]);

  const loadCategory = async () => {
    try {
      setLoading(true);
      const data = await getAdminCategoryById(id);
      setCategory({
        name: data.name || "",
        description: data.description || "",
        image: data.image || ""
      });
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error('Error cargando categoría:', err);
    } finally {
      setLoading(false);
    }
  };
  // Manejar cambios en los campos del formulario
  const handleChange = (e) => {
    const { name, value } = e.target;
    setCategory((prev) => ({ ...prev, [name]: value }));
  };
  // Manejar envío del formulario
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!category.name.trim()) {
      alert("El nombre es obligatorio");
      return;
    }

    try {
      setLoading(true);
      if (isEditing) {
        await updateCategory(id, category);
        alert("Categoría actualizada exitosamente");
      } else {
        await createCategory(category);
        alert("Categoría creada exitosamente");
      }
      navigate("/admin/categorias");
    } catch (err) {
      alert('Error: ' + err.message);
      console.error('Error guardando categoría:', err);
    } finally {
      setLoading(false);
    }
  };
  // Renderizar formulario
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

        {/* Descripción */}
        <div>
          <label
            htmlFor="description"
            className="block text-sm font-medium text-gray-700 mb-1"
          >
            Descripción
          </label>
          <textarea
            id="description"
            name="description"
            value={category.description}
            onChange={handleChange}
            className="w-full border rounded-lg px-3 py-2 focus:ring-primary focus:border-primary"
            placeholder="Descripción de la categoría"
            rows="3"
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
            placeholder="https://ejemplo.com/imagen.jpg o ruta local"
          />
          {category.image && (
            <img
              src={category.image}
              alt="Vista previa"
              className="h-24 w-full object-cover rounded mt-2"
              onError={(e) => {
                e.target.style.display = 'none';
              }}
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
            disabled={loading}
            className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark disabled:opacity-50"
          >
            {loading ? "Guardando..." : (isEditing ? "Guardar cambios" : "Crear Categoría")}
          </button>
        </div>
      </form>
    </div>
  );
}
