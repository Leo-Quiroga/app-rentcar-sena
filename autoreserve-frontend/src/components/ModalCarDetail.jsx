// Componente para mostrar los detalles de un auto en un modal
import { getImageUrl } from '../utils/imageUtils';

export default function ModalCarDetail({ car, filters, onClose, onReserve }) {
  if (!car) return null;

  const canReserve = filters?.startDate && filters?.endDate;

  const handleOverlayClick = (e) => {
    if (e.target === e.currentTarget) onClose && onClose();
  };

  return (
    <div
      className="fixed inset-0 bg-black/50 flex justify-center items-center z-50 p-4"
      role="dialog"
      aria-modal="true"
      aria-labelledby="car-detail-title"
      onClick={handleOverlayClick}
    >
      <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <h2
          id="car-detail-title"
          className="text-2xl font-bold text-neutral-dark mb-4"
        >
          {car.name}
        </h2>

        <div className="grid md:grid-cols-2 gap-6">
          {/* Imagen del auto */}
          <div>
            <img
              src={getImageUrl(car.image, 'car')}
              alt={car.name}
              className="w-full h-48 object-cover rounded mb-4"
              onError={(e) => {
                e.target.src = getImageUrl(null, 'car');
              }}
            />
          </div>

          {/* Información básica */}
          <div className="space-y-3">
            <div className="border-b pb-3">
              <h3 className="text-lg font-semibold text-gray-800 mb-2">Información General</h3>
              <div className="space-y-2 text-gray-700">
                <p><span className="font-medium">Categoría:</span> {car.category?.name || car.category || 'N/A'}</p>
                <p><span className="font-medium">Marca:</span> {car.brand || 'N/A'}</p>
                <p><span className="font-medium">Modelo:</span> {car.model || 'N/A'}</p>
                <p><span className="font-medium">Año:</span> {car.year || 'N/A'}</p>
                <p>
                  <span className="font-medium">Precio por día:</span>{" "}
                  <span className="font-bold text-primary text-lg">
                    ${car.pricePerDay || 'N/A'}
                  </span>
                </p>
                <p>
                  <span className="font-medium">Calificación:</span> 
                  <span className="ml-2">⭐ {car.rating || 'N/A'} / 5</span>
                </p>
              </div>
            </div>

            {/* Especificaciones */}
            {(car.fuelType || car.transmission || car.seats || car.luggage) && (
              <div className="border-b pb-3">
                <h3 className="text-lg font-semibold text-gray-800 mb-2">Especificaciones</h3>
                <div className="space-y-2 text-gray-700">
                  {car.fuelType && <p><span className="font-medium">Combustible:</span> {car.fuelType}</p>}
                  {car.transmission && <p><span className="font-medium">Transmisión:</span> {car.transmission}</p>}
                  {car.seats && <p><span className="font-medium">Asientos:</span> {car.seats}</p>}
                  {car.luggage && <p><span className="font-medium">Equipaje:</span> {car.luggage}</p>}
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Descripción completa */}
        {car.description && (
          <div className="mt-6 border-t pt-4">
            <h3 className="text-lg font-semibold text-gray-800 mb-3">Descripción</h3>
            <p className="text-gray-700 leading-relaxed">
              {car.description}
            </p>
          </div>
        )}

        {/* Características adicionales */}
        {(car.features && car.features.length > 0) && (
          <div className="mt-6 border-t pt-4">
            <h3 className="text-lg font-semibold text-gray-800 mb-3">Características</h3>
            <div className="grid grid-cols-2 gap-2">
              {car.features.map((feature, index) => (
                <div key={index} className="flex items-center text-gray-700">
                  <span className="text-green-500 mr-2">✓</span>
                  {feature}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Estado y disponibilidad */}
        {car.status && (
          <div className="mt-6 border-t pt-4">
            <h3 className="text-lg font-semibold text-gray-800 mb-3">Estado</h3>
            <span className={`inline-block px-3 py-1 rounded text-sm font-medium ${
              car.status === 'AVAILABLE' ? 'bg-green-100 text-green-800' :
              car.status === 'RENTED' ? 'bg-yellow-100 text-yellow-800' :
              'bg-red-100 text-red-800'
            }`}>
              {car.status === 'AVAILABLE' ? 'Disponible' :
               car.status === 'RENTED' ? 'Rentado' :
               car.status === 'MAINTENANCE' ? 'En Mantenimiento' : car.status}
            </span>
          </div>
        )}

        {/* Botones */}
        <div className="flex justify-end gap-3 mt-8 pt-4 border-t">
          <button
            onClick={onClose}
            className="px-6 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition font-medium"
          >
            Cerrar
          </button>

          {onReserve && (
            <button
              onClick={() => onReserve(car)}
              disabled={!canReserve}
              className={`px-6 py-2 rounded-lg transition font-medium ${
                canReserve
                  ? "bg-primary text-white hover:bg-blue-700"
                  : "bg-gray-300 text-gray-500 cursor-not-allowed"
              }`}
              title={!canReserve ? "Selecciona fechas de inicio y fin para reservar" : ""}
            >
              Reservar
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
