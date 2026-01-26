import { useState } from "react";
import { Link } from "react-router-dom";

export default function ResetPassword() {
  const [email, setEmail] = useState("");
  const [submitted, setSubmitted] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    // 🔗 Aquí luego se conectará con la API para enviar el correo de reseteo
    if (email.trim()) {
      setSubmitted(true);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-neutral-light px-4">
      <div className="w-full max-w-md bg-white rounded-lg shadow-md p-8">
        {/* Icono */}
        <div className="text-center text-5xl mb-4">🔑</div>

        {/* Estado: antes de enviar */}
        {!submitted ? (
          <>
            <h1 className="text-2xl font-bold text-neutral-dark mb-2 text-center">
              Restablecer contraseña
            </h1>
            <p className="text-sm text-gray-600 mb-6 text-center">
              Ingresa tu correo electrónico y te enviaremos un enlace para
              restablecer tu contraseña.
            </p>

            <form onSubmit={handleSubmit} className="space-y-4">
              {/* Campo correo */}
              <div>
                <label
                  htmlFor="email"
                  className="block text-sm font-medium text-gray-700"
                >
                  Correo electrónico
                </label>
                <input
                  type="email"
                  id="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  className="mt-1 w-full px-3 py-2 border rounded-lg shadow-sm focus:ring-primary focus:border-primary text-sm"
                  placeholder="tuemail@ejemplo.com"
                />
              </div>

              {/* Botón enviar */}
              <button
                type="submit"
                className="w-full py-2 px-4 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
              >
                Enviar enlace
              </button>
            </form>

            {/* Enlace secundario */}
            <div className="mt-6 text-sm text-center text-gray-600">
              ¿Recordaste tu contraseña?{" "}
              <Link to="/login" className="text-primary hover:underline">
                Inicia sesión
              </Link>
            </div>
          </>
        ) : (
          // Estado: después de enviar
          <div className="text-center">
            <h1 className="text-2xl font-bold text-neutral-dark mb-2">
              Revisa tu correo 📩
            </h1>
            <p className="text-sm text-gray-600 mb-6">
              Si existe una cuenta asociada a <strong>{email}</strong>, te
              hemos enviado un enlace para restablecer tu contraseña.
            </p>

            <Link
              to="/login"
              className="inline-block px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
            >
              Volver al inicio de sesión
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}
