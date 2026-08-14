// Pantalla de favoritos del usuario - Refactorizada para modelos
import { useState, useEffect } from "react";
import { useAuth } from "../auth/useAuth";
import { useFavorites } from "../utils/useFavorites";
import SearchBar from "../components/SearchBar";
import CarCard from "../components/CarCard";
import FavoriteButton from "../components/FavoriteButton";
import { getMyFavorites } from "../api/favoritesApi";
// Modales
import ModalCarDetail from "../components/ModalCarDetail";
import ModalConfirmarReserva from "../components/ModalConfirmarReserva";
import ModalReservaConfirmada from "../components/ModalReservaConfirmada";

// Componente principal de la página de favoritos
export default function Favorites() {
  const [filters, setFilters] = useState(null);
  const [favoriteModels, setFavoriteModels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { user } = useAuth();
  const { favoriteIdsSet } = useFavorites();

  // Modales
  const [selectedCar, setSelectedCar] = useState(null);
  const [carToReserve, setCarToReserve] = useState(null);
  const [reservaConfirmada, setReservaConfirmada] = useState(null);

  // Carga inicial de favoritos desde la API
  useEffect(() => {
    const loadFavorites = async () => {
      if (!user) {
        setError("Debes iniciar sesión para ver tus favoritos");
        setLoading(false);
        return;
      }
      try {
        setLoading(true);
        setError(null);
        const response = await getMyFavorites();
        if (Array.isArray(response)) {
          setFavoriteModels(response);
        } else if (response?.success) {
          setFavoriteModels(response.data || []);
        } else {
          setFavoriteModels([]);
        }
      } catch (err) {
        setError(err.message || 'Error de conexión al cargar favoritos');
        setFavoriteModels([]);
      } finally {
        setLoading(false);
      }
    };
    loadFavorites();
  }, [user]);

  // Sincronizar la grilla cuando el contexto quita un favorito
  useEffect(() => {
    if (!loading) {
      setFavoriteModels(prev => prev.filter(m => favoriteIdsSet.has(m.carModelId)));
    }
  }, [favoriteIdsSet]);

  // Confirmación de la reserva
  const handleConfirmReservation = ({ car, filters, dias, total }) => {
    const newReserva = {
      id: Date.now().toString(),
      car,
      filters,
      dias,
      total,
      createdAt: new Date().toISOString(),
    };
    setCarToReserve(null);
    setReservaConfirmada(newReserva);
  };

  // Si no hay usuario, mostrar mensaje de login
  if (!user) {
    return (
      <div className="bg-neutral-light min-h-screen">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
          <div className="text-center py-16">
            <div className="text-6xl mb-4">🔒</div>
            <h2 className="text-2xl font-bold text-gray-700 mb-4">
              Acceso Restringido
            </h2>
            <p className="text-gray-600 mb-6">
              Debes iniciar sesión para ver tus modelos favoritos
            </p>
            <a 
              href="/login" 
              className="bg-primary text-white px-6 py-3 rounded-lg hover:bg-primary-dark transition-colors"
            >
              Iniciar Sesión
            </a>
          </div>
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="bg-neutral-light min-h-screen">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
            <p className="text-gray-600">Cargando tus modelos favoritos...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-neutral-light min-h-screen">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
          <div className="text-center py-12">
            <div className="text-red-500 text-6xl mb-4">⚠️</div>
            <p className="text-red-600 text-lg mb-4">Error: {error}</p>
            <div className="space-x-4">
              <button 
                onClick={() => window.location.reload()}
                className="bg-primary text-white px-4 py-2 rounded hover:bg-primary-dark"
              >
                Reintentar
              </button>
              <a 
                href="/"
                className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
              >
                Volver al Inicio
              </a>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-neutral-light min-h-screen">
      <div className="space-y-12 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        
        {/* Header con estadísticas */}
        <section>
          <div className="flex items-center justify-between mb-4">
            <h1 className="text-xl sm:text-2xl font-bold text-neutral-dark">
              Tus modelos favoritos
            </h1>
            <div className="bg-white px-4 py-2 rounded-lg shadow-sm">
              <span className="text-sm text-gray-600">
                {favoriteModels.length} modelo{favoriteModels.length !== 1 ? 's' : ''} favorito{favoriteModels.length !== 1 ? 's' : ''}
              </span>
            </div>
          </div>
          
          <SearchBar onSearch={(f) => setFilters(f)} />
          
          {filters && (
            <div className="mt-4 text-xs sm:text-sm text-gray-700 bg-white p-3 rounded shadow-sm max-w-3xl mx-auto">
              <strong>Búsqueda:</strong>{" "}
              {filters.pickupCity} → {filters.dropoffCity || "—"} |{" "}
              {filters.startDate || "Fecha inicio no seleccionada"} →{" "}
              {filters.endDate || "Fecha fin no seleccionada"}
            </div>
          )}
        </section>

        {/* Lista de modelos favoritos */}
        <section>
          {favoriteModels.length > 0 ? (
            <>
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-lg sm:text-xl font-semibold text-neutral-dark">
                  {filters
                    ? "Modelos disponibles para tus fechas"
                    : "Agrega fechas para verificar disponibilidad"}
                </h2>
              </div>
              
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {favoriteModels.map((model) => {
                  // Convertir FavoriteResponse a formato CarCard
                  const carData = {
                    id: model.carModelId,
                    brand: model.brand,
                    model: model.model,
                    year: model.year,
                    pricePerDay: model.pricePerDay,
                    image: model.image,
                    category: model.categoryName,
                    availableUnits: model.availableUnits,
                    totalUnits: model.totalUnits,
                    isFavorite: true
                  };
                  
                  return (
                    <div key={model.favoriteId} className="relative">
                      {/* Badge de favorito con botón para quitar */}
                      <div className="absolute top-2 left-2 z-10 flex items-center gap-2">
                        <div className="bg-yellow-400 text-yellow-900 px-2 py-1 rounded-full text-xs font-semibold">
                          Favorito
                        </div>
                        <FavoriteButton 
                          carModelId={model.carModelId}
                          size="sm"
                          className="bg-white shadow-md"
                        />
                      </div>
                      
                      {/* Información de disponibilidad */}
                      <div className="absolute top-2 right-2 z-10 bg-white bg-opacity-90 px-2 py-1 rounded text-xs">
                        {model.availableUnits}/{model.totalUnits} disponibles
                      </div>
                      
                      <CarCard
                        car={carData}
                        onDetail={(c) => setSelectedCar(c)}
                        onReserve={(c) => {
                          if (!filters?.startDate || !filters?.endDate) {
                            alert("Selecciona fecha de inicio y fin antes de reservar.");
                            return;
                          }
                          if (model.availableUnits === 0) {
                            alert("No hay unidades disponibles de este modelo.");
                            return;
                          }
                          setCarToReserve(c);
                        }}
                        canReserve={Boolean(filters?.startDate && filters?.endDate && model.availableUnits > 0)}
                        showFavoriteButton={true}
                      />
                    </div>
                  );
                })}
              </div>
            </>
          ) : (
            <div className="text-center py-16">
              <div className="text-6xl mb-4">🚗💔</div>
              <h3 className="text-xl font-semibold text-gray-700 mb-2">
                No tienes modelos favoritos todavía
              </h3>
              <p className="text-gray-600 mb-6">
                Explora nuestro catálogo y marca tus modelos preferidos con la estrella ⭐
              </p>
              <a 
                href="/categorias" 
                className="bg-primary text-white px-6 py-3 rounded-lg hover:bg-primary-dark transition-colors"
              >
                Explorar modelos
              </a>
            </div>
          )}
        </section>

        {/* Modales */}
        {selectedCar && (
          <ModalCarDetail
            car={selectedCar}
            filters={filters}
            onClose={() => setSelectedCar(null)}
            onReserve={(car) => {
              setCarToReserve(car);
              setSelectedCar(null);
            }}
          />
        )}

        <ModalConfirmarReserva
          car={carToReserve}
          filters={filters}
          onClose={() => setCarToReserve(null)}
          onConfirm={handleConfirmReservation}
        />

        <ModalReservaConfirmada
          reserva={reservaConfirmada}
          onClose={() => setReservaConfirmada(null)}
        />
      </div>
    </div>
  );
}
