// Componente para mostrar una tarjeta de auto
import { getImageUrl } from '../utils/imageUtils';

export default function CarCard({ car, onDetail, onReserve, canReserve = false }) {
  const price = car?.pricePerDay ?? car?.price ?? null;
  const rating = car?.rating ?? 0;
  
  // Manejar diferentes estructuras de datos
  const carName = car?.name || (car?.brand && car?.model ? `${car.brand} ${car.model}` : "Auto sin nombre");
  const carCategory = car?.category || car?.categoryName || "Sin categoría";

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
        src={getImageUrl(car?.image, 'car')}
        alt={carName ? `Imagen del auto ${carName}` : "Auto disponible"}
        className="h-40 w-full object-cover"
        onError={(e) => {
          e.target.src = getImageUrl(null, 'car');
        }}
      />

      {/* Contenido */}
      <div className="p-4 flex-1 flex flex-col justify-between">
        <div>
          <h3 className="text-base sm:text-lg font-semibold text-neutral-dark">
            {carName}
          </h3>
          <p className="text-sm text-gray-500">{carCategory}</p>

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
            aria-label={`Ver más detalles de ${carName}`}
            className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-800 text-xs sm:text-sm py-2 px-3 rounded-md transition"
          >
            Ver más
          </button>

          <button
            onClick={() => canReserve && onReserve && onReserve(car)}
            disabled={!canReserve}
            aria-label={`Reservar ${carName}`}
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
