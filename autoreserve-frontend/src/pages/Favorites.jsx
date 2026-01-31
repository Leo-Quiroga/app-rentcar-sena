// Pantalla de favoritos del usuario
import { useState } from "react";
import SearchBar from "../components/SearchBar";
import CarCard from "../components/CarCard";
// Modales
import ModalCarDetail from "../components/ModalCarDetail";
import ModalConfirmarReserva from "../components/ModalConfirmarReserva";
import ModalReservaConfirmada from "../components/ModalReservaConfirmada";

// Simulación de favoritos (luego se integrará con la lógica real)
import { cars } from "../data/mockData";
const mockFavorites = cars.slice(0, 3); // solo para probar, 3 autos favoritos
// Componente principal de la página de favoritos
export default function Favorites() {
  const [filters, setFilters] = useState(null);

  // Modales
  const [selectedCar, setSelectedCar] = useState(null);
  const [carToReserve, setCarToReserve] = useState(null);
  const [reservaConfirmada, setReservaConfirmada] = useState(null);

  // Lista de favoritos (luego esto vendrá de un contexto/estado global)
  const favoriteCars = mockFavorites;

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
