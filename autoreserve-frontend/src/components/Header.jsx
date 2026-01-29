
// src/components/Header.jsx
import { useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import { useNavigate } from "react-router-dom";

export default function Header() {
  const [isOpen, setIsOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false); // Estado para el dropdown de perfil
  const { user, logout } = useAuth(); // Obtenemos el usuario y la función logout
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    setIsProfileOpen(false);
    navigate("/", { replace: true });
  };

 return (
    <header className="fixed top-0 left-0 right-0 z-50 w-full bg-primary text-white shadow-md font-sans">
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
        <div className="hidden md:flex gap-3 items-center relative">
          {!user ? (
            <>
              {/* MOSTRAR CUANDO NO HAY SESIÓN */}
              <Link
                to="/login"
                className="px-4 py-2 bg-secondary text-gray-900 font-semibold rounded-lg hover:bg-secondary-dark transition"
              >
                Iniciar sesión
              </Link>
              <Link
                to="/register"
                className="px-4 py-2 border border-secondary rounded-lg hover:bg-secondary hover:text-gray-900 transition"
              >
                Registrarse
              </Link>
            </>
          ) : (
            /* MOSTRAR CUANDO HAY SESIÓN */
            <div className="relative">
              <button 
                onClick={() => setIsProfileOpen(!isProfileOpen)}
                className="flex items-center gap-2 px-4 py-2 bg-primary-dark rounded-full border border-secondary hover:bg-opacity-80 transition"
              >
                <div className="w-8 h-8 bg-secondary rounded-full flex items-center justify-center text-gray-900 font-bold">
                  {user.email.charAt(0).toUpperCase()}
                </div>
                <span>Hola, {user.email.split('@')[0]}</span>
                <span className="text-xs">▼</span>
              </button>

              {/* Dropdown Menu */}
              {isProfileOpen && (
                <div className="absolute right-0 mt-2 w-48 bg-white text-gray-800 rounded-lg shadow-xl overflow-hidden py-1 border border-gray-100">
                  {user.role === "ADMIN" && (
                    <Link 
                      to="/admin" 
                      className="block px-4 py-2 text-sm hover:bg-gray-100 font-semibold text-primary"
                      onClick={() => setIsProfileOpen(false)}
                    >
                      Dashboard Admin
                    </Link>
                  )}
                  <Link 
                    to="/perfil" 
                    className="block px-4 py-2 text-sm hover:bg-gray-100"
                    onClick={() => setIsProfileOpen(false)}
                  >
                    Mi Perfil
                  </Link>
                  <hr className="my-1 border-gray-100" />
                  <button 
                    onClick={handleLogout}
                    className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 font-medium"
                  >
                    Cerrar sesión
                  </button>
                </div>
              )}
            </div>
          )}
        </div>

        {/* Botón hamburguesa (móvil) */}
        <button
          className="md:hidden p-2 rounded hover:bg-primary-dark transition"
          onClick={() => setIsOpen(!isOpen)}
        >
          {isOpen ? "✕" : "☰"}
        </button>
      </div>

      {/* Menú móvil desplegable */}
      <div id="mobile-menu" className={`md:hidden ${isOpen ? "block" : "hidden"} bg-primary-dark px-4 py-3`}>
        <Link to="/" className="block py-2 hover:text-secondary transition" onClick={() => setIsOpen(false)}>Inicio</Link>
        <Link to="/categorias" className="block py-2 hover:text-secondary transition" onClick={() => setIsOpen(false)}>Categorías</Link>
        
        {!user ? (
          <>
            <Link to="/login" className="block py-2 hover:text-secondary transition" onClick={() => setIsOpen(false)}>Iniciar sesión</Link>
            <Link to="/register" className="block py-2 hover:text-secondary transition" onClick={() => setIsOpen(false)}>Registrarse</Link>
          </>
        ) : (
          <>
            {user.role === "ADMIN" && (
               <Link to="/admin" className="block py-2 text-secondary font-bold" onClick={() => setIsOpen(false)}>Dashboard Admin</Link>
            )}
            <Link to="/perfil" className="block py-2 hover:text-secondary transition" onClick={() => setIsOpen(false)}>Mi Perfil</Link>
            <button onClick={handleLogout} className="block py-2 text-red-400 hover:text-red-300 transition w-full text-left">Cerrar sesión</button>
          </>
        )}
      </div>
    </header>
  );
}