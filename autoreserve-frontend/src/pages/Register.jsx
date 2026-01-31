// Página de registro de nuevos usuarios
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

export default function Register() {
  // Estado para capturar los datos del formulario
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
    phone: "",
  });
  // Hook de navegación
  const navigate = useNavigate();
  // Estado para manejar errores
  const [error, setError] = useState("");
  // Manejo de cambios en los campos del formulario
  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.id]: e.target.value });
  };
  // Manejo del envío del formulario
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    // 1. Validación de contraseñas en el Front
    if (formData.password !== formData.confirmPassword) {
      setError("Las contraseñas no coinciden");
      return;
    }

    // 2. Preparar JSON según pide el Back (sin confirmPassword)
    const dataToSend = {
      firstName: formData.firstName,
      lastName: formData.lastName,
      email: formData.email,
      password: formData.password,
      phone: formData.phone,
    };
    // 3. Enviar datos al backend
    try {
      const response = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dataToSend),
      });
      // 4. Manejar respuesta del backend
      if (response.ok) {
        alert("Registro exitoso");
        // lImpiar el formulario y redirigir al login
        if (response.ok) {
          // 1. Limpiar formulario
          setFormData({
            firstName: "",
            lastName: "",
            email: "",
            password: "",
            confirmPassword: "",
            phone: "",
          });

          // 2. Redirigir al login
          navigate("/login");
        }
      } else {
        setError("Error en el registro. Intenta de nuevo.");
      }
    } catch (err) {
      setError("Error en el registro. Intenta de nuevo. " + err.message);
    }
  };
  // Renderizado del formulario de registro
  return (
    <div className="min-h-screen flex items-center justify-center bg-neutral-light px-4">
      <div className="w-full max-w-md bg-white rounded-lg shadow-md p-8">
        <h1 className="text-2xl font-bold text-center text-neutral-dark mb-6">
          Crear cuenta
        </h1>

        {error && (
          <p className="text-red-500 text-sm mb-4 text-center">{error}</p>
        )}

        <form className="space-y-4" onSubmit={handleSubmit}>
          {/* Nombre y Apellido en una fila */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label
                htmlFor="firstName"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Nombres
              </label>
              <input
                type="text"
                id="firstName"
                value={formData.firstName}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
                required
              />
            </div>
            <div>
              <label
                htmlFor="lastName"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Apellidos
              </label>
              <input
                type="text"
                id="lastName"
                value={formData.lastName}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
                required
              />
            </div>
          </div>

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
              value={formData.email}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
              placeholder="tu@email.com"
              required
            />
          </div>

          <div>
            <label
              htmlFor="phone"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Teléfono
            </label>
            <input
              type="text"
              id="phone"
              value={formData.phone}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
              placeholder="321651516"
              required
            />
          </div>

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
              value={formData.password}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
              placeholder="********"
              required
            />
          </div>

          <div>
            <label
              htmlFor="confirmPassword"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Confirmar contraseña
            </label>
            <input
              type="password"
              id="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
              placeholder="********"
              required
            />
          </div>

          <button
            type="submit"
            className="w-full py-2 px-4 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
          >
            Registrarse
          </button>
        </form>

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
