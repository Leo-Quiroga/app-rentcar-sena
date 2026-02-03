// Pantalla de rutas de la aplicación
// Define todas las rutas principales, de usuario y de administración
import { Routes, Route } from "react-router-dom";
import MainLayout from "../layouts/MainLayout";

import ProtectedRoute from "./ProtectedRoute";
import AdminRoute from "./AdminRoute";

// Páginas principales
import Home from "../pages/Home";
import Categories from "../pages/Categories";
import Sedes from "../pages/Sedes";
import CategoryDetail from "../pages/CategoryDetail";

// Autenticación
import Login from "../pages/Login";
import Register from "../pages/Register";
import ResetPassword from "../pages/ResetPassword";
import VerifyEmail from "../pages/VerifyEmail";

// Usuario
import Profile from "../pages/Profile";
import Favorites from "../pages/Favorites";
import Reservations from "../pages/Reservations";
import ReservationDetail from "../pages/ReservationDetail";
import Checkout from "../pages/Checkout";
import ReservationConfirmation from "../pages/ReservationConfirmation";
import PaymentFailed from "../pages/PaymentFailed";
import Invoice from "../pages/Invoice";
import ChangePassword from "../pages/ChangePassword";
import EditProfile from "../pages/EditProfile";

// Administración
import AdminDashboard from "../pages/AdminDashboard";
import AdminAutos from "../pages/AdminAutos";
import AdminAutoForm from "../pages/AdminAutoForm";
import AdminAutoCalendar from "../pages/AdminAutoCalendar";
import AdminCategories from "../pages/AdminCategories";
import AdminCategoryForm from "../pages/AdminCategoryForm";
import AdminUsers from "../pages/AdminUsers";
import AdminUserForm from "../pages/AdminUserForm";
import AdminSedes from "../pages/AdminSedes";
import AdminSedeForm from "../pages/AdminSedeForm";

// Soporte y legales
import Contact from "../pages/Contact";
import Policies from "../pages/Policies";
import SupportTicket from "../pages/SupportTicket";

// Error
import Error404 from "../pages/Error404";

export default function AppRoutes() {
  return (
    <Routes>
      {/* ========= Rutas principales ========= */}
      <Route
        path="/"
        element={
          <MainLayout>
            <Home />
          </MainLayout>
        }
      />
      <Route
        path="/categorias"
        element={
          <MainLayout>
            <Categories />
          </MainLayout>
        }
      />
      <Route
        path="/categorias/:id"
        element={
          <MainLayout>
            <CategoryDetail />
          </MainLayout>
        }
      />
      <Route
        path="/sedes"
        element={
          <MainLayout>
            <Sedes />
          </MainLayout>
        }
      />

      {/* ========= Autenticación ========= */}
      <Route
        path="/login"
        element={
          <MainLayout>
            <Login />
          </MainLayout>
        }
      />
      <Route
        path="/register"
        element={
          <MainLayout>
            <Register />
          </MainLayout>
        }
      />
      <Route
        path="/reset-password"
        element={
          <MainLayout>
            <ResetPassword />
          </MainLayout>
        }
      />
      <Route
        path="/verify-email"
        element={
          <MainLayout>
            <VerifyEmail />
          </MainLayout>
        }
      />

      {/* ========= Usuario ========= */}
      <Route
        path="/perfil"
        element={
          <ProtectedRoute>
            <MainLayout>
              <Profile />
            </MainLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/perfil/editar"
        element={
          <MainLayout>
            <EditProfile />
          </MainLayout>
        }
      />
      <Route
        path="/favoritos"
        element={
          <ProtectedRoute>
            <MainLayout>
              <Favorites />
            </MainLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/reservas"
        element={
          <ProtectedRoute>
            <MainLayout>
              <Reservations />
            </MainLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/reservas/:id"
        element={
          <ProtectedRoute>
            <MainLayout>
              <ReservationDetail />
            </MainLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/reservas/checkout"
        element={
          <ProtectedRoute>
            <MainLayout>
              <Checkout />
            </MainLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/reservas/confirmacion"
        element={
          <ProtectedRoute>
            <MainLayout>
              <ReservationConfirmation />
            </MainLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/reservas/pago-fallido"
        element={
          <ProtectedRoute>
            <MainLayout>
              <PaymentFailed />
            </MainLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/factura/:id"
        element={
          <ProtectedRoute>
            <MainLayout>
              <Invoice />
            </MainLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/perfil/cambiar-password"
        element={
          <ProtectedRoute>
            <MainLayout>
              <ChangePassword />
            </MainLayout>
          </ProtectedRoute>
        }
      />

      {/* ========= Administración ========= */}
      <Route
        path="/admin"
        element={
          <AdminRoute>
            <MainLayout>
              <AdminDashboard />
            </MainLayout>
          </AdminRoute>
        }
      />
      <Route
        path="/admin/autos"
        element={
          <AdminRoute>
            <MainLayout>
              <AdminAutos />
            </MainLayout>
          </AdminRoute>
        }
      />
      <Route
        path="/admin/autos/nuevo"
        element={
          <MainLayout>
            <AdminAutoForm />
          </MainLayout>
        }
      />
      <Route
        path="/admin/autos/editar/:id"
        element={
          <MainLayout>
            <AdminAutoForm />
          </MainLayout>
        }
      />
      <Route
        path="/admin/autos/:id/calendario"
        element={
          <MainLayout>
            <AdminAutoCalendar />
          </MainLayout>
        }
      />
      <Route
        path="/admin/categorias"
        element={
          <AdminRoute>
            <MainLayout>
              <AdminCategories />
            </MainLayout>
          </AdminRoute>
        }
      />
      <Route
        path="/admin/categorias/nueva"
        element={
          <MainLayout>
            <AdminCategoryForm />
          </MainLayout>
        }
      />
      <Route
        path="/admin/categorias/:id/editar"
        element={
          <MainLayout>
            <AdminCategoryForm />
          </MainLayout>
        }
      />
      <Route
        path="/admin/usuarios"
        element={
          <AdminRoute>
            <MainLayout>
              <AdminUsers />
            </MainLayout>
          </AdminRoute>
        }
      />
      <Route
        path="/admin/usuarios/nuevo"
        element={
          <MainLayout>
            <AdminUserForm />
          </MainLayout>
        }
      />
      <Route
        path="/admin/usuarios/:id/editar"
        element={
          <MainLayout>
            <AdminUserForm />
          </MainLayout>
        }
      />
      <Route
        path="/admin/sedes"
        element={
          <AdminRoute>
            <MainLayout>
              <AdminSedes />
            </MainLayout>
          </AdminRoute>
        }
      />
      <Route
        path="/admin/sedes/nuevo"
        element={
          <MainLayout>
            <AdminSedeForm />
          </MainLayout>
        }
      />
      <Route
        path="/admin/sedes/:id/editar"
        element={
          <MainLayout>
            <AdminSedeForm />
          </MainLayout>
        }
      />

      {/* ========= Soporte y legales ========= */}
      <Route
        path="/contacto"
        element={
          <MainLayout>
            <Contact />
          </MainLayout>
        }
      />
      <Route
        path="/politicas"
        element={
          <MainLayout>
            <Policies />
          </MainLayout>
        }
      />
      <Route
        path="/soporte/ticket"
        element={
          <MainLayout>
            <SupportTicket />
          </MainLayout>
        }
      />

      {/* ========= Error ========= */}
      <Route
        path="*"
        element={
          <MainLayout>
            <Error404 />
          </MainLayout>
        }
      />
    </Routes>
  );
}
