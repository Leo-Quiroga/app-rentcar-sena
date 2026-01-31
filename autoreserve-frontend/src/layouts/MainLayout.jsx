// Gestión del diseño principal de la aplicación con Header y Footer fijos
// src/layouts/MainLayout.jsx
import Header from "../components/Header";
import Footer from "../components/Footer";

export default function MainLayout({ children }) {
  return (
    <div className="flex flex-col min-h-screen">
      {/* Header fijo arriba */}
      <Header />

      {/* Contenido dinámico */}
      <main
        role="main"
        className="flex-grow container mx-auto px-4 pt-24 pb-12"
      >
        {children}
      </main>

      {/* Footer fijo abajo */}
      <Footer className="mt-auto" />
    </div>
  );
}
