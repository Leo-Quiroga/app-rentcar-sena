// Componente SearchBar con autocompletado para ciudades
import { useState, useEffect } from "react";
import { getCitiesWithBranches } from "../api/searchApi";

export default function SearchBar({ onSearch }) {
  const [pickupCity, setPickupCity] = useState("");
  const [dropoffCity, setDropoffCity] = useState("");
  const [sameCity, setSameCity] = useState(true);
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [cities, setCities] = useState([]);

  // Cargar ciudades con sedes activas
  useEffect(() => {
    const loadCities = async () => {
      try {
        const activeCities = await getCitiesWithBranches();
        setCities(activeCities);
      } catch (error) {
        console.error('Error cargando ciudades:', error);
      }
    };
    loadCities();
  }, []);

  // Estados para sugerencias
  const [suggestionsPickup, setSuggestionsPickup] = useState([]);
  const [activePickupIndex, setActivePickupIndex] = useState(-1);

  const [suggestionsDropoff, setSuggestionsDropoff] = useState([]);
  const [activeDropoffIndex, setActiveDropoffIndex] = useState(-1);

  // 🔹 Filtra ciudades en base al input
  const filterCities = (input) =>
    cities.filter((city) =>
      city.toLowerCase().includes(input.toLowerCase())
    );

  // ---- Pickup City ----
  const handleChangePickup = (e) => {
    const value = e.target.value;
    setPickupCity(value);
    if (value) {
      setSuggestionsPickup(filterCities(value));
      setActivePickupIndex(-1);
    } else {
      setSuggestionsPickup([]);
    }
  };

  const handleKeyDownPickup = (e) => {
    if (!suggestionsPickup.length) return;
    if (e.key === "ArrowDown") {
      e.preventDefault();
      setActivePickupIndex((prev) => (prev + 1) % suggestionsPickup.length);
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setActivePickupIndex((prev) =>
        prev <= 0 ? suggestionsPickup.length - 1 : prev - 1
      );
    } else if (e.key === "Enter" && activePickupIndex >= 0) {
      e.preventDefault();
      setPickupCity(suggestionsPickup[activePickupIndex]);
      setSuggestionsPickup([]);
    } else if (e.key === "Escape") {
      setSuggestionsPickup([]);
      setActivePickupIndex(-1);
    }
  };

  // ---- Dropoff City ----
  const handleChangeDropoff = (e) => {
    const value = e.target.value;
    setDropoffCity(value);
    if (value) {
      setSuggestionsDropoff(filterCities(value));
      setActiveDropoffIndex(-1);
    } else {
      setSuggestionsDropoff([]);
    }
  };

  const handleKeyDownDropoff = (e) => {
    if (!suggestionsDropoff.length) return;
    if (e.key === "ArrowDown") {
      e.preventDefault();
      setActiveDropoffIndex((prev) => (prev + 1) % suggestionsDropoff.length);
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setActiveDropoffIndex((prev) =>
        prev <= 0 ? suggestionsDropoff.length - 1 : prev - 1
      );
    } else if (e.key === "Enter" && activeDropoffIndex >= 0) {
      e.preventDefault();
      setDropoffCity(suggestionsDropoff[activeDropoffIndex]);
      setSuggestionsDropoff([]);
    } else if (e.key === "Escape") {
      setSuggestionsDropoff([]);
      setActiveDropoffIndex(-1);
    }
  };

  const handleSameCity = (e) => {
    setSameCity(e.target.checked);
    if (e.target.checked) setDropoffCity("");
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSearch({
      pickupCity,
      dropoffCity: sameCity ? pickupCity : dropoffCity,
      startDate,
      endDate,
    });
  };

  return (
    <section className="bg-white shadow rounded-lg p-6 space-y-4">
      {/* Título + párrafo informativo */}
      <header>
        <h2 className="text-lg sm:text-xl font-semibold text-gray-800 text-center">
          Encuentra el auto ideal
        </h2>
        <p className="text-sm text-gray-500 text-center mt-1">
          Selecciona ciudad, fecha de retiro y devolución para comenzar tu búsqueda
        </p>
      </header>

      {/* Formulario */}
      <form
        onSubmit={handleSubmit}
        className="flex flex-col md:flex-row gap-3 items-center"
      >
        {/* Ciudad de retiro */}
        <div className="relative flex-1">
          <select
            value={pickupCity}
            onChange={(e) => setPickupCity(e.target.value)}
            className="w-full border border-gray-300 rounded px-3 py-2 appearance-none bg-white"
            required
          >
            <option value="">Selecciona ciudad de retiro</option>
            {cities.map((city) => (
              <option key={city} value={city}>
                {city}
              </option>
            ))}
          </select>
        </div>

        {/* Checkbox entrega misma ciudad */}
        <div className="flex items-center gap-2">
          <input
            type="checkbox"
            id="sameCity"
            checked={sameCity}
            onChange={handleSameCity}
            className="h-4 w-4"
          />
          <label htmlFor="sameCity" className="text-sm text-gray-600">
            Entrega en misma ciudad
          </label>
        </div>

        {/* Ciudad de entrega */}
        {!sameCity && (
          <div className="relative flex-1">
            <select
              value={dropoffCity}
              onChange={(e) => setDropoffCity(e.target.value)}
              className="w-full border border-gray-300 rounded px-3 py-2 appearance-none bg-white"
              required
            >
              <option value="">Selecciona ciudad de entrega</option>
              {cities.map((city) => (
                <option key={city} value={city}>
                  {city}
                </option>
              ))}
            </select>
          </div>
        )}

        {/* Fechas */}
        <input
          type="date"
          value={startDate}
          min={new Date().toISOString().split('T')[0]}
          onChange={(e) => setStartDate(e.target.value)}
          className="border border-gray-300 rounded px-3 py-2"
          required
        />

        <input
          type="date"
          value={endDate}
          min={startDate || new Date().toISOString().split('T')[0]}
          onChange={(e) => setEndDate(e.target.value)}
          className="border border-gray-300 rounded px-3 py-2"
          required
        />

        {/* Botón */}
        <button
          type="submit"
          className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
        >
          Realizar búsqueda
        </button>
      </form>
    </section>
  );
}
