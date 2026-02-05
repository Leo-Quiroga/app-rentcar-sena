// Página principal - Home
import { useEffect, useState } from "react";
import SearchBar from "../components/SearchBar";
import CategoryCard from "../components/CategoryCard";
import CarCard from "../components/CarCard";
import { getCategories } from "../api/categoriesApi";
import { getCars } from "../api/carsApi";
import { searchAvailableCars } from "../api/searchApi";
import { getBranches } from "../api/branchesApi";
import { getUsers } from "../api/adminUsersApi";
import { useAuth } from "../auth/AuthContext";
// Modales
import ModalCarDetail from "../components/ModalCarDetail";
import ModalConfirmarReserva from "../components/ModalConfirmarReserva";
import ModalReservaConfirmada from "../components/ModalReservaConfirmada";
// Componente principal de la página de inicio
export default function Home() {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  
  const [filters, setFilters] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [categories, setCategories] = useState([]);
  const [cars, setCars] = useState([]);
  const [branches, setBranches] = useState([]);
  const [users, setUsers] = useState([]);
  const [selectedPickupBranch, setSelectedPickupBranch] = useState("");
  const [selectedDropoffBranch, setSelectedDropoffBranch] = useState("");
  const [selectedClient, setSelectedClient] = useState("");
  const [clientFilter, setClientFilter] = useState("");
  const [loading, setLoading] = useState(true);

  // Modales
  const [selectedCar, setSelectedCar] = useState(null);
  const [carToReserve, setCarToReserve] = useState(null); // auto para confirmar
  const [reservaConfirmada, setReservaConfirmada] = useState(null);

  // Cargar datos iniciales
  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const [categoriesData, carsData, branchesData] = await Promise.all([
          getCategories(),
          getCars({ size: 10 }),
          getBranches()
        ]);
        setCategories(categoriesData);
        setCars(carsData);
        setBranches(branchesData);
        
        // Cargar usuarios si es admin
        if (user?.role === 'ADMIN') {
          try {
            const usersData = await getUsers();
            setUsers(usersData.content || usersData.users || []);
          } catch (error) {
            console.error('Error cargando usuarios:', error);
          }
        }
      } catch (error) {
        console.error('Error cargando datos:', error);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [user]);

  // Buscar autos disponibles cuando hay filtros, sino mostrar autos aleatorios
  useEffect(() => {
    if (filters?.startDate && filters?.endDate) {
      const searchCars = async () => {
        try {
          setLoading(true);
          const availableCars = await searchAvailableCars(
            filters.startDate,
            filters.endDate,
            selectedCategory?.id
          );
          setCars(availableCars);
        } catch (error) {
          console.error('Error buscando autos disponibles:', error);
        } finally {
          setLoading(false);
        }
      };
      searchCars();
    }
  }, [filters?.startDate, filters?.endDate, selectedCategory]);

  // Cargar autos por categoría o aleatorios cuando no hay filtros de fecha
  useEffect(() => {
    if (!filters?.startDate || !filters?.endDate) {
      const loadCars = async () => {
        try {
          setLoading(true);
          const carsData = selectedCategory 
            ? await getCars({ categoryId: selectedCategory.id, size: 10 })
            : await getCars({ size: 10 });
          setCars(carsData);
        } catch (error) {
          console.error('Error cargando autos:', error);
        } finally {
          setLoading(false);
        }
      };
      loadCars();
    }
  }, [selectedCategory, filters?.startDate, filters?.endDate]);

  // Confirmación de la reserva (pasa del modal confirmar → modal confirmada)
  const handleConfirmReservation = ({ reservation, car, filters, dias, total }) => {
    setCarToReserve(null);          // cierra modal confirmar
    setReservaConfirmada({ reservation, car, filters, dias, total }); // abre modal reserva confirmada
  };

  if (loading) {
    return (
      <div className="bg-neutral-light min-h-screen">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
          <div className="text-center py-12">
            <p className="text-gray-600">Cargando...</p>
          </div>
        </div>
      </div>
    );
  }

 // Renderizar página de inicio
  return (
    <div className="bg-neutral-light min-h-screen">
      <div className="space-y-12 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        
        {/* Hero + SearchBar */}
        <section>
          <SearchBar onSearch={(f) => setFilters(f)} />
          {filters && (
            <div className="mt-4 space-y-3">
              <div className="text-xs sm:text-sm text-gray-700 bg-white p-3 rounded shadow-sm max-w-3xl mx-auto">
                <strong>Búsqueda:</strong>{" "}
                {filters.pickupCity} → {filters.dropoffCity || filters.pickupCity} |{" "}
                {filters.startDate || "Fecha inicio no seleccionada"} →{" "}
                {filters.endDate || "Fecha fin no seleccionada"}
              </div>
              
              {/* Información de sedes y cliente seleccionados */}
              {(selectedPickupBranch || selectedDropoffBranch || (isAdmin && selectedClient)) && (
                <div className="text-xs sm:text-sm text-gray-700 bg-white p-3 rounded shadow-sm max-w-3xl mx-auto">
                  <strong>Selección:</strong>{" "}
                  {selectedPickupBranch && (
                    <span>Retiro: {branches.find(b => b.id == selectedPickupBranch)?.name}</span>
                  )}
                  {selectedPickupBranch && selectedDropoffBranch && " | "}
                  {selectedDropoffBranch && (
                    <span>Entrega: {branches.find(b => b.id == selectedDropoffBranch)?.name}</span>
                  )}
                  {(selectedPickupBranch || selectedDropoffBranch) && isAdmin && selectedClient && " | "}
                  {isAdmin && selectedClient && (
                    <span>Cliente: {users.find(u => u.id == selectedClient)?.firstName} {users.find(u => u.id == selectedClient)?.lastName}</span>
                  )}
                </div>
              )}
              
              {/* Selector de sedes */}
              <div className="bg-white p-4 rounded shadow-sm max-w-3xl mx-auto">
                <h3 className="text-sm font-medium text-gray-700 mb-3">Selecciona las sedes:</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-medium text-gray-600 mb-1">Sede de Retiro</label>
                    <select
                      value={selectedPickupBranch}
                      onChange={(e) => setSelectedPickupBranch(e.target.value)}
                      className="w-full text-sm border border-gray-300 rounded px-3 py-2 focus:ring-primary focus:border-primary"
                    >
                      <option value="">Selecciona sede de retiro</option>
                      {branches.filter(b => b.city === filters.pickupCity).map((branch) => (
                        <option key={branch.id} value={branch.id}>
                          {branch.name} - {branch.address}
                        </option>
                      ))}
                    </select>
                  </div>
                  
                  <div>
                    <label className="block text-xs font-medium text-gray-600 mb-1">Sede de Entrega</label>
                    <select
                      value={selectedDropoffBranch}
                      onChange={(e) => setSelectedDropoffBranch(e.target.value)}
                      className="w-full text-sm border border-gray-300 rounded px-3 py-2 focus:ring-primary focus:border-primary"
                    >
                      <option value="">Selecciona sede de entrega</option>
                      {branches.filter(b => b.city === (filters.dropoffCity || filters.pickupCity)).map((branch) => (
                        <option key={branch.id} value={branch.id}>
                          {branch.name} - {branch.address}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
                
                {/* Selector de cliente (solo admin) */}
                {isAdmin && (
                  <div className="mt-4">
                    <h3 className="text-sm font-medium text-gray-700 mb-3">Selecciona el cliente:</h3>
                    <div className="relative">
                      <input
                        type="text"
                        placeholder="Buscar por nombre, email, ID o licencia..."
                        value={clientFilter}
                        onChange={(e) => {
                          setClientFilter(e.target.value);
                          setSelectedClient("");
                        }}
                        className="w-full text-sm border border-gray-300 rounded px-3 py-2 focus:ring-primary focus:border-primary"
                      />
                      
                      {/* Sugerencias dinámicas */}
                      {clientFilter && !selectedClient && (
                        <div className="absolute z-10 w-full bg-white border border-gray-300 rounded-b-lg shadow-lg max-h-40 overflow-y-auto">
                          {users
                            .filter(u => u.role === 'CLIENT' && (
                              `${u.firstName} ${u.lastName}`.toLowerCase().includes(clientFilter.toLowerCase()) ||
                              u.email.toLowerCase().includes(clientFilter.toLowerCase()) ||
                              u.id.toString().includes(clientFilter)
                            ))
                            .slice(0, 5)
                            .map((user) => (
                              <div
                                key={user.id}
                                onClick={() => {
                                  setSelectedClient(user.id);
                                  setClientFilter(`${user.firstName} ${user.lastName} - ${user.email}`);
                                }}
                                className="px-3 py-2 hover:bg-gray-100 cursor-pointer border-b border-gray-100 last:border-b-0"
                              >
                                <div className="text-sm font-medium text-gray-900">
                                  {user.firstName} {user.lastName}
                                </div>
                                <div className="text-xs text-gray-500">
                                  #{user.id} - {user.email}
                                </div>
                              </div>
                            ))
                          }
                          {users.filter(u => u.role === 'CLIENT' && (
                            `${u.firstName} ${u.lastName}`.toLowerCase().includes(clientFilter.toLowerCase()) ||
                            u.email.toLowerCase().includes(clientFilter.toLowerCase()) ||
                            u.id.toString().includes(clientFilter)
                          )).length === 0 && (
                            <div className="px-3 py-2 text-sm text-gray-500 italic">
                              No se encontraron clientes
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                )}
              </div>
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
            {cars.map((car) => (
              <CarCard
                key={car.id}
                car={car}
                onDetail={(c) => setSelectedCar(c)}
                onReserve={(c) => {
                  if (!filters?.startDate || !filters?.endDate) {
                    alert("Selecciona fecha de inicio y fin antes de reservar.");
                    return;
                  }
                  if (!selectedPickupBranch || !selectedDropoffBranch) {
                    alert("Selecciona las sedes de retiro y entrega antes de reservar.");
                    return;
                  }
                  if (isAdmin && !selectedClient) {
                    alert("Selecciona el cliente para la reserva.");
                    return;
                  }
                  setCarToReserve(c);
                }}
                canReserve={Boolean(filters?.startDate && filters?.endDate && selectedPickupBranch && selectedDropoffBranch)}
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
          filters={{
            ...filters,
            selectedPickupBranch,
            selectedDropoffBranch,
            selectedClient
          }}
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
