import { useContext } from "react";
import { AuthContext } from "./AuthContext";

// Hook personalizado para usar el contexto de autenticación
export function useAuth() {
  return useContext(AuthContext);
}

// Exportar también como default
export default useAuth;
