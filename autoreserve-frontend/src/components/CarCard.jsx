// Componente para mostrar una tarjeta de auto
export default function CarCard({ car, onDetail, onReserve, canReserve = false }) {
  const price = car?.pricePerDay ?? car?.price ?? null;
  const rating = car?.rating ?? 0;

  // Generar estrellas dinámicas (máx 5)
  const stars = Array.from({ length: 5 }, (_, i) => (
    <span key={i} className={i < rating ? "text-yellow-400" : "text-gray-300"}>
      ★
    </span>
  ));

  return (
    <article className="bg-white shadow-md rounded-lg overflow-hidden flex flex-col transition hover:shadow-lg">
      {/* Imagen */}
      <img
        src={car?.image || "/src/assets/placeholder-car.jpg"}
        alt={car?.name ? `Imagen del auto ${car.name}` : "Auto disponible"}
        className="h-40 w-full object-cover"
      />

      {/* Contenido */}
      <div className="p-4 flex-1 flex flex-col justify-between">
        <div>
          <h3 className="text-base sm:text-lg font-semibold text-neutral-dark">
            {car?.name || "Auto sin nombre"}
          </h3>
          <p className="text-sm text-gray-500">{car?.category || "Sin categoría"}</p>

          {/* Rating */}
          <div className="flex items-center text-sm mt-1">
            {stars}
            <span className="ml-2 text-gray-600">{rating} / 5</span>
          </div>

          {/* Precio */}
          <p className="text-sm font-semibold text-neutral-dark mt-2">
            {price ? `$${Number(price).toLocaleString()} / día` : "Precio no disponible"}
          </p>
        </div>

        {/* Botones */}
        <div className="mt-4 flex gap-2">
          <button
            onClick={() => onDetail && onDetail(car)}
            aria-label={`Ver más detalles de ${car?.name}`}
            className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-800 text-xs sm:text-sm py-2 px-3 rounded-md transition"
          >
            Ver más
          </button>

          <button
            onClick={() => canReserve && onReserve && onReserve(car)}
            disabled={!canReserve}
            aria-label={`Reservar ${car?.name}`}
            className={`flex-1 text-xs sm:text-sm py-2 px-3 rounded-md transition ${
              canReserve
                ? "bg-primary hover:bg-primary-dark text-white"
                : "bg-gray-300 text-gray-500 cursor-not-allowed"
            }`}
          >
            Reservar
          </button>
        </div>
      </div>
    </article>
  );
}
