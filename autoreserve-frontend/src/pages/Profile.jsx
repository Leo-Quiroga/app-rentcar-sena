// import { useEffect, useState } from "react";
// import { useNavigate } from "react-router-dom";
// import { apiFetch } from "../api/http";

// export default function Profile() {
//   const navigate = useNavigate();
//   const [profile, setProfile] = useState(null);
//   const [loading, setLoading] = useState(true);

//   const formatDate = (isoDate) => {
//     if (!isoDate) return "—";
//     const date = new Date(isoDate);
//     return date.toLocaleDateString("es-CO", {
//       day: "2-digit",
//       month: "2-digit",
//       year: "numeric",
//     });
//   };

//   useEffect(() => {
//     const loadProfile = async () => {
//       try {
//         const data = await apiFetch("/api/profile/me");
//         setProfile(data);
//       } catch (err) {
//         console.error("Error cargando perfil", err);
//       } finally {
//         setLoading(false);
//       }
//     };

//     loadProfile();
//   }, []);

//   if (loading) {
//     return <p className="p-6">Cargando perfil...</p>;
//   }

//   if (!profile) {
//     return <p className="p-6 text-red-500">No se pudo cargar el perfil</p>;
//   }

//   return (
//     <div className="max-w-3xl mx-auto py-10 px-4 space-y-4">
//       <h1 className="text-2xl font-bold">Mi Perfil</h1>

//       <div className="bg-white p-6 rounded shadow space-y-2">
//         <p>
//           <strong>Nombre:</strong> {profile.firstName} {profile.lastName}
//         </p>
//         <p>
//           <strong>Email:</strong> {profile.email}
//         </p>
//         <p>
//           <strong>Teléfono:</strong> {profile.phone || "—"}
//         </p>
//         <p>
//           <strong>Ciudad:</strong> {profile.city || "—"}
//         </p>
//         <p>
//           <strong>Dirección:</strong> {profile.address || "—"}
//         </p>
//         <p>
//           <strong>Fecha de nacimiento:</strong> {profile.birthDate || "—"}
//         </p>
//         <p>
//           <strong>Licencia de conducción:</strong>{" "}
//           {profile.drivingLicense || "—"}
//         </p>
//         <p>
//           <strong>Inscrito desde:</strong> {formatDate(profile.createdAt)}
//         </p>

//         <button
//           onClick={() => navigate("/perfil/editar")}
//           className="mt-4 px-4 py-2 bg-primary text-white rounded"
//         >
//           Editar perfil
//         </button>
//       </div>
//     </div>
//   );
// }

import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiFetch } from "../api/http";

