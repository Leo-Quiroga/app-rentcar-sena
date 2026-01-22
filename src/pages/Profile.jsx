import { Link } from "react-router-dom";

export default function Profile() {
  // Mock temporal de usuario (esto luego vendrá de auth/API)
  const user = {
    name: "Juan Pérez",
    email: "juan.perez@example.com",
    phone: "+57 300 123 4567",
    memberSince: "2024-06-15",
  };

  return (
    <div className="max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8 space-y-10">
      {/* Encabezado */}
      <header className="text-center">
        <h1 className="text-3xl font-bold text-neutral-dark mb-4">👤 Mi Perfil</h1>
        <p className="text-gray-600">
          Aquí puedes consultar tu información personal y acceder a tus opciones
          principales dentro de AutoReserve.
        </p>
      </header>

      {/* Información del usuario */}
      <section className="bg-white rounded-lg shadow p-6 space-y-4">
        <h2 className="text-xl font-semibold text-neutral-dark mb-2">
          Información personal
        </h2>
        <p>
          <strong>Nombre:</strong> {user.name}
        </p>
        <p>
          <strong>Email:</strong> {user.email}
        </p>
        <p>
          <strong>Teléfono:</strong> {user.phone}
        </p>
        <p>
          <strong>Miembro desde:</strong>{" "}
          {new Date(user.memberSince).toLocaleDateString()}
        </p>

        <div className="flex justify-end mt-4">
          <button className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition">
            Editar perfil
          </button>
        </div>
      </section>

      {/* Accesos rápidos */}
      <section className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <Link
          to="/reservas"
          className="bg-white shadow rounded-lg p-6 text-center hover:shadow-md transition"
        >
          <h3 className="text-lg font-semibold text-neutral-dark mb-2">
            Mis Reservas
          </h3>
          <p className="text-gray-600 text-sm">Consulta tus reservas activas y pasadas.</p>
        </Link>

        <Link
          to="/favoritos"
          className="bg-white shadow rounded-lg p-6 text-center hover:shadow-md transition"
        >
          <h3 className="text-lg font-semibold text-neutral-dark mb-2">
            Favoritos
          </h3>
          <p className="text-gray-600 text-sm">Encuentra rápidamente los autos que guardaste.</p>
        </Link>

        <Link
          to="/perfil/cambiar-password"
          className="bg-white shadow rounded-lg p-6 text-center hover:shadow-md transition"
        >
          <h3 className="text-lg font-semibold text-neutral-dark mb-2">
            Cambiar contraseña
          </h3>
          <p className="text-gray-600 text-sm">Mantén segura tu cuenta actualizando tu clave.</p>
        </Link>
      </section>
    </div>
  );
}
