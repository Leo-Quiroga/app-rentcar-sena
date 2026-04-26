// Gestión de vehículos - tabla por modelo con ordenadores y desplegables de unidades
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  getAdminCarModels, deleteCarModel,
  getUnitsByModel, updateCarUnit, deleteCarUnit, createCarModel
} from "../api/adminCarsApi";
import { getAdminBranches } from "../api/adminBranchesApi";

const normalize = (str) =>
  String(str ?? "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();

const STATUS_LABEL = {
  PENDING_REGISTRATION: { text: "Pendiente de identificación", color: "text-orange-600", bg: "bg-orange-100" },
  AVAILABLE:            { text: "Disponible",                  color: "text-green-600",  bg: "bg-green-100" },
  RENTED:               { text: "Rentado",                     color: "text-blue-600",   bg: "bg-blue-100" },
  MAINTENANCE:          { text: "En mantenimiento",            color: "text-yellow-600", bg: "bg-yellow-100" },
  OUT_OF_SERVICE:       { text: "Fuera de servicio",           color: "text-red-600",    bg: "bg-red-100" },
};

function StatusPill({ status }) {
  const s = STATUS_LABEL[status] || { text: status, color: "text-gray-600", bg: "bg-gray-100" };
  return (
    <span className={`inline-block px-2 py-0.5 rounded-full text-xs font-medium ${s.bg} ${s.color}`}>
      {s.text}
    </span>
  );
}

function SortIcon({ col, sortCol, sortDir }) {
  if (sortCol !== col) return <span className="ml-1 text-gray-300">↕</span>;
  return <span className="ml-1">{sortDir === "asc" ? "↑" : "↓"}</span>;
}

// ── Fila de unidad individual (editable) ──────────────────────────────────────
function UnitRow({ unit, branches, onSave, onDelete }) {
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState({
    plate: unit.plate || "",
    color: unit.color || "",
    status: unit.status || "PENDING_REGISTRATION",
    branchId: unit.branchId ? String(unit.branchId) : "",
    notes: unit.notes || "",
  });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const handleSave = async () => {
    setError("");
    setSaving(true);
    try {
      await onSave(unit.id, form);
      setEditing(false);
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  if (editing) {
    return (
      <tr className="bg-blue-50 border-t">
        <td className="px-3 py-2">
          <input value={form.plate} onChange={e => setForm({ ...form, plate: e.target.value })}
            placeholder="Placa" className="border rounded px-2 py-1 text-xs w-24" />
        </td>
        <td className="px-3 py-2">
          <input value={form.color} onChange={e => setForm({ ...form, color: e.target.value })}
            placeholder="Color" className="border rounded px-2 py-1 text-xs w-24" />
        </td>
        <td className="px-3 py-2">
          <select value={form.status} onChange={e => setForm({ ...form, status: e.target.value })}
            className="border rounded px-2 py-1 text-xs">
            {Object.entries(STATUS_LABEL).map(([k, v]) => (
              <option key={k} value={k}>{v.text}</option>
            ))}
          </select>
        </td>
        <td className="px-3 py-2">
          <input value={form.notes} onChange={e => setForm({ ...form, notes: e.target.value })}
            placeholder="Notas" className="border rounded px-2 py-1 text-xs w-32" />
        </td>
        <td className="px-3 py-2">
          <select value={form.branchId} onChange={e => setForm({ ...form, branchId: e.target.value })}
            className="border rounded px-2 py-1 text-xs w-36">
            <option value="">Selecciona sede</option>
            {branches.map(b => (
              <option key={b.id} value={String(b.id)}>{b.name}</option>
            ))}
          </select>
        </td>
        <td className="px-3 py-2">
          {error && <p className="text-red-500 text-xs mb-1">{error}</p>}
          <div className="flex gap-1">
            <button onClick={handleSave} disabled={saving}
              className="text-xs px-2 py-1 bg-green-500 text-white rounded hover:bg-green-600 disabled:opacity-50">
              {saving ? "..." : "Guardar"}
            </button>
            <button onClick={() => { setEditing(false); setError(""); }}
              className="text-xs px-2 py-1 bg-gray-300 rounded hover:bg-gray-400">
              Cancelar
            </button>
          </div>
        </td>
      </tr>
    );
  }

  return (
    <tr className="border-t hover:bg-gray-50">
      <td className="px-3 py-2 text-xs font-mono">{unit.plate || <span className="text-gray-400 italic">Sin placa</span>}</td>
      <td className="px-3 py-2 text-xs">{unit.color || <span className="text-gray-400 italic">Sin color</span>}</td>
      <td className="px-3 py-2"><StatusPill status={unit.status} /></td>
      <td className="px-3 py-2 text-xs text-gray-500">{unit.notes || "—"}</td>
      <td className="px-3 py-2 text-xs text-gray-500">{unit.branchName}</td>
      <td className="px-3 py-2">
        <div className="flex gap-1">
          <button onClick={() => setEditing(true)}
            className="text-xs px-2 py-1 text-yellow-600 border border-yellow-200 rounded hover:bg-yellow-50">
            ✏️ Editar
          </button>
          <button onClick={() => onDelete(unit.id)}
            className="text-xs px-2 py-1 text-red-600 border border-red-200 rounded hover:bg-red-50">
            🗑️
          </button>
        </div>
      </td>
    </tr>
  );
}

// ── Desplegable de unidades con filtros, ordenadores y agregar unidades ────────
function UnitsPanel({ modelId, modelName, branches, onClose }) {
  const [units, setUnits] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [filterStatus, setFilterStatus] = useState("");
  const [sortCol, setSortCol] = useState("branchName");
  const [sortDir, setSortDir] = useState("asc");

  // Agregar unidades
  const [showAddForm, setShowAddForm] = useState(false);
  const [addQty, setAddQty] = useState(1);
  const [addBranchId, setAddBranchId] = useState("");
  const [adding, setAdding] = useState(false);

  const load = () =>
    getUnitsByModel(modelId)
      .then(setUnits)
      .catch(err => alert("Error: " + err.message))
      .finally(() => setLoading(false));

  useEffect(() => { load(); }, [modelId]);

  const handleSort = (col) => {
    if (sortCol === col) setSortDir(d => d === "asc" ? "desc" : "asc");
    else { setSortCol(col); setSortDir("asc"); }
  };

  const handleSaveUnit = async (unitId, form) => {
    await updateCarUnit(unitId, form);
    await load();
  };

  const handleDeleteUnit = async (unitId) => {
    if (!window.confirm("¿Eliminar esta unidad?")) return;
    await deleteCarUnit(unitId);
    setUnits(prev => prev.filter(u => u.id !== unitId));
  };

  const handleAddUnits = async () => {
    if (!addBranchId) { alert("Selecciona una sede para las nuevas unidades."); return; }
    setAdding(true);
    try {
      // Reutilizamos el endpoint de crear modelo pasando quantity y branchId
      // pero en realidad necesitamos un endpoint de agregar unidades a modelo existente
      // Usamos updateCarUnit con POST al endpoint de unidades del modelo
      const { apiFetch } = await import("../api/http.js");
      for (let i = 0; i < addQty; i++) {
        await apiFetch(`/api/admin/cars/models/${modelId}/units`, {
          method: "POST",
          body: JSON.stringify({ branchId: parseInt(addBranchId) }),
        });
      }
      await load();
      setShowAddForm(false);
      setAddQty(1);
      setAddBranchId("");
    } catch (err) {
      alert("Error agregando unidades: " + err.message);
    } finally {
      setAdding(false);
    }
  };

  const filtered = units.filter(u => {
    const term = normalize(search);
    const matchSearch = !search ||
      normalize(u.plate).includes(term) ||
      normalize(u.color).includes(term) ||
      normalize(u.branchName).includes(term) ||
      normalize(STATUS_LABEL[u.status]?.text).includes(term);
    const matchStatus = !filterStatus || u.status === filterStatus;
    return matchSearch && matchStatus;
  });

  const sorted = [...filtered].sort((a, b) => {
    const va = normalize(a[sortCol] ?? "");
    const vb = normalize(b[sortCol] ?? "");
    return sortDir === "asc" ? va.localeCompare(vb) : vb.localeCompare(va);
  });

  return (
    <tr>
      <td colSpan="6" className="px-0 py-0 bg-gray-50 border-t border-gray-200">
        <div className="px-6 py-4">
          <div className="flex items-center justify-between mb-3">
            <p className="text-xs font-semibold text-gray-600 uppercase tracking-wide">
              Unidades — {modelName}
            </p>
            <button onClick={onClose} className="text-xs text-gray-400 hover:text-gray-600">▲ Ocultar</button>
          </div>

          {/* Filtros */}
          <div className="flex flex-wrap gap-2 mb-3">
            <input type="text" placeholder="Buscar placa, color, sede, estado..."
              value={search} onChange={e => setSearch(e.target.value)}
              className="border border-gray-300 rounded px-2 py-1 text-xs focus:ring-primary focus:border-primary w-56" />
            <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)}
              className="border border-gray-300 rounded px-2 py-1 text-xs focus:ring-primary focus:border-primary">
              <option value="">Todos los estados</option>
              {Object.entries(STATUS_LABEL).map(([k, v]) => (
                <option key={k} value={k}>{v.text}</option>
              ))}
            </select>
            {(search || filterStatus) && (
              <button onClick={() => { setSearch(""); setFilterStatus(""); }}
                className="text-xs text-gray-400 hover:text-red-500 underline">Limpiar</button>
            )}
          </div>

          {loading ? (
            <p className="text-xs text-gray-400">Cargando unidades...</p>
          ) : (
            <>
              {sorted.length === 0 ? (
                <p className="text-xs text-gray-400 italic">
                  {search || filterStatus ? "No hay unidades con esos filtros." : "No hay unidades registradas."}
                </p>
              ) : (
                <table className="w-full text-sm mb-3">
                  <thead>
                    <tr className="text-xs text-gray-500 border-b">
                      {[
                        { col: "plate",      label: "Placa" },
                        { col: "color",      label: "Color" },
                        { col: "status",     label: "Estado" },
                        { col: "notes",      label: "Notas" },
                        { col: "branchName", label: "Sede" },
                      ].map(({ col, label }) => (
                        <th key={col} onClick={() => handleSort(col)}
                          className="px-3 py-1 text-left cursor-pointer hover:bg-gray-100 select-none whitespace-nowrap">
                          {label}<SortIcon col={col} sortCol={sortCol} sortDir={sortDir} />
                        </th>
                      ))}
                      <th className="px-3 py-1 text-left">Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {sorted.map(unit => (
                      <UnitRow key={unit.id} unit={unit} branches={branches}
                        onSave={handleSaveUnit} onDelete={handleDeleteUnit} />
                    ))}
                  </tbody>
                </table>
              )}

              {/* Agregar unidades */}
              {!showAddForm ? (
                <button onClick={() => setShowAddForm(true)}
                  className="text-xs px-3 py-1 border border-dashed border-gray-400 text-gray-500 rounded hover:border-primary hover:text-primary transition">
                  + Agregar unidades
                </button>
              ) : (
                <div className="flex items-center gap-3 mt-2 p-3 bg-white border border-gray-200 rounded">
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">Cantidad</label>
                    <input type="number" min="1" max="20" value={addQty}
                      onChange={e => setAddQty(parseInt(e.target.value) || 1)}
                      className="border rounded px-2 py-1 text-xs w-16" />
                  </div>
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">Sede *</label>
                    <select value={addBranchId} onChange={e => setAddBranchId(e.target.value)}
                      className="border rounded px-2 py-1 text-xs w-40">
                      <option value="">Selecciona sede</option>
                      {branches.map(b => (
                        <option key={b.id} value={String(b.id)}>{b.name}</option>
                      ))}
                    </select>
                  </div>
                  <div className="flex gap-1 mt-4">
                    <button onClick={handleAddUnits} disabled={adding}
                      className="text-xs px-3 py-1 bg-primary text-white rounded hover:bg-primary-dark disabled:opacity-50">
                      {adding ? "..." : "Agregar"}
                    </button>
                    <button onClick={() => setShowAddForm(false)}
                      className="text-xs px-3 py-1 bg-gray-200 rounded hover:bg-gray-300">
                      Cancelar
                    </button>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </td>
    </tr>
  );
}

// ── Fila de modelo ─────────────────────────────────────────────────────────────
function ModelRow({ model, branches, onDelete, navigate }) {
  const [expanded, setExpanded] = useState(false);

  return (
    <>
      <tr className="border-t hover:bg-gray-50">
        <td className="px-4 py-3 font-medium">{model.brand} {model.model}</td>
        <td className="px-4 py-3 text-sm text-gray-500">{model.year}</td>
        <td className="px-4 py-3 text-sm">{model.categoryName}</td>
        <td className="px-4 py-3 text-sm">${model.pricePerDay?.toLocaleString()}</td>
        <td className="px-4 py-3">
          <span className="text-green-600 font-semibold">{model.availableUnits}</span>
          <span className="text-gray-400"> / {model.totalUnits}</span>
          <span className="text-xs text-gray-400 ml-1">disponibles</span>
        </td>
        <td className="px-4 py-3">
          <div className="flex gap-2 flex-wrap">
            <button onClick={() => setExpanded(e => !e)}
              className="text-xs px-2 py-1 bg-blue-50 text-blue-600 border border-blue-200 rounded hover:bg-blue-100">
              {expanded ? "▲ Ocultar" : "▼ Ver unidades"}
            </button>
            <button onClick={() => navigate(`/admin/autos/editar/${model.id}`)}
              className="text-xs px-2 py-1 text-yellow-600 border border-yellow-200 rounded hover:bg-yellow-50">
              ✏️ Editar
            </button>
            <button onClick={() => onDelete(model.id, `${model.brand} ${model.model}`)}
              className="text-xs px-2 py-1 text-red-600 border border-red-200 rounded hover:bg-red-50">
              🗑️ Eliminar
            </button>
          </div>
        </td>
      </tr>
      {expanded && (
        <UnitsPanel
          modelId={model.id}
          modelName={`${model.brand} ${model.model}`}
          branches={branches}
          onClose={() => setExpanded(false)}
        />
      )}
    </>
  );
}

// ── Componente principal ──────────────────────────────────────────────────────
export default function AdminAutos() {
  const navigate = useNavigate();
  const [models, setModels] = useState([]);
  const [branches, setBranches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState("");
  const [sortCol, setSortCol] = useState("categoryName");
  const [sortDir, setSortDir] = useState("asc");

  useEffect(() => {
    Promise.all([loadModels(), loadBranches()]);
  }, []);

  const loadModels = async () => {
    try {
      setLoading(true);
      const data = await getAdminCarModels();
      setModels(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const loadBranches = async () => {
    try {
      const data = await getAdminBranches();
      setBranches(data);
    } catch { /* no bloquear si falla */ }
  };

  const handleSort = (col) => {
    if (sortCol === col) setSortDir(d => d === "asc" ? "desc" : "asc");
    else { setSortCol(col); setSortDir("asc"); }
  };

  const handleDeleteModel = async (id, name) => {
    if (!window.confirm(`¿Eliminar el modelo "${name}" y todas sus unidades?`)) return;
    try {
      await deleteCarModel(id);
      setModels(prev => prev.filter(m => m.id !== id));
    } catch (err) {
      alert("Error: " + err.message);
    }
  };

  const filtered = models.filter(m =>
    !search ||
    normalize(m.brand).includes(normalize(search)) ||
    normalize(m.model).includes(normalize(search)) ||
    normalize(m.categoryName).includes(normalize(search))
  );

  const sorted = [...filtered].sort((a, b) => {
    const va = normalize(a[sortCol] ?? "");
    const vb = normalize(b[sortCol] ?? "");
    if (sortCol === "pricePerDay" || sortCol === "availableUnits" || sortCol === "totalUnits") {
      return sortDir === "asc" ? (a[sortCol] - b[sortCol]) : (b[sortCol] - a[sortCol]);
    }
    return sortDir === "asc" ? va.localeCompare(vb) : vb.localeCompare(va);
  });

  if (loading) return (
    <div className="max-w-6xl mx-auto py-10 px-4 text-center">
      <p className="text-gray-600">Cargando modelos...</p>
    </div>
  );

  if (error) return (
    <div className="max-w-6xl mx-auto py-10 px-4 text-center">
      <p className="text-red-600">Error: {error}</p>
      <button onClick={loadModels} className="mt-4 px-4 py-2 bg-primary text-white rounded">Reintentar</button>
    </div>
  );

  return (
    <div className="min-h-screen bg-neutral-light px-4 py-10 sm:px-6 lg:px-8">
      <div className="max-w-6xl mx-auto">
        <div className="flex items-center justify-between mb-6">
          <div>
            <button onClick={() => navigate("/admin")} className="text-primary hover:underline text-sm mb-2">
              ← Volver al Dashboard
            </button>
            <h1 className="text-2xl font-bold text-neutral-dark">🚗 Gestión de Vehículos</h1>
            <p className="text-sm text-gray-500 mt-1">{sorted.length} modelo(s)</p>
          </div>
          <button onClick={() => navigate("/admin/autos/nuevo")}
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition">
            + Nuevo Modelo
          </button>
        </div>

        <div className="mb-4">
          <input type="text" placeholder="Buscar por marca, modelo o categoría..."
            value={search} onChange={e => setSearch(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2 text-sm w-full max-w-md focus:ring-primary focus:border-primary" />
        </div>

        <div className="bg-white shadow rounded-lg overflow-x-auto">
          <table className="w-full table-auto text-sm">
            <thead className="bg-gray-50 border-b">
              <tr>
                {[
                  { col: "brand",          label: "Modelo" },
                  { col: "year",           label: "Año" },
                  { col: "categoryName",   label: "Categoría" },
                  { col: "pricePerDay",    label: "Precio/día" },
                  { col: "availableUnits", label: "Disponibilidad hoy" },
                ].map(({ col, label }) => (
                  <th key={col} onClick={() => handleSort(col)}
                    className="px-4 py-3 text-left font-semibold text-gray-700 cursor-pointer hover:bg-gray-100 select-none whitespace-nowrap">
                    {label}<SortIcon col={col} sortCol={sortCol} sortDir={sortDir} />
                  </th>
                ))}
                <th className="px-4 py-3 text-left font-semibold text-gray-700">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {sorted.map(model => (
                <ModelRow key={model.id} model={model} branches={branches}
                  onDelete={handleDeleteModel} navigate={navigate} />
              ))}
              {sorted.length === 0 && (
                <tr>
                  <td colSpan="6" className="text-center py-10 text-gray-500">
                    {search ? "No se encontraron modelos." : "No hay modelos registrados."}
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
