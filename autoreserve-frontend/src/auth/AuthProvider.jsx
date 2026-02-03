import { useState, useEffect } from "react";
import { AuthContext } from "./AuthContext";
import { loginRequest } from "./authApi";

// Proveedor de contexto de autenticación
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Al cargar la app, recuperar sesión
  useEffect(() => {
    try {
      const stored = localStorage.getItem("auth");
      if (stored) {
        const parsedUser = JSON.parse(stored);
        // Validar que el objeto tenga las propiedades necesarias
        if (parsedUser && parsedUser.email && parsedUser.id) {
          setUser(parsedUser);
        } else {
          // Si los datos están corruptos, limpiar localStorage
          localStorage.removeItem("auth");
        }
      }
    } catch (error) {
      console.error("Error parsing auth data:", error);
      localStorage.removeItem("auth");
    }
    setLoading(false);
  }, []);
  
  // Función para iniciar sesión
  const login = async (email, password) => {
    const data = await loginRequest(email, password);

    const authUser = {
      id: data.userId,
      email: data.email,
      role: data.role,
      token: data.token
    };

    // Validar que los datos del backend sean correctos
    if (!authUser.email || !authUser.id) {
      throw new Error("Datos de usuario incompletos del servidor");
    }

    localStorage.setItem("auth", JSON.stringify(authUser));
    setUser(authUser);
    return authUser;
  };

  // Función para cerrar sesión
  const logout = () => {
    localStorage.removeItem("auth");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
}
