import { useState } from 'react';
import { useAuth } from '../auth/useAuth';
import { addToFavorites, removeFromFavorites } from '../api/favoritesApi';
import { useNavigate } from 'react-router-dom';
import { useFavorites } from '../utils/useFavorites';

/**
 * Botón de favorito con corazón animado y toggle completo
 * Ahora integrado con Context API para actualizaciones automáticas
 * @param {Object} props
 * @param {number} props.carModelId - ID del modelo de auto
 * @param {string} props.size - Tamaño del botón ('sm', 'md', 'lg')
 * @param {string} props.className - Clases CSS adicionales
 */
export default function FavoriteButton({ 
  carModelId, 
  size = 'md',
  className = '' 
}) {
  const [isLoading, setIsLoading] = useState(false);
  const { user } = useAuth();
  const { isFavorite, updateFavorite } = useFavorites();
  const navigate = useNavigate();
  
  // Obtener estado actual desde el contexto global
  const currentFavorite = isFavorite(carModelId);

  const sizeClasses = {
    sm: 'w-6 h-6 text-sm',
    md: 'w-8 h-8 text-base',
    lg: 'w-10 h-10 text-lg'
  };

  const handleToggle = async (e) => {
    e.preventDefault();
    e.stopPropagation();
    
    // Si no hay usuario, redirigir a login
    if (!user) {
      navigate('/login');
      return;
    }
    
    if (isLoading) return;
    
    setIsLoading(true);
    
    try {
      let newFavoriteState;
      
      if (currentFavorite) {
        // Quitar de favoritos
        await removeFromFavorites(carModelId);
        newFavoriteState = false;
        console.log('Modelo removido de favoritos:', carModelId);
      } else {
        // Agregar a favoritos
        await addToFavorites(carModelId);
        newFavoriteState = true;
        console.log('Modelo agregado a favoritos:', carModelId);
      }
      
      // Actualizar estado global (esto actualizará automáticamente el Header)
      updateFavorite(carModelId, newFavoriteState);
      
    } catch (error) {
      console.error('Error toggling favorite:', error);
      
      // Manejar diferentes tipos de errores
      if (error.status === 401) {
        // No autenticado - redirigir a login
        navigate('/login');
      } else if (error.message?.includes('ya está en')) {
        // Ya es favorito - intentar quitar
        try {
          await removeFromFavorites(carModelId);
          updateFavorite(carModelId, false);
        } catch (removeError) {
          alert('Error al actualizar favoritos. Intenta recargar la página.' + removeError.message);
        }
      } else if (error.message?.includes('no está en')) {
        // No es favorito - intentar agregar
        try {
          await addToFavorites(carModelId);
          updateFavorite(carModelId, true);
        } catch (addError) {
          alert('Error al actualizar favoritos. Intenta recargar la página.' + addError.message);
        }
      } else {
        // Otros errores
        alert(error.message || 'Error al actualizar favoritos');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <button
      onClick={handleToggle}
      disabled={isLoading}
      className={`
        ${sizeClasses[size]}
        flex items-center justify-center
        rounded-full
        transition-all duration-200
        hover:scale-110
        focus:outline-none focus:ring-2 focus:ring-yellow-300
        ${currentFavorite 
          ? 'bg-yellow-500 text-white shadow-lg hover:bg-yellow-600' 
          : 'bg-white text-gray-400 shadow-md hover:text-yellow-500 hover:bg-yellow-50'
        }
        ${isLoading ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'}
        ${className}
      `}
      title={currentFavorite ? 'Quitar de favoritos' : 'Agregar a favoritos'}
    >
      {isLoading ? (
        <div className="animate-spin text-xs">⏳</div>
      ) : (
        <span className={`transition-transform duration-200 ${currentFavorite ? 'scale-110' : ''}`}>
          {currentFavorite ? '⭐' : '☆'}
        </span>
      )}
    </button>
  );
}