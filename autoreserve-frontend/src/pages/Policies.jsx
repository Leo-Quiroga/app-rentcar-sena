// Página de políticas — solo lectura para clientes, editable para admin
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { getPolicies, createPolicy, updatePolicy, deletePolicy } from "../api/policiesApi";

const DEFAULT_POLICIES = [
  { id: "d1", title: "Uso de la plataforma", sortOrder: 1, content: "El uso de la plataforma AutoReserve está sujeto a las leyes locales e internacionales. Los usuarios deben ser mayores de edad y contar con una licencia de conducción válida para poder realizar una reserva." },
  { id: "d2", title: "Cancelaciones y reembolsos", sortOrder: 2, content: "Las reservas pueden ser canceladas sin costo hasta 48 horas antes de la fecha de inicio. Después de este periodo, podrían aplicarse cargos por cancelación." },
  { id: "d3", title: "Seguros y cobertura", sortOrder: 3, content: "Todos los vehículos incluyen un seguro básico obligatorio. El cliente podrá adquirir coberturas adicionales durante el proceso de reserva." },
  { id: "d4", title: "Responsabilidad del usuario", sortOrder: 4, content: "El usuario es responsable del cuidado del vehículo durante el periodo de la reserva y deberá responder por cualquier daño ocasionado por un mal uso del mismo." },
];

