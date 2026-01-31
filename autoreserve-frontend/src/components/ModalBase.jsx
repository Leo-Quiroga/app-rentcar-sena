// Componente base para modales
export default function ModalBase({ title, children, onClose }) {
  const handleOverlayClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose && onClose();
    }
  };

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 animate-fadeIn"
      role="dialog"
      aria-modal="true"
      onClick={handleOverlayClick}
    >
      <div className="bg-white rounded-lg shadow-lg w-full max-w-lg sm:max-w-xl md:max-w-2xl lg:max-w-3xl mx-4 animate-fadeIn">
        {/* Header */}
        <div className="flex items-center justify-between border-b px-6 py-3">
          <h2 className="text-lg font-semibold text-neutral-dark">{title}</h2>
          <button
            onClick={onClose}
            aria-label="Cerrar"
            className="text-gray-500 hover:text-secondary transition text-xl"
          >
            ✕
          </button>
        </div>

        {/* Body */}
        <div className="p-6">{children}</div>
      </div>
    </div>
  );
}
