// Pantalla de formulario para que el administrador cree o edite un auto
import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getAdminCarById, createCar, updateCar } from "../api/adminCarsApi";
import { getAdminCategories } from "../api/adminCategoriesApi";
import { getAdminBranches } from "../api/adminBranchesApi";

export default function AdminAutoForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditing = Boolean(id);

  // Estado del formulario
  const [formData, setFormData] = useState({
    brand: "",
    model: "",
    year: "",
    plate: "",
    pricePerDay: "",
    categoryId: "",
    branchId: "",
    image: ""
  });
  
  const [categories, setCategories] = useState([]);
  const [branches, setBranches] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Cargar datos iniciales
  useEffect(() => {
    loadInitialData();
    if (isEditing) {
      loadCar();
    }
  }, [id, isEditing]);

  const loadInitialData = async () => {
    try {
      const [categoriesData, branchesData] = await Promise.all([
        getAdminCategories(),
        getAdminBranches()
      ]);
      setCategories(categoriesData);
      setBranches(branchesData);
    } catch (err) {
      console.error('Error cargando datos iniciales:', err);
    }
  };

  const loadCar = async () => {
    try {
      setLoading(true);
      const data = await getAdminCarById(id);
      setFormData({
        brand: data.brand || "",
        model: data.model || "",
        year: data.year || "",
        plate: data.plate || "",
        pricePerDay: data.pricePerDay || "",
        categoryId: data.categoryId || "",
        branchId: data.branchId || "",
        image: data.image || ""
      });
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error('Error cargando auto:', err);
    } finally {
      setLoading(false);
    }
  };
  // Manejar cambios en los campos del formulario
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };
  // Manejar envío del formulario
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.brand || !formData.model || !formData.year || !formData.plate || !formData.pricePerDay || !formData.categoryId || !formData.branchId) {
      alert("Todos los campos son obligatorios");
      return;
    }

    try {
      setLoading(true);
      const carData = {
        ...formData,
        year: parseInt(formData.year),
        pricePerDay: parseFloat(formData.pricePerDay),
        categoryId: parseInt(formData.categoryId),
        branchId: parseInt(formData.branchId)
      };

      if (isEditing) {
        await updateCar(id, carData);
        alert(`Auto "${formData.model}" actualizado correctamente`);
      } else {
        await createCar(carData);
        alert(`Auto "${formData.model}" creado correctamente`);
      }

      navigate("/admin/autos");
    } catch (err) {
      alert('Error: ' + err.message);
      console.error('Error guardando auto:', err);
    } finally {
      setLoading(false);
    }
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

        {/* Placa */}
        <div>
          <label htmlFor="plate" className="block font-medium text-sm text-gray-700">
            Placa
          </label>
          <input
            type="text"
            id="plate"
            name="plate"
            value={formData.plate}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary"
            required
          />
        </div>

        {/* Categoría */}
        <div>
          <label htmlFor="categoryId" className="block font-medium text-sm text-gray-700">
            Categoría
          </label>
          <select
            id="categoryId"
            name="categoryId"
            value={formData.categoryId}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary"
            required
          >
            <option value="">Selecciona una categoría</option>
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </select>
        </div>

        {/* Sede */}
        <div>
          <label htmlFor="branchId" className="block font-medium text-sm text-gray-700">
            Sede
          </label>
          <select
            id="branchId"
            name="branchId"
            value={formData.branchId}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary"
            required
          >
            <option value="">Selecciona una sede</option>
            {branches.map((branch) => (
              <option key={branch.id} value={branch.id}>
                {branch.name}
              </option>
            ))}
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

        {/* Imagen */}
        <div>
          <label htmlFor="image" className="block font-medium text-sm text-gray-700">
            URL de Imagen
          </label>
          <input
            type="url"
            id="image"
            name="image"
            value={formData.image}
            onChange={handleChange}
            className="mt-1 block w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary"
            placeholder="https://ejemplo.com/imagen.jpg o ruta local"
          />
          {formData.image && (
            <img
              src={formData.image}
              alt="Vista previa"
              className="h-24 w-full object-cover rounded mt-2"
              onError={(e) => {
                e.target.style.display = 'none';
              }}
            />
          )}
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
            disabled
          >
            <option value="AVAILABLE">Disponible</option>
            <option value="MAINTENANCE">En mantenimiento</option>
            <option value="RENTED">Rentado</option>
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
            disabled={loading}
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition disabled:opacity-50"
          >
            {loading ? "Guardando..." : (isEditing ? "Actualizar" : "Crear")}
          </button>
        </div>
      </form>
    </div>
  );
}
