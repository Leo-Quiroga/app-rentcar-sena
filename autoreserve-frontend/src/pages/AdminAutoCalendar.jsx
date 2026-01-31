// Pantalla de calendario para que el administrador gestione la disponibilidad de un auto
import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { mockAdminAutos } from "../data/mockAdminAutos";

export default function AdminAutoCalendar() {
  const { id } = useParams();
  const navigate = useNavigate();

  // Buscar auto por ID
  const auto = mockAdminAutos.find((a) => a.id === id);
  // Estado para las fechas no disponibles (mock temporal)
  const [unavailableDates, setUnavailableDates] = useState([
    "2025-09-20",
    "2025-09-25",
  ]); 
 // Si no se encuentra el auto, mostrar mensaje de error
  if (!auto) {
    return (
      <div className="text-center py-12">
        <h2 className="text-2xl font-bold">Auto no encontrado</h2>
        <button
          onClick={() => navigate("/admin/autos")}
          className="mt-4 px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
        >
          Volver
        </button>
      </div>
    );
  }

  // Manejar selección/deselección de fechas
  const handleToggleDate = (date) => {
    if (unavailableDates.includes(date)) {
      setUnavailableDates(unavailableDates.filter((d) => d !== date));
    } else {
      setUnavailableDates([...unavailableDates, date]);
    }
  };

  //  Generar días del mes actual
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth();

  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const daysArray = Array.from({ length: daysInMonth }, (_, i) => {
    const date = new Date(year, month, i + 1);
    return date.toISOString().split("T")[0]; // formato YYYY-MM-DD
  });
 // Renderizar calendario y controles
  return (
    <div className="max-w-4xl mx-auto bg-white shadow rounded-lg p-6 mt-10">
      <h1 className="text-2xl font-bold mb-6 text-neutral-dark">
        📅 Disponibilidad de {auto.brand} {auto.model}
      </h1>

      <div className="grid grid-cols-7 gap-2 text-center">
        {daysArray.map((date) => {
          const isUnavailable = unavailableDates.includes(date);
          return (
            <button
              key={date}
              onClick={() => handleToggleDate(date)}
              className={`p-3 rounded-lg border text-sm ${
                isUnavailable
                  ? "bg-red-500 text-white border-red-600"
                  : "bg-green-100 text-green-800 border-green-300"
              } hover:opacity-80 transition`}
            >
              {new Date(date).getDate()}
            </button>
          );
        })}
      </div>

      {/* Botones */}
      <div className="flex justify-end gap-4 mt-6">
        <button
          onClick={() => navigate("/admin/autos")}
          className="px-4 py-2 bg-gray-300 rounded-lg hover:bg-gray-400 transition"
        >
          Cancelar
        </button>
        <button
          onClick={() => {
            console.log("Guardar disponibilidad:", auto.id, unavailableDates);
            alert("Disponibilidad guardada correctamente");
            navigate("/admin/autos");
          }}
          className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
        >
          Guardar
        </button>
      </div>
    </div>
  );
}
