// Pantalla de ruta protegida
// Controla el acceso basado en autenticación y roles de usuario
import { Navigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth";

export default function ProtectedRoute({ children, role }) {
  // Obtener usuario y estado de carga desde el contexto de autenticación
  const { user, loading } = useAuth();
  // Mostrar indicador de carga mientras se verifica la autenticación
  if (loading) {
    return <div>Cargando...</div>;
  }
  // Redirigir si no está autenticado
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  // Verificar rol si se especifica
  if (role && user.role !== role) {
    return <Navigate to="/" replace />;
  }

  return children;
}
