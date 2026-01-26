

// src/components/Footer.jsx
import { Link } from "react-router-dom";

export default function Footer() {
  return (
    <footer className="bg-neutral-dark text-gray-300 py-6 mt-10">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col md:flex-row justify-between items-center gap-6">
        
        {/* Logo */}
        <Link to="/" className="flex items-center gap-2">
          <img
            src="/src/assets/logowhite.png"
            alt="AutoReserve"
            className="h-10 sm:h-12 md:h-14 w-auto object-contain"
          />
          <p className="font-display text-base text-gray-400">AutoReserve</p>
        </Link>

        {/* Info breve */}
        <p className="text-sm text-gray-400 text-center md:text-left">
          © {new Date().getFullYear()} AutoReserve. Todos los derechos reservados.
        </p>

        {/* Links secundarios */}
        <nav aria-label="Enlaces secundarios" className="flex gap-6 text-sm">
          <Link to="/politicas" className="hover:text-secondary transition">
            Políticas
          </Link>
          <Link to="/contacto" className="hover:text-secondary transition">
            Contacto
          </Link>
          <Link to="/faq" className="hover:text-secondary transition">
            Dudas / FAQ
          </Link>
        </nav>
      </div>
    </footer>
  );
}
