import { Link } from "react-router-dom";

export default function Login() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-neutral-light px-4">
      <div className="w-full max-w-md bg-white rounded-lg shadow-md p-8">
        {/* Título */}
        <h1 className="text-2xl font-bold text-center text-neutral-dark mb-6">
          Iniciar sesión
        </h1>

        {/* Formulario */}
        <form className="space-y-4">
          {/* Email */}
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
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
            <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
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

          {/* Botón */}
          <button
            type="submit"
            className="w-full py-2 px-4 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
          >
            Iniciar sesión
          </button>
        </form>

        {/* Enlaces secundarios */}
        <div className="mt-6 flex flex-col sm:flex-row sm:justify-between text-sm text-gray-600">
          <Link to="/reset-password" className="hover:underline">
            ¿Olvidaste tu contraseña?
          </Link>
          <Link to="/register" className="hover:underline">
            Crear cuenta
          </Link>
        </div>
      </div>
    </div>
  );
}
