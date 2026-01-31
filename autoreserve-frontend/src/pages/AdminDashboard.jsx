// Panel de administración con accesos rápidos y métricas
import { Link } from "react-router-dom";

export default function AdminDashboard() {
  // Renderizar panel de administración
  return (
    <div className="min-h-screen bg-neutral-light px-4 py-10 sm:px-6 lg:px-8">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-3xl font-bold text-neutral-dark mb-8">
          📊 Panel de Administración
        </h1>

        {/* Resumen / métricas simuladas */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
          <div className="bg-white shadow rounded-lg p-6 text-center">
            <p className="text-2xl font-bold text-primary">120</p>
            <p className="text-sm text-gray-600">Autos registrados</p>
          </div>
          <div className="bg-white shadow rounded-lg p-6 text-center">
            <p className="text-2xl font-bold text-primary">8</p>
            <p className="text-sm text-gray-600">Categorías</p>
          </div>
          <div className="bg-white shadow rounded-lg p-6 text-center">
            <p className="text-2xl font-bold text-primary">45</p>
            <p className="text-sm text-gray-600">Usuarios</p>
          </div>
          <div className="bg-white shadow rounded-lg p-6 text-center">
            <p className="text-2xl font-bold text-primary">32</p>
            <p className="text-sm text-gray-600">Reservas activas</p>
          </div>
        </div>

        {/* Accesos rápidos */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          <Link
            to="/admin/autos"
            className="bg-white shadow rounded-lg p-6 flex flex-col items-center hover:bg-gray-50 transition"
          >
            <span className="text-4xl mb-2">🚗</span>
            <p className="text-lg font-semibold">Gestión de Autos</p>
          </Link>

          <Link
            to="/admin/categorias"
            className="bg-white shadow rounded-lg p-6 flex flex-col items-center hover:bg-gray-50 transition"
          >
            <span className="text-4xl mb-2">📂</span>
            <p className="text-lg font-semibold">Categorías</p>
          </Link>

          <Link
            to="/admin/sedes"
            className="bg-white shadow rounded-lg p-6 flex flex-col items-center hover:bg-gray-50 transition"
          >
            <span className="text-4xl mb-2">📍</span>
            <p className="text-lg font-semibold">Sedes</p>
          </Link>

          <Link
            to="/admin/usuarios"
            className="bg-white shadow rounded-lg p-6 flex flex-col items-center hover:bg-gray-50 transition"
          >
            <span className="text-4xl mb-2">👥</span>
            <p className="text-lg font-semibold">Usuarios</p>
          </Link>

          <Link
            to="/reservas"
            className="bg-white shadow rounded-lg p-6 flex flex-col items-center hover:bg-gray-50 transition"
          >
            <span className="text-4xl mb-2">📅</span>
            <p className="text-lg font-semibold">Reservas</p>
          </Link>

          <Link
            to="/politicas"
            className="bg-white shadow rounded-lg p-6 flex flex-col items-center hover:bg-gray-50 transition"
          >
            <span className="text-4xl mb-2">📜</span>
            <p className="text-lg font-semibold">Políticas</p>
          </Link>
        </div>
      </div>
    </div>
  );
}
