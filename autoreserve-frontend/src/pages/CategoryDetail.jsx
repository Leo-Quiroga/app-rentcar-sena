// Página de detalle de categoría de vehículos
import { useParams, Link } from "react-router-dom";
import { categories, cars } from "../data/mockData";
import CarCard from "../components/CarCard";

export default function CategoryDetail() {
  const { id } = useParams();
  const category = categories.find((c) => String(c.id) === String(id));
  // Si no se encuentra la categoría, mostrar mensaje de error
  if (!category) {
    return (
      <div className="text-center py-12">
        <h2 className="text-2xl font-bold">Categoría no encontrada</h2>
        <p className="mt-4 text-gray-600">La categoría que buscas no existe.</p>
        <Link
          to="/"
          className="mt-4 inline-block px-4 py-2 bg-primary text-white rounded"
        >
          Volver al inicio
        </Link>
      </div>
    );
  }
  // Filtrar autos que pertenecen a esta categoría
  const categoryCars = cars.filter((car) => car.category === category.name);
  // Renderizar detalle de categoría con lista de autos
  return (
    <div className="space-y-6">
      <header className="flex items-center gap-4">
        <img
          src={category.image}
          alt={`Imagen de la categoría ${category.name}`}
          className="h-20 object-cover rounded"
        />
        <div>
          <h1 className="text-3xl font-bold">{category.name}</h1>
          <p className="text-gray-600">
            Explora los autos disponibles en esta categoría.
          </p>
        </div>
      </header>

      <section>
        <h2 className="text-lg sm:text-xl font-semibold text-neutral-dark mb-4">
          Autos en {category.name}
        </h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {categoryCars.map((car) => (
            <CarCard key={car.id} car={car} />
          ))}
        </div>
      </section>
    </div>
  );
}
