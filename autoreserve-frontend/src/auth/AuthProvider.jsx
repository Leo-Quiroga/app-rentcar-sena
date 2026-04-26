import { useState, useEffect } from "react";
import { AuthContext } from "./AuthContext";
import { loginRequest } from "./authApi";
import { getMyProfile } from "../api/userApi";

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
        if (parsedUser && parsedUser.email && parsedUser.id) {
          setUser(parsedUser);
        } else {
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
      token: data.token,
      firstName: "",
      lastName: "",
    };

    if (!authUser.email || !authUser.id) {
      throw new Error("Datos de usuario incompletos del servidor");
    }

    // Guardar token primero para que getMyProfile pueda autenticarse
    localStorage.setItem("auth", JSON.stringify(authUser));
    setUser(authUser);

    // Enriquecer con nombre completo desde el perfil
    try {
      const profile = await getMyProfile();
      authUser.firstName = profile.firstName || "";
      authUser.lastName = profile.lastName || "";
      localStorage.setItem("auth", JSON.stringify(authUser));
      setUser({ ...authUser });
    } catch {
      // Si falla, continúa sin nombre — no es bloqueante
    }

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