export default function Policies() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const isAdmin = user?.role === "ADMIN";

  const [policies, setPolicies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState(null);
  const [editForm, setEditForm] = useState({ title: "", content: "" });
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState("");
  const [showNewForm, setShowNewForm] = useState(false);
  const [newForm, setNewForm] = useState({ title: "", content: "" });
  const [creating, setCreating] = useState(false);

  const load = () =>
    getPolicies()
      .then(data => setPolicies(data.length > 0 ? data : DEFAULT_POLICIES))
      .catch(() => setPolicies(DEFAULT_POLICIES))
      .finally(() => setLoading(false));

  useEffect(() => { load(); }, []);

  const startEdit = (policy) => {
    setEditingId(policy.id);
    setEditForm({ title: policy.title, content: policy.content });
    setSaveError("");
  };

  const handleSave = async (id) => {
    setSaving(true); setSaveError("");
    try {
      await updatePolicy(id, editForm);
      setPolicies(prev => prev.map(p => p.id === id ? { ...p, ...editForm } : p));
      setEditingId(null);
    } catch (err) { setSaveError(err.message); }
    finally { setSaving(false); }
  };

  const handleDelete = async (id, title) => {
    if (!window.confirm(`¿Eliminar la política "${title}"? La numeración se actualizará automáticamente.`)) return;
    try {
      await deletePolicy(id);
      await load();
    } catch (err) { alert("Error: " + err.message); }
  };

  const handleCreate = async () => {
    if (!newForm.title.trim() || !newForm.content.trim()) {
      alert("El título y el contenido son obligatorios.");
      return;
    }
    setCreating(true);
    try {
      await createPolicy(newForm);
      await load();
      setNewForm({ title: "", content: "" });
      setShowNewForm(false);
    } catch (err) { alert("Error: " + err.message); }
    finally { setCreating(false); }
  };

  if (loading) return (
    <div className="max-w-4xl mx-auto py-12 px-4 text-center">
      <p className="text-gray-500">Cargando políticas...</p>
    </div>
  );

  return (
    <div className="max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8 space-y-8">
      {isAdmin && (
        <button onClick={() => navigate("/admin")} className="text-primary hover:underline text-sm">
          ← Volver al Dashboard
        </button>
      )}

      <header className="text-center">
        <h1 className="text-3xl font-bold text-neutral-dark mb-4">📜 Políticas de AutoReserve</h1>
        <p className="text-gray-600">Condiciones y políticas que aplican a todas las reservas realizadas en nuestra plataforma.</p>
        {isAdmin && (
          <p className="text-xs text-blue-600 mt-2 bg-blue-50 inline-block px-3 py-1 rounded-full">
            ✏️ Modo administrador — puedes crear, editar y eliminar secciones
          </p>
        )}
      </header>

      {policies.map((policy, index) => (
        <section key={policy.id} className="border border-gray-100 rounded-lg p-6 bg-white shadow-sm">
          {editingId === policy.id ? (
            <div className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-gray-500 mb-1">Título</label>
                <input type="text" value={editForm.title}
                  onChange={e => setEditForm({ ...editForm, title: e.target.value })}
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary" />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-500 mb-1">Contenido</label>
                <textarea value={editForm.content} rows={6}
                  onChange={e => setEditForm({ ...editForm, content: e.target.value })}
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:ring-primary focus:border-primary resize-y" />
              </div>
              {saveError && <p className="text-red-500 text-xs">{saveError}</p>}
              <div className="flex gap-2">
                <button onClick={() => handleSave(policy.id)} disabled={saving}
                  className="px-4 py-2 bg-primary text-white text-sm rounded hover:bg-primary-dark disabled:opacity-50 transition">
                  {saving ? "Guardando..." : "Guardar cambios"}
                </button>
                <button onClick={() => setEditingId(null)}
                  className="px-4 py-2 border border-gray-300 text-sm rounded hover:bg-gray-100 transition">
                  Cancelar
                </button>
              </div>
            </div>
          ) : (
            <div>
              <div className="flex items-start justify-between gap-4">
                <h2 className="text-xl font-semibold text-neutral-dark mb-3">
                  {index + 1}. {policy.title}
                </h2>
                {isAdmin && (
                  <div className="flex gap-2 shrink-0">
                    <button onClick={() => startEdit(policy)}
                      className="text-xs px-3 py-1 border border-blue-200 text-blue-600 rounded hover:bg-blue-50 transition">
                      ✏️ Editar
                    </button>
                    <button onClick={() => handleDelete(policy.id, policy.title)}
                      className="text-xs px-3 py-1 border border-red-200 text-red-600 rounded hover:bg-red-50 transition">
                      🗑️ Eliminar
                    </button>
                  </div>
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

      {isAdmin && (
        !showNewForm ? (
          <button onClick={() => setShowNewForm(true)}
            className="w-full py-3 border-2 border-dashed border-gray-300 text-gray-500 rounded-lg hover:border-primary hover:text-primary transition text-sm font-medium">
            + Agregar nueva política ({policies.length + 1})
          </button>
        ) : (
          <section className="border-2 border-primary border-dashed rounded-lg p-6 bg-blue-50 space-y-3">
            <h3 className="font-semibold text-gray-700">Nueva política — sección {policies.length + 1}</h3>
            <div>
              <label className="block text-xs font-semibold text-gray-500 mb-1">Título *</label>
              <input type="text" value={newForm.title}
                onChange={e => setNewForm({ ...newForm, title: e.target.value })}
                placeholder="Ej: Política de privacidad"
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm bg-white focus:ring-primary focus:border-primary" />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-500 mb-1">Contenido *</label>
              <textarea value={newForm.content} rows={5}
                onChange={e => setNewForm({ ...newForm, content: e.target.value })}
                placeholder="Escribe el contenido de la política..."
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm bg-white focus:ring-primary focus:border-primary resize-y" />
            </div>
            <div className="flex gap-2">
              <button onClick={handleCreate} disabled={creating}
                className="px-4 py-2 bg-primary text-white text-sm rounded hover:bg-primary-dark disabled:opacity-50 transition">
                {creating ? "Creando..." : "Crear política"}
              </button>
              <button onClick={() => { setShowNewForm(false); setNewForm({ title: "", content: "" }); }}
                className="px-4 py-2 border border-gray-300 text-sm rounded hover:bg-gray-100 transition">
                Cancelar
              </button>
            </div>
          </section>
        )
      )}

      <footer className="text-sm text-gray-500 text-center">
        Estas políticas pueden actualizarse en cualquier momento. Te recomendamos revisarlas periódicamente.
      </footer>
    </div>
  );
}
