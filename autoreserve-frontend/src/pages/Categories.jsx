// Página de categorías de vehículos
import { useState, useMemo, useEffect } from "react";
import CategoryCard from "../components/CategoryCard";
import { getCategories } from "../api/categoriesApi";

export default function Categories() {
  const [q, setQ] = useState("");
  const [sort, setSort] = useState("name_asc");
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Cargar categorías desde la API
  useEffect(() => {
    const loadCategories = async () => {
      try {
        setLoading(true);
        const data = await getCategories();
        setCategories(data);
        setError(null);
      } catch (err) {
        setError(err.message);
        console.error('Error cargando categorías:', err);
      } finally {
        setLoading(false);
      }
    };

    loadCategories();
  }, []);

  // Filtrado + ordenamiento
  const filtered = useMemo(() => {
    const result = categories.filter((c) =>
      c.name.toLowerCase().includes(q.trim().toLowerCase())
    );

    // No mutamos el array original: creamos copia antes de ordenar
    const copy = [...result];
    if (sort === "name_asc") return copy.sort((a, b) => a.name.localeCompare(b.name));
    if (sort === "name_desc") return copy.sort((a, b) => b.name.localeCompare(a.name));
    return copy;
  }, [categories, q, sort]);

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
        <div className="text-center py-12">
          <p className="text-gray-600">Cargando categorías...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
        <div className="text-center py-12">
          <p className="text-red-600">Error: {error}</p>
        </div>
      </div>
    );
  }

  // Renderizar lista de categorías con buscador y ordenamiento
  return (
    <div className="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <header className="mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">Categorías</h1>
          <p className="text-sm text-gray-600">Explora por tipo de vehículo y descubre sus autos disponibles.</p>
        </div>

        <div className="flex gap-3 items-center">
          <label htmlFor="q" className="sr-only">Buscar categorías</label>
          <input
            id="q"
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="Buscar categoría..."
            className="border border-gray-300 rounded px-3 py-2 w-48"
            aria-label="Buscar categorías"
          />

          <label htmlFor="sort" className="sr-only">Ordenar</label>
          <select
            id="sort"
            value={sort}
            onChange={(e) => setSort(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2"
            aria-label="Ordenar categorías"
          >
            <option value="name_asc">Nombre ↑</option>
            <option value="name_desc">Nombre ↓</option>
          </select>
        </div>
      </header>

      <section>
        {filtered.length === 0 ? (
          <p className="text-gray-600">No se encontraron categorías con esa búsqueda.</p>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {filtered.map((category) => (
              // CategoryCard ya tiene el Link hacia /categorias/:id
              <CategoryCard key={category.id} category={category} />
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
