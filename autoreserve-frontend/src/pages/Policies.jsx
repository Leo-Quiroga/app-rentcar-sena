// Página de políticas — solo lectura para clientes, editable para admin
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { getPolicies, updatePolicy } from "../api/policiesApi";

// Políticas por defecto si el backend aún no tiene datos
const DEFAULT_POLICIES = [
  {
    slug: "uso",
    title: "1. Uso de la plataforma",
    content:
      "El uso de la plataforma AutoReserve está sujeto a las leyes locales e internacionales. Los usuarios deben ser mayores de edad y contar con una licencia de conducción válida para poder realizar una reserva.",
  },
  {
    slug: "cancelaciones",
    title: "2. Cancelaciones y reembolsos",
    content:
      "Las reservas pueden ser canceladas sin costo hasta 48 horas antes de la fecha de inicio. Después de este periodo, podrían aplicarse cargos por cancelación según el proveedor del vehículo.",
  },
  {
    slug: "seguros",
    title: "3. Seguros y cobertura",
    content:
      "Todos los vehículos incluyen un seguro básico obligatorio. El cliente podrá adquirir coberturas adicionales durante el proceso de reserva o directamente con el proveedor al momento de retirar el vehículo.",
  },
  {
    slug: "responsabilidad",
    title: "4. Responsabilidad del usuario",
    content:
      "El usuario es responsable del cuidado del vehículo durante el periodo de la reserva y deberá responder por cualquier daño ocasionado por un mal uso del mismo.",
  },
];

export default function Policies() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const isAdmin = user?.role === "ADMIN";

  const [policies, setPolicies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editingSlug, setEditingSlug] = useState(null);
  const [editForm, setEditForm] = useState({ title: "", content: "" });
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState("");

  useEffect(() => {
    getPolicies()
      .then(data => {
        setPolicies(data.length > 0 ? data : DEFAULT_POLICIES);
      })
      .catch(() => {
        // Si el backend no responde, usar las políticas por defecto
        setPolicies(DEFAULT_POLICIES);
      })
      .finally(() => setLoading(false));
  }, []);

  const startEdit = (policy) => {
    setEditingSlug(policy.slug);
    setEditForm({ title: policy.title, content: policy.content });
    setSaveError("");
  };

  const cancelEdit = () => {
    setEditingSlug(null);
    setSaveError("");
  };

  const handleSave = async (slug) => {
    setSaving(true);
    setSaveError("");
    try {
      await updatePolicy(slug, editForm);
      setPolicies(prev =>
        prev.map(p => p.slug === slug ? { ...p, ...editForm } : p)
      );
      setEditingSlug(null);
    } catch (err) {
      setSaveError(err.message);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return (
    <div className="max-w-4xl mx-auto py-12 px-4 text-center">
      <p className="text-gray-500">Cargando políticas...</p>
    </div>
  );

  return (
    <div className="max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8 space-y-10">

      {/* Botón volver al dashboard (solo admin) */}
      {isAdmin && (
        <div>
          <button onClick={() => navigate("/admin")} className="text-primary hover:underline text-sm">
            ← Volver al Dashboard
          </button>
        </div>
      )}

      {/* Encabezado */}
      <header className="text-center">
        <h1 className="text-3xl font-bold text-neutral-dark mb-4">📜 Políticas de AutoReserve</h1>
        <p className="text-gray-600">
          Aquí encontrarás las condiciones y políticas que aplican a todas las reservas de vehículos realizadas en nuestra plataforma.
        </p>
        {isAdmin && (
          <p className="text-xs text-blue-600 mt-2 bg-blue-50 inline-block px-3 py-1 rounded-full">
            ✏️ Modo administrador — puedes editar cada sección
          </p>
        )}
      </header>

      {/* Secciones de políticas */}
      {policies.map(policy => (
        <section key={policy.slug} className="border border-gray-100 rounded-lg p-6 bg-white shadow-sm">
          {editingSlug === policy.slug ? (
            // Modo edición
            <div className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-gray-500 mb-1">Título</label>
                <input
                  type="text"
                  value={editForm.title}
                  onChange={e => setEditForm({ ...editForm, title: e.target.value })}
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-500 mb-1">Contenido</label>
                <textarea
                  value={editForm.content}
                  onChange={e => setEditForm({ ...editForm, content: e.target.value })}
                  rows={6}
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary resize-y"
                />
              </div>
              {saveError && (
                <p className="text-red-500 text-xs">{saveError}</p>
              )}
              <div className="flex gap-2">
                <button
                  onClick={() => handleSave(policy.slug)}
                  disabled={saving}
                  className="px-4 py-2 bg-primary text-white text-sm rounded hover:bg-primary-dark disabled:opacity-50 transition"
                >
                  {saving ? "Guardando..." : "Guardar cambios"}
                </button>
                <button
                  onClick={cancelEdit}
                  className="px-4 py-2 border border-gray-300 text-sm rounded hover:bg-gray-100 transition"
                >
                  Cancelar
                </button>
              </div>
            </div>
          ) : (
            // Modo lectura
            <div>
              <div className="flex items-start justify-between gap-4">
                <h2 className="text-xl font-semibold text-neutral-dark mb-3">{policy.title}</h2>
                {isAdmin && (
                  <button
                    onClick={() => startEdit(policy)}
                    className="text-xs px-3 py-1 border border-blue-200 text-blue-600 rounded hover:bg-blue-50 transition shrink-0"
                  >
                    ✏️ Editar
                  </button>
                )}
              </div>
              <p className="text-gray-700 leading-relaxed whitespace-pre-line">{policy.content}</p>
              {policy.updatedAt && (
                <p className="text-xs text-gray-400 mt-3">
                  Última actualización: {new Date(policy.updatedAt).toLocaleDateString()}
                </p>
              )}
            </div>
          )}
        </section>
      ))}

      <footer className="text-sm text-gray-500 text-center">
        Estas políticas pueden actualizarse en cualquier momento. Te recomendamos revisarlas periódicamente.
      </footer>
    </div>
  );
}
