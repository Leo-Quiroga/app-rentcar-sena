// Página principal - Home
import { useEffect, useState } from "react";
import SearchBar from "../components/SearchBar";
import CategoryCard from "../components/CategoryCard";
import CarCard from "../components/CarCard";
import { categories, cars } from "../data/mockData";
// Modales
import ModalCarDetail from "../components/ModalCarDetail";
import ModalConfirmarReserva from "../components/ModalConfirmarReserva";
import ModalReservaConfirmada from "../components/ModalReservaConfirmada";
// Componente principal de la página de inicio
export default function Home() {
  const [filters, setFilters] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState(null);

  // Modales
  const [selectedCar, setSelectedCar] = useState(null);
  const [carToReserve, setCarToReserve] = useState(null); // auto para confirmar
  const [reservaConfirmada, setReservaConfirmada] = useState(null);

  const [randomCars, setRandomCars] = useState([]);

  // Generar 10 autos aleatorios
  useEffect(() => {
    const shuffled = [...cars].sort(() => 0.5 - Math.random());
    setRandomCars(shuffled.slice(0, 10));
  }, []);

  // Si hay categoría seleccionada, mostramos autos de esa categoría en lugar de los aleatorios
  const displayCars = selectedCategory
    ? cars.filter((c) => c.category === selectedCategory.name).slice(0, 10)
    : randomCars;

  // Confirmación de la reserva (pasa del modal confirmar → modal confirmada)
  const handleConfirmReservation = ({ car, filters, dias, total }) => {
    const newReserva = {
      id: Date.now().toString(),
      car,
      filters,
      dias,
      total,
      createdAt: new Date().toISOString(),
    };
    setCarToReserve(null);          // cierra modal confirmar
    setReservaConfirmada(newReserva); // abre modal reserva confirmada
  };
 // Renderizar página de inicio
  return (
    <div className="bg-neutral-light min-h-screen">
      <div className="space-y-12 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        
        {/* Hero + SearchBar */}
        <section>
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

        {/* Categorías */}
        <section>
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-lg sm:text-xl font-semibold text-neutral-dark">
              Explora por categoría
            </h2>
            {selectedCategory && (
              <button
                onClick={() => setSelectedCategory(null)}
                className="text-xs sm:text-sm text-gray-600 hover:underline"
              >
                Limpiar selección
              </button>
            )}
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-6">
            {categories.map((category) => (
              <CategoryCard
                key={category.id}
                category={category}
                onSelect={(cat) => setSelectedCategory(cat)}
              />
            ))}
          </div>
        </section>

        {/* Recomendados */}
        <section>
          <h2 className="text-lg sm:text-xl font-semibold mb-6 text-neutral-dark">
            {selectedCategory
              ? `Autos en ${selectedCategory.name}`
              : "Recomendados para ti"}
          </h2>

          <div className="grid grid-cols-2 gap-6">
            {displayCars.map((car) => (
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
