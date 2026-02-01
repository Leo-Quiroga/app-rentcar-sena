// Pantalla de inicio de sesión
import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import { useAuth } from "../auth/useAuth";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [loading, setLoading] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});
  
  // Validación de email
  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  // Manejar cambios en campos
  const handleEmailChange = (e) => {
    const value = e.target.value;
    setEmail(value);
    setFieldErrors(prev => ({ ...prev, email: null }));
    setError(null);
    setSuccess(null);
  };

  const handlePasswordChange = (e) => {
    const value = e.target.value;
    setPassword(value);
    setFieldErrors(prev => ({ ...prev, password: null }));
    setError(null);
    setSuccess(null);
  };

  // Manejar envío del formulario
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setFieldErrors({});
    setLoading(true);

    // Validaciones frontend
    const errors = {};
    if (!email.trim()) {
      errors.email = "El correo es requerido";
    } else if (!validateEmail(email)) {
      errors.email = "El formato del correo no es válido";
    }
    
    if (!password.trim()) {
      errors.password = "La contraseña es requerida";
    } else if (password.length < 6) {
      errors.password = "La contraseña debe tener al menos 6 caracteres";
    }

    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors);
      setLoading(false);
      return;
    }

    try {
      const userData = await login(email, password);
      setSuccess("¡Inicio de sesión exitoso! Redirigiendo...");
      
      setTimeout(() => {
        if (userData.role === "ADMIN") {
          navigate("/admin");
        } else {
          navigate("/");
        }
      }, 1500);
    } catch (err) {
      setError(err.message || "Error al iniciar sesión");
    } finally {
      setLoading(false);
    }
  };
  /// Renderizar formulario de inicio de sesión
  return (
    <div className="min-h-screen flex items-center justify-center bg-neutral-light px-4">
      <div className="w-full max-w-md bg-white rounded-lg shadow-md p-8">
        <h1 className="text-2xl font-bold text-center text-neutral-dark mb-6">
          Iniciar sesión
        </h1>

        <form className="space-y-4" onSubmit={handleSubmit}>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Correo electrónico
            </label>
            <input
              type="text"
              className={`w-full border rounded-lg px-3 py-2 transition-colors ${
                fieldErrors.email 
                  ? "border-red-500 focus:border-red-500 focus:ring-red-200" 
                  : "border-gray-300 focus:border-primary focus:ring-primary/20"
              } focus:ring-2 focus:outline-none`}
              value={email}
              onChange={handleEmailChange}
              placeholder="ejemplo@correo.com"
              disabled={loading}
            />
            {fieldErrors.email && (
              <p className="text-sm text-red-600 mt-1 flex items-center gap-1">
                <span>⚠️</span> {fieldErrors.email}
              </p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Contraseña
            </label>
            <input
              type="password"
              className={`w-full border rounded-lg px-3 py-2 transition-colors ${
                fieldErrors.password 
                  ? "border-red-500 focus:border-red-500 focus:ring-red-200" 
                  : "border-gray-300 focus:border-primary focus:ring-primary/20"
              } focus:ring-2 focus:outline-none`}
              value={password}
              onChange={handlePasswordChange}
              placeholder="Mínimo 6 caracteres"
              disabled={loading}
            />
            {fieldErrors.password && (
              <p className="text-sm text-red-600 mt-1 flex items-center gap-1">
                <span>⚠️</span> {fieldErrors.password}
              </p>
            )}
          </div>

          {/* Mensajes de estado */}
          {error && (
            <div className="bg-red-50 border border-red-200 rounded-lg p-3">
              <p className="text-sm text-red-700 flex items-center gap-2">
                <span>❌</span> {error}
              </p>
            </div>
          )}
          
          {success && (
            <div className="bg-green-50 border border-green-200 rounded-lg p-3">
              <p className="text-sm text-green-700 flex items-center gap-2">
                <span>✅</span> {success}
              </p>
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className={`w-full py-3 px-4 rounded-lg font-medium transition-all ${
              loading
                ? "bg-gray-400 cursor-not-allowed"
                : "bg-primary hover:bg-primary-dark text-white shadow-lg hover:shadow-xl"
            }`}
          >
            {loading ? (
              <span className="flex items-center justify-center gap-2">
                <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                Iniciando sesión...
              </span>
            ) : (
              "Iniciar sesión"
            )}
          </button>
        </form>

        <div className="mt-6 flex justify-between text-sm text-gray-600">
          <Link to="/reset-password">¿Olvidaste tu contraseña?</Link>
          <Link to="/register">Crear cuenta</Link>
        </div>
      </div>
    </div>
  );
}
