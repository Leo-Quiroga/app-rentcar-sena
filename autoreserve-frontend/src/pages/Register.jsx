import { Link } from "react-router-dom";

export default function Register() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-neutral-light px-4">
      <div className="w-full max-w-md bg-white rounded-lg shadow-md p-8">
        {/* Título */}
        <h1 className="text-2xl font-bold text-center text-neutral-dark mb-6">
          Crear cuenta
        </h1>

        {/* Formulario */}
        <form className="space-y-4">
          {/* Nombre */}
          <div>
            <label
              htmlFor="name"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Nombre completo
            </label>
            <input
              type="text"
              id="name"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-primary focus:border-primary"
              placeholder="Tu nombre"
              required
            />
          </div>

          {/* Email */}
          <div>
            <label
              htmlFor="email"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Correo electrónico
            </label>
            <input
              type="email"
              id="email"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-primary focus:border-primary"
              placeholder="tu@email.com"
              required
            />
          </div>

          {/* Contraseña */}
          <div>
            <label
              htmlFor="password"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Contraseña
            </label>
            <input
              type="password"
              id="password"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-primary focus:border-primary"
              placeholder="********"
              required
            />
          </div>

          {/* Confirmar contraseña */}
          <div>
            <label
              htmlFor="confirm-password"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Confirmar contraseña
            </label>
            <input
              type="password"
              id="confirm-password"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-primary focus:border-primary"
              placeholder="********"
              required
            />
          </div>

          {/* Botón */}
          <button
            type="submit"
            className="w-full py-2 px-4 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
          >
            Registrarse
          </button>
        </form>

        {/* Enlace secundario */}
        <div className="mt-6 text-center text-sm text-gray-600">
          ¿Ya tienes cuenta?{" "}
          <Link to="/login" className="hover:underline text-primary">
            Inicia sesión
          </Link>
        </div>
      </div>
    </div>
  );
}

