// Componente para mostrar una tarjeta de modelo de vehículo
import { getImageUrl } from '../utils/imageUtils';
import FavoriteButton from './FavoriteButton';
import { useFavorites } from '../utils/useFavorites';

export default function CarCard({ 
  car, 
  onDetail, 
  onReserve, 
  canReserve = false, 
  showFavoriteButton = true,
  onFavoriteChange 
}) {
  const price = car?.pricePerDay ?? null;
  const carName = car?.name || (car?.brand && car?.model ? `${car.brand} ${car.model}` : "Vehículo");
  const carCategory = car?.category || car?.categoryName || "Sin categoría";
  const availableUnits = car?.availableUnits ?? null;
  const isAvailable = availableUnits === null || availableUnits > 0;
  
  // Hook de favoritos para verificar estado
  const { isFavorite, updateFavorite } = useFavorites();
  
  // ID del modelo (puede venir como id o carModelId)
  const carModelId = car?.carModelId || car?.id;
  const isCurrentlyFavorite = car?.isFavorite !== undefined ? car.isFavorite : isFavorite(carModelId);

  const handleFavoriteToggle = (newFavoriteState, modelId) => {
    // Actualizar estado local del hook
    updateFavorite(modelId, newFavoriteState);
    
    // Notificar al componente padre si existe callback
    if (onFavoriteChange) {
      onFavoriteChange(newFavoriteState, modelId);
    }
  };

  return (
    <article className="bg-white shadow-md rounded-lg overflow-hidden flex flex-col transition hover:shadow-lg">
      {/* Imagen con badge de disponibilidad y botón de favorito */}
      <div className="relative">
        <img
          src={getImageUrl(car?.image, 'car')}
          alt={`Imagen de ${carName}`}
          className="h-40 w-full object-cover"
          onError={e => { e.target.src = getImageUrl(null, 'car'); }}
        />
        
        {/* Badge de disponibilidad */}
        {availableUnits !== null && (
          <div className={`absolute top-2 right-2 px-2 py-0.5 rounded-full text-xs font-semibold ${
            availableUnits > 0 ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"
          }`}>
            {availableUnits > 0 ? `${availableUnits} disponible(s)` : "No disponible"}
          </div>
        )}
        
        {/* Botón de favorito */}
        {showFavoriteButton && carModelId && (
          <div className="absolute top-2 left-2">
            <FavoriteButton
              carModelId={carModelId}
              isFavorite={isCurrentlyFavorite}
              onToggle={handleFavoriteToggle}
              size="sm"
            />
          </div>
        )}
      </div>

      {/* Contenido */}
      <div className="p-4 flex-1 flex flex-col justify-between">
        <div>
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <h3 className="text-base sm:text-lg font-semibold text-neutral-dark">{carName}</h3>
              <p className="text-sm text-gray-500">{carCategory}</p>
              {car?.year && <p className="text-xs text-gray-400">Año {car.year}</p>}
            </div>
            
            {/* Indicador de favorito en el título */}
            {isCurrentlyFavorite && (
              <span className="text-yellow-500 text-sm ml-2" title="En tus favoritos">
                ⭐
              </span>
            )}
          </div>
          
          <p className="text-sm font-semibold text-neutral-dark mt-2">
            {price ? `$${Number(price).toLocaleString()} / día` : "Precio no disponible"}
          </p>
        </div>

        <div className="mt-4 flex gap-2">
          <button
            onClick={() => onDetail && onDetail(car)}
            className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-800 text-xs sm:text-sm py-2 px-3 rounded-md transition"
          >
            Ver más
          </button>
          <button
            onClick={() => canReserve && isAvailable && onReserve && onReserve(car)}
            disabled={!canReserve || !isAvailable}
            className={`flex-1 text-xs sm:text-sm py-2 px-3 rounded-md transition ${
              canReserve && isAvailable
                ? "bg-primary hover:bg-primary-dark text-white"
                : "bg-gray-300 text-gray-500 cursor-not-allowed"
            }`}
          >
            {!isAvailable ? "No disponible" : "Reservar"}
          </button>
        </div>
      </div>
    </article>
  );
}
