// Pantalla de error 404 - Página no encontrada
import { Link } from "react-router-dom";

export default function Error404() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center text-center px-6">
      {/* Icono / ilustración */}
      <div className="text-7xl mb-6">🚧</div>

      {/* Mensaje */}
      <h1 className="text-3xl font-bold text-neutral-dark mb-2">
        404 – Página no encontrada
      </h1>
      <p className="text-gray-600 mb-6 max-w-md">
        Lo sentimos, la página que buscas no existe o fue movida.  
        Revisa la URL o vuelve al inicio.
      </p>

      {/* Botón volver */}
      <Link
        to="/"
        className="px-6 py-3 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
      >
        Ir al inicio
      </Link>
    </div>
  );
}
