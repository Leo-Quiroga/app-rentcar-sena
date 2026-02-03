// Pantalla de favoritos del usuario
import { useState, useEffect } from "react";
import SearchBar from "../components/SearchBar";
import CarCard from "../components/CarCard";
import { getMyFavorites } from "../api/favoritesApi";
// Modales
import ModalCarDetail from "../components/ModalCarDetail";
import ModalConfirmarReserva from "../components/ModalConfirmarReserva";
import ModalReservaConfirmada from "../components/ModalReservaConfirmada";

// Componente principal de la página de favoritos
export default function Favorites() {
  const [filters, setFilters] = useState(null);
  const [favoriteCars, setFavoriteCars] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Modales
  const [selectedCar, setSelectedCar] = useState(null);
  const [carToReserve, setCarToReserve] = useState(null);
  const [reservaConfirmada, setReservaConfirmada] = useState(null);

  // Cargar favoritos
  useEffect(() => {
    const loadFavorites = async () => {
      try {
        setLoading(true);
        const data = await getMyFavorites();
        setFavoriteCars(data);
        setError(null);
      } catch (err) {
        setError(err.message);
        console.error('Error cargando favoritos:', err);
      } finally {
        setLoading(false);
      }
    };

    loadFavorites();
  }, []);

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

  if (loading) {
    return (
      <div className="bg-neutral-light min-h-screen">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
          <div className="text-center py-12">
            <p className="text-gray-600">Cargando favoritos...</p>
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
            <p className="text-red-600">Error: {error}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-neutral-light min-h-screen">
      <div className="space-y-12 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        
        {/* SearchBar */}
        <section>
          <h1 className="text-xl sm:text-2xl font-bold text-neutral-dark mb-4">
            ⭐ Tus autos favoritos
          </h1>
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

        {/* Lista de favoritos */}
        <section>
          {favoriteCars.length > 0 ? (
            <>
              <h2 className="text-lg sm:text-xl font-semibold mb-6 text-neutral-dark">
                {filters
                  ? "Autos disponibles para tus fechas"
                  : "Agrega fechas para reservar tus favoritos"}
              </h2>
              <div className="grid grid-cols-2 gap-6">
                {favoriteCars.map((car) => (
                  <CarCard
                    key={car.id}
                    car={car}
                    onDetail={(c) => setSelectedCar(c)}
                    onReserve={(c) => {
                      if (!filters?.startDate || !filters?.endDate) {
                        alert("Selecciona fecha de inicio y fin antes de reservar.");
                        return;
                      }
                      setCarToReserve(c);
                    }}
                    canReserve={Boolean(filters?.startDate && filters?.endDate)}
                  />
                ))}
              </div>
            </>
          ) : (
            <p className="text-gray-600 text-center py-12">
              No tienes autos en favoritos todavía.
            </p>
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
