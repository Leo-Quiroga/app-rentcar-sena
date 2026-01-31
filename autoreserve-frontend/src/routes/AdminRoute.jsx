//Pantalla de ruta de administrador
import { Navigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth";

export default function AdminRoute({ children }) {
  // Obtener usuario y estado de carga desde el contexto de autenticación
  const { user, loading } = useAuth();
  // Mostrar indicador de carga mientras se verifica la autenticación
  if (loading) {
    return <div>Cargando...</div>;
  }
  // Redirigir si no está autenticado o no es admin
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  // Verificar rol de administrador
  if (user.role !== "ADMIN") {
    return <Navigate to="/" replace />;
  }
  // Renderizar contenido protegido para administradores
  return children;
}
