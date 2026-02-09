// Utilidades para manejo de imágenes
export const getImageUrl = (imagePath, type = 'car') => {
  // Debug temporal
  console.log(`getImageUrl called with: "${imagePath}", type: "${type}"`);
  
  // Si no hay imagen, usar placeholder
  if (!imagePath) {
    return getPlaceholderImage(type);
  }

  // Si es una URL completa (http/https), usarla directamente
  if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    return imagePath;
  }

  // Si es una ruta absoluta de Windows (C:\Users\...), convertirla
  if (imagePath.includes('\\') || imagePath.match(/^[A-Z]:/) || imagePath.includes('Desktop')) {
    // Extraer solo la parte desde src/assets hacia adelante
    const match = imagePath.match(/src[\\\/]assets[\\\/].+/);
    if (match) {
      const converted = '/' + match[0].replace(/\\/g, '/');
      console.log(`Windows path converted: "${imagePath}" -> "${converted}"`);
      return converted;
    }
    // Si no encuentra src/assets, pero contiene assets, intentar extraer desde assets
    const assetsMatch = imagePath.match(/assets[\\\/].+/);
    if (assetsMatch) {
      const converted = '/src/' + assetsMatch[0].replace(/\\/g, '/');
      console.log(`Assets path converted: "${imagePath}" -> "${converted}"`);
      return converted;
    }
  }

  // Si ya es una ruta completa de assets, usarla directamente
  if (imagePath.startsWith('/src/assets/')) {
    return imagePath;
  }

  // Si es solo el nombre del archivo, construir la ruta según el tipo
  if (!imagePath.startsWith('/')) {
    const assetPaths = {
      car: `/src/assets/cars/${imagePath}`,
      category: `/src/assets/categories/${imagePath}`,
      branch: `/src/assets/branches/${imagePath}`
    };
    return assetPaths[type] || `/src/assets/${imagePath}`;
  }

  console.log(`getImageUrl result: "${imagePath}"`);
  return imagePath;
};

export const getPlaceholderImage = (type = 'car') => {
  // Usar imágenes placeholder SVG embebidas
  const placeholders = {
    car: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZGRkIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxOCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPkF1dG88L3RleHQ+PC9zdmc+',
    category: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZGRkIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxOCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPkNhdGVnb3LDrWE8L3RleHQ+PC9zdmc+',
    branch: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZGRkIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxOCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPlNlZGU8L3RleHQ+PC9zdmc+'
  };
  
  return placeholders[type] || placeholders.car;
};