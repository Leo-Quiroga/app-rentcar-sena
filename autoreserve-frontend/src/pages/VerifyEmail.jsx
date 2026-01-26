import { Link } from "react-router-dom";

export default function VerifyEmail() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-neutral-light px-4">
      <div className="w-full max-w-md bg-white rounded-lg shadow-md p-8 text-center">
        {/* Icono */}
        <div className="text-5xl mb-4">📧</div>

        {/* Título */}
        <h1 className="text-2xl font-bold text-neutral-dark mb-2">
          Verifica tu correo electrónico
        </h1>

        {/* Descripción */}
        <p className="text-gray-600 mb-6 text-sm">
          Hemos enviado un enlace de verificación a tu correo.  
          Revisa tu bandeja de entrada y haz clic en el enlace para activar tu cuenta.
        </p>

        {/* Botón reenvío */}
        <button
          type="button"
          className="w-full py-2 px-4 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
        >
          Reenviar correo de verificación
        </button>

        {/* Enlace secundario */}
        <div className="mt-6 text-sm text-gray-600">
          ¿Ya verificaste tu cuenta?{" "}
          <Link to="/login" className="text-primary hover:underline">
            Inicia sesión
          </Link>
        </div>
      </div>
    </div>
  );
}
