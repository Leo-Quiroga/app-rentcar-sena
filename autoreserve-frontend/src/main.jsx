// Pantalla principal de la aplicación
// Renderiza las rutas definidas en AppRoutes
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from "react-router-dom";

// Importar el componente principal de la aplicación
import App from './App.jsx'
import './styles/index.css'
// Importar el proveedor de autenticación
import { AuthProvider } from "./auth/AuthProvider";
// Renderizar la aplicación dentro del proveedor de autenticación y el enrutador
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <AuthProvider>
    <BrowserRouter>
      <App />
    </BrowserRouter>
    </AuthProvider>
  </StrictMode>,
)

