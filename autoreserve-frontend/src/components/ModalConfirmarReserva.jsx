// Componente para confirmar la reserva de un auto
// src/components/ModalConfirmarReserva.jsx
export default function ModalConfirmarReserva({ car, filters, onClose, onConfirm }) {
  if (!car || !filters || !filters.startDate || !filters.endDate) return null;

  const start = new Date(filters.startDate);
  const end = new Date(filters.endDate);
  const dias = Math.max(1, Math.ceil((end - start) / (1000 * 60 * 60 * 24)));
  const total = (car.pricePerDay || 0) * dias;

  return (
    <div className="fixed inset-0 bg-black/50 flex justify-center items-center z-50">
      <div className="bg-white rounded-lg shadow-lg w-full max-w-lg mx-4 sm:mx-6 lg:mx-8 p-6">
        {/* Header */}
        <h2 className="text-xl font-bold text-neutral-dark mb-4">Confirmar Reserva</h2>

        {/* Info del auto */}
        <div className="space-y-2 text-sm sm:text-base text-gray-700">
          <p><span className="font-semibold">Auto:</span> {car.name}</p>
          <p><span className="font-semibold">Categoría:</span> {car.category}</p>
          <p><span className="font-semibold">Ciudad de Retiro:</span> {filters.pickupCity}</p>
          <p><span className="font-semibold">Ciudad de Entrega:</span> {filters.dropoffCity}</p>
          <p><span className="font-semibold">Fechas:</span> {filters.startDate} → {filters.endDate}</p>
          <p><span className="font-semibold">Precio por día:</span> ${car.pricePerDay ?? 0}</p>
          <p><span className="font-semibold">Días:</span> {dias}</p>
          <p className="font-bold text-neutral-dark mt-3">
            Total: ${total.toLocaleString()}
          </p>
        </div>

        {/* Botones */}
        <div className="flex justify-end gap-3 mt-6">
          <button
            onClick={onClose}
            className="px-4 py-2 border border-gray-300 text-gray-600 rounded-lg hover:bg-gray-100 transition"
          >
            Cancelar
          </button>
          <button
            onClick={() => onConfirm && onConfirm({ car, filters, dias, total })}
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
          >
            Confirmar Reserva
          </button>
        </div>
      </div>
    </div>
  );
}
