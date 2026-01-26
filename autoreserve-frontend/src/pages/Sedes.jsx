import { mockSedes } from "../data/mockSedes";

export default function Sedes() {
  return (
    <div className="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <h1 className="text-2xl font-bold mb-8">🏢 Nuestras Sedes</h1>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
        {mockSedes.map((sede) => (
          <div
            key={sede.id}
            className="bg-white shadow rounded-lg overflow-hidden flex flex-col"
          >
            <img
              src={sede.image}
              alt={sede.name}
              className="h-48 w-full object-cover"
            />
            <div className="p-6 flex flex-col flex-grow">
              <h2 className="text-lg font-semibold mb-2">{sede.name}</h2>
              <p className="text-sm text-gray-600 mb-1">
                <strong>Dirección:</strong> {sede.address}
              </p>
              <p className="text-sm text-gray-600 mb-4">
                <strong>Teléfono:</strong> {sede.phone}
              </p>

              <button
                className="mt-auto px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
                onClick={() => alert(`📍 Ver ubicación de ${sede.name}`)}
              >
                Ver ubicación
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