export default function Profile() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  // Mantenemos tu lógica de formateo intacta
  const formatDate = (isoDate) => {
    if (!isoDate) return "—";
    const date = new Date(isoDate);
    return date.toLocaleDateString("es-CO", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  useEffect(() => {
    const loadProfile = async () => {
      try {
        const data = await apiFetch("/api/profile/me");
        setProfile(data);
      } catch (err) {
        console.error("Error cargando perfil", err);
      } finally {
        setLoading(false);
      }
    };
    loadProfile();
  }, []);

  if (loading) {
    return (
      <div className="min-h-[60vh] flex items-center justify-center">
        <div className="flex flex-col items-center gap-3">
          <div className="w-8 h-8 border-4 border-primary/20 border-t-primary rounded-full animate-spin"></div>
          <p className="text-primary font-bold text-xs uppercase tracking-widest">Cargando perfil...</p>
        </div>
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="min-h-[60vh] flex items-center justify-center px-4">
        <p className="text-red-500 bg-red-50 px-4 py-2 rounded-lg border border-red-100 shadow-sm">
          No se pudo cargar el perfil
        </p>
      </div>
    );
  }

  return (
    <div className="bg-neutral-light min-h-screen">
      <div className="max-w-5xl mx-auto py-12 px-4 sm:px-6 lg:px-8 space-y-8">
        
        {/* Header con Acción Principal */}
        <header className="flex flex-col md:flex-row justify-between items-center border-b border-gray-200 pb-8 gap-4 text-center md:text-left">
          <div>
            <h1 className="text-3xl font-bold text-neutral-dark tracking-tight">Mi Perfil</h1>
            <p className="text-gray-500 mt-1 text-sm lg:text-base italic">Gestiona tu información de conductor y cuenta.</p>
          </div>
          <button
            onClick={() => navigate("/perfil/editar")}
            className="w-full md:w-auto px-8 py-3 bg-primary text-white font-bold rounded-lg shadow-md hover:brightness-95 active:scale-95 transition-all uppercase text-xs tracking-widest"
          >
            Editar perfil
          </button>
        </header>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          
          {/* Tarjeta de Identidad (Lateral) */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 text-center sticky top-6">
              <div className="w-24 h-24 bg-primary text-white rounded-full mx-auto flex items-center justify-center text-4xl mb-4 font-black shadow-inner">
                {profile.firstName?.charAt(0)}{profile.lastName?.charAt(0)}
              </div>
              <h2 className="text-xl font-bold text-neutral-dark uppercase tracking-tighter">
                {profile.firstName} {profile.lastName}
              </h2>
              <p className="text-sm text-gray-500 font-medium mb-6 break-all">{profile.email}</p>
              
              <div className="pt-6 border-t border-gray-50">
                <p className="text-[10px] font-black text-primary uppercase tracking-[0.2em] leading-none mb-2">Inscrito desde</p>
                <p className="text-sm font-bold text-neutral-dark">
                  {formatDate(profile.createdAt)}
                </p>
              </div>
            </div>
          </div>

          {/* Bloques de Información (Principal) */}
          <div className="lg:col-span-2 space-y-6">
            
            {/* Sección: Información Personal */}
            <section className="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
              <div className="bg-gray-50 px-6 py-3 border-b border-gray-100">
                <h3 className="text-[11px] font-black text-gray-400 uppercase tracking-widest">Detalles del Usuario</h3>
              </div>
              <div className="p-6 sm:p-8 grid grid-cols-1 md:grid-cols-2 gap-y-8 gap-x-12">
                <InfoRow label="Nombre Completo" value={`${profile.firstName} ${profile.lastName}`} />
                <InfoRow label="Email Registrado" value={profile.email} />
                <InfoRow label="Teléfono" value={profile.phone} />
                <InfoRow label="Fecha de Nacimiento" value={profile.birthDate} />
              </div>
            </section>

            {/* Sección: Ubicación y Licencia */}
            <section className="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
              <div className="bg-gray-50 px-6 py-3 border-b border-gray-100">
                <h3 className="text-[11px] font-black text-gray-400 uppercase tracking-widest">Ubicación y Conducción</h3>
              </div>
              <div className="p-6 sm:p-8 grid grid-cols-1 md:grid-cols-2 gap-y-8 gap-x-12">
                <InfoRow label="Ciudad" value={profile.city} />
                <InfoRow label="Dirección de Residencia" value={profile.address} />
                <InfoRow label="Licencia de Conducción" value={profile.drivingLicense} isMono />
              </div>
            </section>

            {/* Accesos Rápidos Estilo Tarjetas */}
            <div className="grid grid-cols-2 sm:grid-cols-3 gap-4 pt-4">
              <QuickLink label="Mis Reservas" path="/reservas" />
              <QuickLink label="Favoritos" path="/favoritos" />
              <QuickLink label="Seguridad" path="/cambiar-password" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

/** * Sub-componentes para mantener el código limpio y profesional 
 */
function InfoRow({ label, value, isMono = false }) {
  return (
    <div className="space-y-1">
      <p className="text-[10px] font-bold text-primary/60 uppercase tracking-wider">{label}</p>
      <p className={`text-sm sm:text-base font-semibold text-neutral-dark ${isMono ? 'font-mono' : ''}`}>
        {value || <span className="text-gray-300 font-normal italic">No registrado</span>}
      </p>
    </div>
  );
}

function QuickLink({ label, path }) {
  const navigate = useNavigate();
  return (
    <button 
      onClick={() => navigate(path)}
      className="p-4 bg-white border border-gray-200 rounded-xl hover:border-primary hover:shadow-md transition-all group text-center"
    >
      <span className="text-[10px] font-bold text-neutral-dark group-hover:text-primary transition-colors uppercase tracking-tight">
        {label}
      </span>
    </button>
  );
}