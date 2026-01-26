
// src/components/Header.jsx
import { useState } from "react";
import { Link } from "react-router-dom";

export default function Header() {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <header className="fixed top-0 left-0 right-0 z-50 w-full bg-primary text-white shadow-md">
      {/* Contenedor principal */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between h-20">
        {/* Left: logo + lema */}
        <Link to="/" className="flex items-center gap-3" aria-label="Ir al inicio - AutoReserve">
          <img
            src="/src/assets/logowhite.png"
            alt="AutoReserve"
            className="h-16 w-auto object-contain"
          />
          <span className="font-display text-2xl font-bold leading-none">AutoReserve</span>
        </Link>

        {/* Nav (desktop) */}
        <nav aria-label="Menú principal" className="hidden md:flex gap-8 items-center">
          <Link to="/" className="hover:text-secondary transition">Inicio</Link>
          <Link to="/categorias" className="hover:text-secondary transition">Categorías</Link>
          <Link to="/sedes" className="hover:text-secondary transition">Sedes</Link>
        </nav>

        {/* Acciones (desktop) */}
        <div className="hidden md:flex gap-3 items-center">
          <Link
            to="/login"
            className="px-4 py-2 bg-secondary text-gray-900 font-semibold rounded-lg hover:bg-secondary-dark transition"
            aria-label="Iniciar sesión"
          >
            Iniciar sesión
          </Link>
          <Link
            to="/register"
            className="px-4 py-2 border border-secondary rounded-lg hover:bg-secondary hover:text-gray-900 transition"
            aria-label="Crear cuenta"
          >
            Registrarse
          </Link>
        </div>

        {/* Botón hamburguesa (móvil) */}
        <button
          className="md:hidden p-2 rounded hover:bg-primary-dark transition"
          onClick={() => setIsOpen(!isOpen)}
          aria-expanded={isOpen}
          aria-controls="mobile-menu"
          aria-label={isOpen ? "Cerrar menú" : "Abrir menú"}
        >
          {/* Aquí podrías reemplazar por un ícono SVG */}
          {isOpen ? "✕" : "☰"}
        </button>
      </div>

      {/* Menú móvil desplegable */}
      <div id="mobile-menu" className={`md:hidden ${isOpen ? "block" : "hidden"} bg-primary-dark px-4 py-3`}>
        <Link to="/" className="block py-2 hover:text-secondary transition">Inicio</Link>
        <Link to="/categorias" className="block py-2 hover:text-secondary transition">Categorías</Link>
        <Link to="/sedes" className="block py-2 hover:text-secondary transition">Sedes</Link>
        <Link to="/login" className="block py-2 hover:text-secondary transition">Iniciar sesión</Link>
        <Link to="/register" className="block py-2 hover:text-secondary transition">Registrarse</Link>
      </div>
    </header>
  );
}
