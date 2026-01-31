// Pantalla para editar el perfil del usuario
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiFetch } from "../api/http";

export default function EditProfile() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState(null);
  const [saving, setSaving] = useState(false);
  // Cargar datos del perfil al montar el componente
  useEffect(() => {
    const loadProfile = async () => {
      try {
        const data = await apiFetch("/api/profile/me");
        setFormData({
          firstName: data.firstName || "",
          lastName: data.lastName || "",
          email: data.email || "",
          phone: data.phone || "",
          address: data.address || "",
          city: data.city || "",
          birthDate: data.birthDate || "",
          drivingLicense: data.drivingLicense || ""
        });
      } catch (err) {
        console.error("Error cargando perfil", err);
      }
    };

    loadProfile();
  }, []);
  // Manejar cambios en los campos del formulario
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };
  // Manejar envío del formulario
  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);

    try {
      await apiFetch("/api/profile/me", {
        method: "PUT",
        body: JSON.stringify(formData)
      });
      navigate("/perfil");
    } catch (err) {
      console.error("Error actualizando perfil", err);
    } finally {
      setSaving(false);
    }
  };
  // Si los datos no están cargados, mostramos un loader
  if (!formData) {
    return (
      <div className="min-h-[60vh] flex items-center justify-center">
        <div className="flex flex-col items-center gap-2">
          <div className="w-6 h-6 border-2 border-primary/30 border-t-primary rounded-full animate-spin"></div>
          <p className="text-gray-400 text-xs font-bold uppercase tracking-widest">Cargando...</p>
        </div>
      </div>
    );
  }
  // Renderizar formulario de edición de perfil
  return (
    <div className="bg-neutral-light min-h-screen">
      <div className="max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
        
        {/* Encabezado */}
        <header className="mb-10 text-center md:text-left">
          <h1 className="text-3xl font-bold text-neutral-dark tracking-tight">Editar Perfil</h1>
          <p className="text-gray-500 mt-2 text-sm lg:text-base">
            Modifica tus datos personales para mantener tu cuenta actualizada.
          </p>
        </header>

        <form onSubmit={handleSubmit} className="space-y-6">
          
          {/* Tarjeta 1: Información Básica */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
            <div className="bg-gray-50/50 px-6 py-4 border-b border-gray-100">
              <h2 className="text-[11px] font-black text-primary uppercase tracking-[0.2em]">Datos de Identidad</h2>
            </div>
            
            <div className="p-6 sm:p-8 grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="flex flex-col gap-1.5">
                <label className="text-[10px] font-bold text-gray-400 uppercase ml-1">Nombre</label>
                <input
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  placeholder="Tu nombre"
                  className="w-full border border-gray-200 rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none transition-all font-medium"
                  required
                />
              </div>

              <div className="flex flex-col gap-1.5">
                <label className="text-[10px] font-bold text-gray-400 uppercase ml-1">Apellido</label>
                <input
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  placeholder="Tu apellido"
                  className="w-full border border-gray-200 rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none transition-all font-medium"
                  required
                />
              </div>

              <div className="flex flex-col gap-1.5 md:col-span-2 lg:col-span-1">
                <label className="text-[10px] font-bold text-gray-400 uppercase ml-1">Email (No editable)</label>
                <input
                  name="email"
                  value={formData.email}
                  disabled
                  className="w-full border border-gray-200 rounded-xl px-4 py-3 text-sm bg-gray-50 text-gray-400 cursor-not-allowed font-medium italic"
                />
              </div>

              <div className="flex flex-col gap-1.5">
                <label className="text-[10px] font-bold text-gray-400 uppercase ml-1">Teléfono</label>
                <input
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                  placeholder="Ej: +57 300 123 4567"
                  className="w-full border border-gray-200 rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none transition-all font-medium"
                />
              </div>
            </div>
          </div>

          {/* Tarjeta 2: Ubicación y Documentación */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
            <div className="bg-gray-50/50 px-6 py-4 border-b border-gray-100">
              <h2 className="text-[11px] font-black text-primary uppercase tracking-[0.2em]">Ubicación y Conducción</h2>
            </div>
            
            <div className="p-6 sm:p-8 grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="flex flex-col gap-1.5">
                <label className="text-[10px] font-bold text-gray-400 uppercase ml-1">Dirección</label>
                <input
                  name="address"
                  value={formData.address}
                  onChange={handleChange}
                  placeholder="Calle, número, apto"
                  className="w-full border border-gray-200 rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none transition-all font-medium"
                />
              </div>

              <div className="flex flex-col gap-1.5">
                <label className="text-[10px] font-bold text-gray-400 uppercase ml-1">Ciudad</label>
                <input
                  name="city"
                  value={formData.city}
                  onChange={handleChange}
                  placeholder="Ciudad de residencia"
                  className="w-full border border-gray-200 rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none transition-all font-medium"
                />
              </div>

              <div className="flex flex-col gap-1.5">
                <label className="text-[10px] font-bold text-gray-400 uppercase ml-1">Fecha de nacimiento</label>
                <input
                  type="date"
                  name="birthDate"
                  value={formData.birthDate}
                  onChange={handleChange}
                  className="w-full border border-gray-200 rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none transition-all font-medium"
                />
              </div>

              <div className="flex flex-col gap-1.5">
                <label className="text-[10px] font-bold text-gray-400 uppercase ml-1">Licencia de conducción</label>
                <input
                  name="drivingLicense"
                  value={formData.drivingLicense}
                  onChange={handleChange}
                  placeholder="Número de licencia"
                  className="w-full border border-gray-200 rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none transition-all font-medium uppercase tracking-wider"
                />
              </div>
            </div>
          </div>

          {/* Botones de Acción */}
          <div className="flex flex-col sm:flex-row justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={() => navigate("/perfil")}
              className="w-full sm:w-auto px-8 py-3 bg-white border border-gray-200 text-gray-500 font-bold rounded-xl text-xs uppercase tracking-widest hover:bg-gray-50 transition-all"
            >
              Cancelar
            </button>

            <button
              type="submit"
              disabled={saving}
              className="w-full sm:w-auto px-12 py-3 bg-primary text-white font-bold rounded-xl text-xs uppercase tracking-widest shadow-lg shadow-primary/20 hover:brightness-95 active:scale-95 transition-all disabled:opacity-50"
            >
              {saving ? "Guardando..." : "Guardar cambios"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}