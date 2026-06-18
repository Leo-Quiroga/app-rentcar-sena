// Componente Header con menú de navegación, autenticación y badges de mensajes y favoritos
import { useState, useEffect } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import { getUnreadCount } from "../api/contactApi";
import { useFavorites } from "../utils/useFavorites";
import logoWhite from "../assets/logowhite.png";
export default function Header() {
  const [isOpen, setIsOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const { user, logout } = useAuth();
  const { count: favoritesCount } = useFavorites();
  const navigate = useNavigate();
  const location = useLocation();

  // Cargar conteo de mensajes no leídos
  useEffect(() => {
    if (!user) { setUnreadCount(0); return; }

    const fetchCount = () => {
      getUnreadCount()
        .then(data => setUnreadCount(data.unreadCount || 0))
        .catch(() => setUnreadCount(0));
    };

    fetchCount();
    // Actualizar cada 60 segundos
    const interval = setInterval(fetchCount, 60000);
    return () => clearInterval(interval);
  }, [user, location.pathname]); // Re-consultar al cambiar de página

  const handleLogout = () => {
    logout();
    setIsProfileOpen(false);
    setUnreadCount(0);
    navigate("/", { replace: true });
  };

  const messagesPath = user?.role === "ADMIN" ? "/admin/mensajes" : "/mis-mensajes";
  const messagesLabel = user?.role === "ADMIN" ? "Gestor de mensajes" : "Mis mensajes";

  const displayName = user
    ? (user.firstName ? user.firstName : user.email.split("@")[0])
    : "";

  return (
    <header className="fixed top-0 left-0 right-0 z-50 w-full bg-primary text-white shadow-md font-sans">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between h-20">

        {/* Logo */}
        <Link to="/" className="flex items-center gap-3" aria-label="Ir al inicio - AutoReserve">
          <img src={logoWhite} alt="AutoReserve" className="h-14 sm:h-20 md:h-28 w-auto object-contain"/>
          <span className="font-display text-2xl font-bold leading-none">AutoReserve</span>
        </Link>

        {/* Nav desktop */}
        <nav aria-label="Menú principal" className="hidden md:flex gap-8 items-center">
          <Link to="/" className="hover:text-secondary transition">Inicio</Link>
          <Link to="/categorias" className="hover:text-secondary transition">Categorías</Link>
          <Link to="/sedes" className="hover:text-secondary transition">Sedes</Link>
          {user && (
            <>
              <Link to="/favoritos" className="hover:text-secondary transition flex items-center gap-1">
                Favoritos
                {favoritesCount > 0 && (
                  <span className="bg-yellow-500 text-yellow-900 text-xs font-bold rounded-full min-w-[18px] h-[18px] flex items-center justify-center px-1">
                    {favoritesCount}
                  </span>
                )}
              </Link>
              {user.role === "ADMIN" ? (
                <Link to="/admin/reservas" className="hover:text-secondary transition">
                  Reservas
                </Link>
              ) : (
                <Link to="/reservas" className="hover:text-secondary transition">
                  Mis Reservas
                </Link>
              )}
            </>
          )}
        </nav>

        {/* Acciones desktop */}
        <div className="hidden md:flex gap-3 items-center relative">
          {!user ? (
            <>
              <Link to="/login" className="px-4 py-2 bg-secondary text-gray-900 font-semibold rounded-lg hover:bg-secondary-dark transition">
                Iniciar sesión
              </Link>
              <Link to="/register" className="px-4 py-2 border border-secondary rounded-lg hover:bg-secondary hover:text-gray-900 transition">
                Registrarse
              </Link>
            </>
          ) : (
            <div className="flex items-center gap-3">
              {/* Badge de mensajes no leídos */}
              <Link to={messagesPath} className="relative p-2 hover:text-secondary transition"
                title={messagesLabel}>
                <span className="text-xl">💬</span>
                {unreadCount > 0 && (
                  <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs font-bold rounded-full min-w-[18px] h-[18px] flex items-center justify-center px-1">
                    {unreadCount > 99 ? "99+" : unreadCount}
                  </span>
                )}
              </Link>

              {/* Dropdown de perfil */}
              <div className="relative">
                <button
                  onClick={() => setIsProfileOpen(!isProfileOpen)}
                  className="flex items-center gap-2 px-4 py-2 bg-primary-dark rounded-full border border-secondary hover:bg-opacity-80 transition"
                >
                  <div className="w-8 h-8 bg-secondary rounded-full flex items-center justify-center text-gray-900 font-bold">
                    {displayName.charAt(0).toUpperCase()}
                  </div>
                  <span>Hola, {displayName}</span>
                  <span className="text-xs">▼</span>
                </button>

                {isProfileOpen && (
                  <div className="absolute right-0 mt-2 w-52 bg-white text-gray-800 rounded-lg shadow-xl overflow-hidden py-1 border border-gray-100">
                    {user.role === "ADMIN" && (
                      <Link to="/admin" className="block px-4 py-2 text-sm hover:bg-gray-100 font-semibold text-primary"
                        onClick={() => setIsProfileOpen(false)}>
                        Dashboard Admin
                      </Link>
                    )}
                    <Link to="/perfil" className="block px-4 py-2 text-sm hover:bg-gray-100"
                      onClick={() => setIsProfileOpen(false)}>
                      Mi Perfil
                    </Link>
                    <Link to="/favoritos"
                      className="flex items-center justify-between px-4 py-2 text-sm hover:bg-gray-100"
                      onClick={() => setIsProfileOpen(false)}>
                      <span>Mis Favoritos</span>
                      {favoritesCount > 0 && (
                        <span className="bg-yellow-500 text-yellow-900 text-xs font-bold rounded-full min-w-[18px] h-[18px] flex items-center justify-center px-1">
                          {favoritesCount > 99 ? "99+" : favoritesCount}
                        </span>
                      )}
                    </Link>
                    <Link to={messagesPath}
                      className="flex items-center justify-between px-4 py-2 text-sm hover:bg-gray-100"
                      onClick={() => setIsProfileOpen(false)}>
                      <span>{messagesLabel}</span>
                      {unreadCount > 0 && (
                        <span className="bg-red-500 text-white text-xs font-bold rounded-full min-w-[18px] h-[18px] flex items-center justify-center px-1">
                          {unreadCount > 99 ? "99+" : unreadCount}
                        </span>
                      )}
                    </Link>
                    <hr className="my-1 border-gray-100" />
                    <button onClick={handleLogout}
                      className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 font-medium">
                      Cerrar sesión
                    </button>
                  </div>
                )}
              </div>
            </div>
          )}
        </div>

        {/* Botón hamburguesa móvil */}
        <button className="md:hidden p-2 rounded hover:bg-primary-dark transition"
          onClick={() => setIsOpen(!isOpen)}>
          {isOpen ? "✕" : "☰"}
        </button>
      </div>

      {/* Menú móvil */}
      <div className={`md:hidden ${isOpen ? "block" : "hidden"} bg-primary-dark px-4 py-3`}>
        <Link to="/" className="block py-2 hover:text-secondary transition" onClick={() => setIsOpen(false)}>Inicio</Link>
        <Link to="/categorias" className="block py-2 hover:text-secondary transition" onClick={() => setIsOpen(false)}>Categorías</Link>
        
        {user && (
          <>
            <Link to="/favoritos" className="flex items-center gap-2 py-2 hover:text-secondary transition" onClick={() => setIsOpen(false)}>
              <span>Favoritos</span>
              {favoritesCount > 0 && (
                <span className="bg-yellow-500 text-yellow-900 text-xs font-bold rounded-full px-1.5 py-0.5">
                  {favoritesCount}
                </span>
              )}
            </Link>
            {user.role === "ADMIN" ? (
              <Link to="/admin/reservas" className="block py-2 hover:text-secondary transition" onClick={() => setIsOpen(false)}>
                Reservas
              </Link>
            ) : (
              <Link to="/reservas" className="block py-2 hover:text-secondary transition" onClick={() => setIsOpen(false)}>
                Mis Reservas
              </Link>
            )}
          </>
        )}

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
            <Link to={messagesPath} className="flex items-center gap-2 py-2 hover:text-secondary transition" onClick={() => setIsOpen(false)}>
              <span>{messagesLabel}</span>
              {unreadCount > 0 && (
                <span className="bg-red-500 text-white text-xs font-bold rounded-full px-1.5 py-0.5">
                  {unreadCount}
                </span>
              )}
            </Link>
            <button onClick={handleLogout} className="block py-2 text-red-400 hover:text-red-300 transition w-full text-left">
              Cerrar sesión
            </button>
          </>
        )}
      </div>
    </header>
  );
}
