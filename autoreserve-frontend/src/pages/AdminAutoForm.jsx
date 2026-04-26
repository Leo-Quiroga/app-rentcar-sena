// Formulario para crear o editar un modelo de vehículo
import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { createCarModel, updateCarModel, getAdminCarModelById } from "../api/adminCarsApi";
import { getAdminCategories } from "../api/adminCategoriesApi";
import { getAdminBranches } from "../api/adminBranchesApi";

export default function AdminAutoForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditing = Boolean(id);

  const [formData, setFormData] = useState({
    categoryId: "",
    brand: "",
    model: "",
    year: "",
    branchId: "",
    pricePerDay: "",
    image: "",
    description: "",
    quantity: 1,
  });

  const [categories, setCategories] = useState([]);
  const [branches, setBranches] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadData = async () => {
      try {
        const [cats, brs] = await Promise.all([getAdminCategories(), getAdminBranches()]);
        setCategories(cats);
        setBranches(brs);
        if (isEditing) {
          const data = await getAdminCarModelById(id);
          setFormData({
            categoryId: data.categoryId || "",
            brand: data.brand || "",
            model: data.model || "",
            year: data.year || "",
            branchId: "",
            pricePerDay: data.pricePerDay || "",
            image: data.image || "",
            description: data.description || "",
            quantity: 1,
          });
        }
      } catch (err) {
        setError(err.message);
      }
    };
    loadData();
  }, [id, isEditing]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      setLoading(true);
      const payload = {
        ...formData,
        year: parseInt(formData.year),
        pricePerDay: parseFloat(formData.pricePerDay),
        categoryId: parseInt(formData.categoryId),
        branchId: parseInt(formData.branchId),
        quantity: parseInt(formData.quantity),
      };

      if (isEditing) {
        await updateCarModel(id, payload);
        alert(`Modelo "${formData.brand} ${formData.model}" actualizado correctamente`);
      } else {
        await createCarModel(payload);
        alert(`Modelo "${formData.brand} ${formData.model}" creado con ${formData.quantity} unidad(es) pendiente(s) de identificación`);
      }
      navigate("/admin/autos");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto bg-white shadow rounded-lg p-6 mt-10">
      <div className="mb-6">
        <button onClick={() => navigate("/admin/autos")} className="text-primary hover:underline text-sm mb-2">
          ← Volver a Gestión de Vehículos
        </button>
        <h1 className="text-2xl font-bold text-neutral-dark">
          {isEditing ? "✏️ Editar Modelo" : "➕ Nuevo Modelo de Vehículo"}
        </h1>
        {!isEditing && (
          <p className="text-sm text-gray-500 mt-1">
            Las unidades se crearán en estado "Pendiente de identificación". Luego podrás asignarles placa y color individualmente.
          </p>
        )}
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 rounded p-3 mb-4 text-sm">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Categoría */}
        <div>
          <label className="block text-sm font-medium text-gray-700">Categoría *</label>
          <select name="categoryId" value={formData.categoryId} onChange={handleChange} required
            className="mt-1 w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary">
            <option value="">Selecciona una categoría</option>
            {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </div>

        {/* Marca y Modelo en fila */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Marca *</label>
            <input type="text" name="brand" value={formData.brand} onChange={handleChange} required
              placeholder="Toyota, Honda, Mazda..."
              className="mt-1 w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Modelo *</label>
            <input type="text" name="model" value={formData.model} onChange={handleChange} required
              placeholder="Corolla, Civic, CX-5..."
              className="mt-1 w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary" />
          </div>
        </div>

        {/* Año y Precio en fila */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Año *</label>
            <input type="number" name="year" value={formData.year} onChange={handleChange} required
              min="1900" max="2030" placeholder="2024"
              className="mt-1 w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Precio por día *</label>
            <input type="number" name="pricePerDay" value={formData.pricePerDay} onChange={handleChange} required
              min="0.01" step="0.01" placeholder="150000"
              className="mt-1 w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary" />
          </div>
        </div>

        {/* Sede */}
        <div>
          <label className="block text-sm font-medium text-gray-700">Sede principal *</label>
          <select name="branchId" value={formData.branchId} onChange={handleChange} required={!isEditing}
            className="mt-1 w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary">
            <option value="">Selecciona una sede</option>
            {branches.map(b => <option key={b.id} value={b.id}>{b.name} — {b.city}</option>)}
          </select>
        </div>

        {/* Cantidad (solo al crear) */}
        {!isEditing && (
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Cantidad de unidades a registrar *
            </label>
            <input type="number" name="quantity" value={formData.quantity} onChange={handleChange} required
              min="1" max="50"
              className="mt-1 w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary" />
            <p className="text-xs text-gray-400 mt-1">
              Se crearán {formData.quantity} unidad(es) en estado "Pendiente de identificación". Deberás asignarles placa y color individualmente.
            </p>
          </div>
        )}

        {/* Imagen */}
        <div>
          <label className="block text-sm font-medium text-gray-700">URL de imagen</label>
          <input type="text" name="image" value={formData.image} onChange={handleChange}
            placeholder="https://ejemplo.com/imagen.jpg"
            className="mt-1 w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary" />
          {formData.image && (
            <img src={formData.image} alt="Vista previa" className="h-24 w-full object-cover rounded mt-2"
              onError={e => e.target.style.display = "none"} />
          )}
        </div>

        {/* Descripción */}
        <div>
          <label className="block text-sm font-medium text-gray-700">Descripción</label>
          <textarea name="description" value={formData.description} onChange={handleChange} rows={3}
            placeholder="Características del vehículo, equipamiento, etc."
            className="mt-1 w-full border border-gray-300 rounded-lg p-2 focus:ring-primary focus:border-primary" />
        </div>

        {/* Botones */}
        <div className="flex justify-end gap-4 pt-4">
          <button type="button" onClick={() => navigate("/admin/autos")}
            className="px-4 py-2 bg-gray-200 rounded-lg hover:bg-gray-300 transition">
            Cancelar
          </button>
          <button type="submit" disabled={loading}
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition disabled:opacity-50">
            {loading ? "Guardando..." : isEditing ? "Actualizar modelo" : `Crear modelo con ${formData.quantity} unidad(es)`}
          </button>
        </div>
      </form>
    </div>
  );
}
