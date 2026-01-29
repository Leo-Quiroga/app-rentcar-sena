import { useState, useEffect } from "react";
import { AuthContext } from "./AuthContext";
import { loginRequest } from "./authApi";

// Proveedor de contexto de autenticación
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Al cargar la app, recuperar sesión
  useEffect(() => {
    const stored = localStorage.getItem("auth");
    if (stored) {
      setUser(JSON.parse(stored));
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
