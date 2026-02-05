import { createContext, useContext } from "react";

// Contexto de autenticación
export const AuthContext = createContext(null);

// Hook personalizado para usar el contexto de autenticación
export function useAuth() {
  return useContext(AuthContext);
}
