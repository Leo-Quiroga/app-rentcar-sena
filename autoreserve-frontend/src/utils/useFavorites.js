import { useFavoritesContext } from '../contexts/FavoritesContext.jsx';

/**
 * Hook personalizado para manejar el estado de favoritos
 * Ahora usa Context API para estado global profesional
 * Mantiene compatibilidad con la API anterior
 */
export function useFavorites() {
  return useFavoritesContext();
}