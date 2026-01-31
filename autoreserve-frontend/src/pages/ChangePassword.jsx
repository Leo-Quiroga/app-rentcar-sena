//Pantalla para cambiar la contraseña de usuario
import { useState } from "react";
import { Link } from "react-router-dom";

export default function ChangePassword() {
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState("");
  // Manejar envío del formulario
  const handleSubmit = (e) => {
    e.preventDefault();
    setError("");

    // Validaciones básicas
    if (newPassword.length < 6) {
      setError("La nueva contraseña debe tener al menos 6 caracteres.");
      return;
    }
    if (newPassword !== confirmPassword) {
      setError("Las contraseñas no coinciden.");
      return;
    }

    // Aquí luego se conectará a la API para cambiar la contraseña
    setSuccess(true);
  };
  // Renderizar formulario de cambio de contraseña
  return (
    <div className="min-h-screen flex items-center justify-center bg-neutral-light px-4">
      <div className="w-full max-w-md bg-white rounded-lg shadow-md p-8">
        <div className="text-center text-5xl mb-4">🔒</div>

        {!success ? (
          <>
            <h1 className="text-2xl font-bold text-neutral-dark mb-2 text-center">
              Cambiar contraseña
            </h1>
            <p className="text-sm text-gray-600 mb-6 text-center">
              Ingresa tu contraseña actual y define una nueva.
            </p>

            {error && (
              <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded p-2 mb-4">
                {error}
              </p>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              {/* Contraseña actual */}
              <div>
                <label
                  htmlFor="current-password"
                  className="block text-sm font-medium text-gray-700"
                >
                  Contraseña actual
                </label>
                <input
                  type="password"
                  id="current-password"
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                  required
                  className="mt-1 w-full px-3 py-2 border rounded-lg shadow-sm focus:ring-primary focus:border-primary text-sm"
                />
              </div>

              {/* Nueva contraseña */}
              <div>
                <label
                  htmlFor="new-password"
                  className="block text-sm font-medium text-gray-700"
                >
                  Nueva contraseña
                </label>
                <input
                  type="password"
                  id="new-password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  required
                  className="mt-1 w-full px-3 py-2 border rounded-lg shadow-sm focus:ring-primary focus:border-primary text-sm"
                />
              </div>

              {/* Confirmar contraseña */}
              <div>
                <label
                  htmlFor="confirm-password"
                  className="block text-sm font-medium text-gray-700"
                >
                  Confirmar nueva contraseña
                </label>
                <input
                  type="password"
                  id="confirm-password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                  className="mt-1 w-full px-3 py-2 border rounded-lg shadow-sm focus:ring-primary focus:border-primary text-sm"
                />
              </div>

              {/* Botón enviar */}
              <button
                type="submit"
                className="w-full py-2 px-4 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
              >
                Guardar nueva contraseña
              </button>
            </form>

            <div className="mt-6 text-sm text-center text-gray-600">
              ¿No quieres cambiarla ahora?{" "}
              <Link to="/perfil" className="text-primary hover:underline">
                Volver al perfil
              </Link>
            </div>
          </>
        ) : (
          // Estado de éxito
          <div className="text-center">
            <h1 className="text-2xl font-bold text-neutral-dark mb-2">
              Contraseña actualizada ✅
            </h1>
            <p className="text-sm text-gray-600 mb-6">
              Tu contraseña ha sido cambiada correctamente.
            </p>

            <Link
              to="/perfil"
              className="inline-block px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
            >
              Volver al perfil
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}
