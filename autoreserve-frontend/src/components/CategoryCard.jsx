import { Link } from "react-router-dom";
// Componente para mostrar una tarjeta de categoría
export default function CategoryCard({ category, onSelect }) {
  return (
    <div className="bg-white rounded-lg shadow-sm overflow-hidden hover:shadow-md transition flex flex-col">
      {/* Imagen de categoría */}
      <Link
        to={`/categorias/${category.id}`}
        className="block h-36 overflow-hidden"
        aria-label={`Ver más sobre categoría ${category.name}`}
      >
        <img
          src={category.image}
          alt={`Categoría ${category.name}`}
          className="w-full h-full object-cover"
        />
      </Link>

      {/* Contenido */}
      <div className="p-4 flex flex-col items-center">
        <h3 className="font-semibold text-lg text-gray-800">
          {category.name}
        </h3>

        {/* Botones */}
        <div className="mt-4 flex flex-col sm:flex-row gap-2 w-full justify-center">
          <button
            onClick={() => onSelect && onSelect(category)}
            className="flex-1 px-3 py-2 text-sm font-medium bg-primary text-white rounded-md hover:bg-blue-700 transition"
            aria-label={`Seleccionar categoría ${category.name}`}
          >
            Seleccionar
          </button>

          <Link
            to={`/categorias/${category.id}`}
            className="flex-1 px-3 py-2 text-sm text-gray-800 border border-yellow-400 rounded-md hover:bg-yellow-400 hover:text-gray-900 transition text-center"
          >
            Ver más
          </Link>
        </div>
      </div>
    </div>
  );
}
