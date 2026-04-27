import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { getFavoriteIds } from '../api/favoritesApi';
import { useAuth } from '../auth/useAuth';

/**
 * Context para manejar el estado global de favoritos
 * Implementación profesional con Context API
 */
const FavoritesContext = createContext();

/**
 * Provider del contexto de favoritos
 * Maneja el estado global y las operaciones de favoritos
 */
export function FavoritesProvider({ children }) {
  const [favoriteIds, setFavoriteIds] = useState(new Set());
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const { user } = useAuth();

  // Cargar favoritos desde la API
  const loadFavorites = useCallback(async () => {
    if (!user) {
      setFavoriteIds(new Set());
      setLoading(false);
      setError(null);
      return;
    }

    try {
      setLoading(true);
      setError(null);
      
      const response = await getFavoriteIds();
      
      if (response && response.success) {
        setFavoriteIds(new Set(response.favoriteIds || []));
      } else if (response && Array.isArray(response.favoriteIds)) {
        setFavoriteIds(new Set(response.favoriteIds));
      } else {
        setError(response?.error || 'Error cargando favoritos');
        setFavoriteIds(new Set());
      }
    } catch (err) {
      console.error('Error loading favorites:', err);
      setError(err.message || 'Error de conexión');
      setFavoriteIds(new Set());
    } finally {
      setLoading(false);
    }
  }, [user]);

  // Cargar favoritos cuando cambie el usuario
  useEffect(() => {
    loadFavorites();
  }, [loadFavorites]);

  // Verificar si un modelo es favorito
  const isFavorite = useCallback((carModelId) => {
    return favoriteIds.has(carModelId);
  }, [favoriteIds]);

  // Agregar modelo a favoritos (optimistic update)
  const addFavorite = useCallback((carModelId) => {
    setFavoriteIds(prev => new Set([...prev, carModelId]));
  }, []);

  // Remover modelo de favoritos (optimistic update)
  const removeFavorite = useCallback((carModelId) => {
    setFavoriteIds(prev => {
      const newSet = new Set(prev);
      newSet.delete(carModelId);
      return newSet;
    });
  }, []);

  // Toggle favorito (optimistic update)
  const toggleFavorite = useCallback((carModelId) => {
    if (favoriteIds.has(carModelId)) {
      removeFavorite(carModelId);
      return false;
    } else {
      addFavorite(carModelId);
      return true;
    }
  }, [favoriteIds, addFavorite, removeFavorite]);

  // Actualizar favorito desde componente externo
  const updateFavorite = useCallback((carModelId, isFavoriteValue) => {
    if (isFavoriteValue) {
      addFavorite(carModelId);
    } else {
      removeFavorite(carModelId);
    }
  }, [addFavorite, removeFavorite]);

  // Recargar favoritos desde el servidor
  const refreshFavorites = useCallback(() => {
    loadFavorites();
  }, [loadFavorites]);

  // Valor del contexto
  const contextValue = {
    // Estado
    favoriteIds: Array.from(favoriteIds),
    favoriteIdsSet: favoriteIds,
    loading,
    error,
    count: favoriteIds.size,
    isAuthenticated: !!user,
    
    // Funciones
    isFavorite,
    addFavorite,
    removeFavorite,
    toggleFavorite,
    updateFavorite,
    refreshFavorites
  };

  return (
    <FavoritesContext.Provider value={contextValue}>
      {children}
    </FavoritesContext.Provider>
  );
}

/**
 * Hook personalizado para usar el contexto de favoritos
 * Lanza error si se usa fuera del Provider
 */
export function useFavoritesContext() {
  const context = useContext(FavoritesContext);
  
  if (!context) {
    throw new Error('useFavoritesContext must be used within a FavoritesProvider');
  }
  
  return context;
}

export default FavoritesContext;