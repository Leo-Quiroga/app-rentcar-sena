// Panel de administración con accesos rápidos y métricas
import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { getAdminStats } from "../api/adminStatsApi";

export default function AdminDashboard() {
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalCars: 0,
    totalReservations: 0,
    totalCategories: 0,
    totalBranches: 0
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Cargar estadísticas al montar el componente
  useEffect(() => {
    const loadStats = async () => {
      try {
        setLoading(true);
        const data = await getAdminStats();
        setStats(data);
        setError(null);
      } catch (err) {
        setError(err.message);
        console.error('Error cargando estadísticas:', err);
      } finally {
        setLoading(false);
      }
    };

    loadStats();
  }, []);

  // Renderizar panel de administración
  return (
    <div className="min-h-screen bg-neutral-light px-4 py-10 sm:px-6 lg:px-8">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-3xl font-bold text-neutral-dark mb-8">
          📊 Panel de Administración
        </h1>

        {/* Resumen / métricas */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-6 mb-12">
          <div className="bg-white shadow rounded-lg p-6 text-center">
            {loading ? (
              <p className="text-2xl font-bold text-gray-400">...</p>
            ) : (
              <p className="text-2xl font-bold text-primary">{stats.totalCars}</p>
            )}
            <p className="text-sm text-gray-600">Autos registrados</p>
          </div>
          <div className="bg-white shadow rounded-lg p-6 text-center">
            {loading ? (
              <p className="text-2xl font-bold text-gray-400">...</p>
            ) : (
              <p className="text-2xl font-bold text-primary">{stats.totalCategories}</p>
            )}
            <p className="text-sm text-gray-600">Categorías</p>
          </div>
          <div className="bg-white shadow rounded-lg p-6 text-center">
            {loading ? (
              <p className="text-2xl font-bold text-gray-400">...</p>
            ) : (
              <p className="text-2xl font-bold text-primary">{stats.totalBranches}</p>
            )}
            <p className="text-sm text-gray-600">Sedes</p>
          </div>
          <div className="bg-white shadow rounded-lg p-6 text-center">
            {loading ? (
              <p className="text-2xl font-bold text-gray-400">...</p>
            ) : (
              <p className="text-2xl font-bold text-primary">{stats.totalUsers}</p>
            )}
            <p className="text-sm text-gray-600">Usuarios</p>
          </div>
          <div className="bg-white shadow rounded-lg p-6 text-center">
            {loading ? (
              <p className="text-2xl font-bold text-gray-400">...</p>
            ) : (
              <p className="text-2xl font-bold text-primary">{stats.totalReservations}</p>
            )}
            <p className="text-sm text-gray-600">Reservas</p>
          </div>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
            <p className="text-red-600 text-sm">Error cargando estadísticas: {error}</p>
          </div>
        )}

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
            to="/admin/reservas"
            className="bg-white shadow rounded-lg p-6 flex flex-col items-center hover:bg-gray-50 transition"
          >
            <span className="text-4xl mb-2">📅</span>
            <p className="text-lg font-semibold">Gestor de Reservas</p>
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
