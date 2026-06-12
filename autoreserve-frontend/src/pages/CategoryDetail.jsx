// Página de detalle de categoría de vehículos
import { useParams, Link } from "react-router-dom";
import { useState, useEffect } from "react";
import { getCategoryById } from "../api/categoriesApi";
import { getCars } from "../api/carsApi";
import CarCard from "../components/CarCard";
import ModalCarDetail from "../components/ModalCarDetail";
import { getImageUrl } from "../utils/imageUtils";

export default function CategoryDetail() {
  const { id } = useParams();
  const [category, setCategory] = useState(null);
  const [categoryCars, setCategoryCars] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCar, setSelectedCar] = useState(null);

  // Cargar datos de la categoría y sus autos
  useEffect(() => {
    const loadCategoryData = async () => {
      try {
        setLoading(true);
        setError(null);
        
        // Cargar categoría y autos en paralelo
        const [categoryData, carsData] = await Promise.all([
          getCategoryById(id),
          getCars({ categoryId: id })
        ]);
        
        setCategory(categoryData);
        setCategoryCars(Array.isArray(carsData) ? carsData : carsData.content || []);
      } catch (err) {
        console.error('Error cargando datos de categoría:', err);
        setError(err.message || 'Error al cargar la categoría');
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      loadCategoryData();
    }
  }, [id]);

  // Estados de carga y error
  if (loading) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-600">Cargando categoría...</p>
      </div>
    );
  }

  if (error || !category) {
    return (
      <div className="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
        <div className="text-center py-12">
          <h2 className="text-2xl font-bold">Categoría no encontrada</h2>
          <p className="mt-4 text-gray-600">{error || 'La categoría que buscas no existe.'}</p>
          <Link
            to="/categorias"
            className="mt-4 inline-block px-4 py-2 bg-primary text-white rounded"
          >
            Volver a categorías
          </Link>
        </div>
      </div>
    );
  }
  
  // Renderizar detalle de categoría con lista de autos
  return (
    <div className="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <div className="space-y-6">
        {/* Botón volver */}
        <div className="flex items-center">
          <Link
            to="/categorias"
            className="flex items-center text-primary hover:text-blue-700 transition"
          >
            <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
            Volver a categorías
          </Link>
        </div>
        {/* Header de la categoría */}
        <header className="flex items-center gap-4">
          <img
            src={getImageUrl(category.image, 'category')}
            alt={`Imagen de la categoría ${category.name}`}
            className="h-20 object-cover rounded"
            onError={(e) => {
              e.target.src = getImageUrl(null, 'category');
            }}
          />
          <div>
            <h1 className="text-3xl font-bold">{category.name}</h1>
            {category.description && (
              <p className="text-gray-600 mt-2">
                {category.description}
              </p>
            )}
            {!category.description && (
              <p className="text-gray-600 mt-2">
                Explora los autos disponibles en esta categoría.
              </p>
            )}
          </div>
        </header>

        {/* Sección de autos */}
        <section>
          <h2 className="text-lg sm:text-xl font-semibold text-neutral-dark mb-4">
            Autos en {category.name}
          </h2>
          {categoryCars.length === 0 ? (
            <p className="text-gray-600">No hay autos disponibles en esta categoría.</p>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {categoryCars.map((car) => (
                <CarCard 
                  key={car.id} 
                  car={car} 
                  onDetail={(c) => setSelectedCar(c)}
                />
              ))}
            </div>
          )}
        </section>
      </div>

      {/* Modal de detalle del auto */}
      {selectedCar && (
        <ModalCarDetail
          car={selectedCar}
          onClose={() => setSelectedCar(null)}
        />
      )}
    </div>
  );
}
