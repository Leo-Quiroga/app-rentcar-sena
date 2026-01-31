// Componente para mostrar los detalles de un auto en un modal
export default function ModalCarDetail({ car, filters, onClose, onReserve }) {
  if (!car) return null;

  const canReserve = filters?.startDate && filters?.endDate;

  const handleOverlayClick = (e) => {
    if (e.target === e.currentTarget) onClose && onClose();
  };

  return (
    <div
      className="fixed inset-0 bg-black/50 flex justify-center items-center z-50"
      role="dialog"
      aria-modal="true"
      aria-labelledby="car-detail-title"
      onClick={handleOverlayClick}
    >
      <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-lg">
        <h2
          id="car-detail-title"
          className="text-xl font-bold text-neutral-dark mb-4"
        >
          {car.name}
        </h2>

        <img
          src={car.image}
          alt={car.name}
          className="w-full h-40 object-cover rounded mb-4"
        />

        <div className="flex flex-col gap-2 text-gray-700">
          <p>Categoría: {car.category}</p>
          <p>
            Precio por día:{" "}
            <span className="font-semibold text-neutral-dark">
              ${car.pricePerDay}
            </span>
          </p>
          <p>
            Calificación: ⭐ {car.rating ?? "N/A"} / 5
          </p>
        </div>

        <div className="flex justify-end gap-3 mt-6">
          <button
            onClick={onClose}
            className="px-4 py-2 bg-neutral-light text-neutral-dark rounded hover:bg-gray-200 transition"
          >
            Cerrar
          </button>

          <button
            onClick={() => onReserve(car)}
            disabled={!canReserve}
            className={`px-4 py-2 rounded transition ${
              canReserve
                ? "bg-primary text-white hover:bg-primary-dark"
                : "bg-gray-300 text-gray-500 cursor-not-allowed"
            }`}
          >
            Reservar
          </button>
        </div>
      </div>
    </div>
  );
}
