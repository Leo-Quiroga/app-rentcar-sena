// Componente para confirmar la reserva de un auto
import { useState, useEffect } from "react";
import { createReservation } from "../api/reservationsApi";
import { createReservationForClient } from "../api/adminReservationsApi";
import { getBranches } from "../api/branchesApi";
import { getUsers } from "../api/adminUsersApi";
import { useAuth } from "../auth/AuthContext";

export default function ModalConfirmarReserva({ car, filters, onClose, onConfirm }) {
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [branches, setBranches] = useState([]);
  const [users, setUsers] = useState([]);

  const isAdmin = user?.role === 'ADMIN';
  const pickupBranch = branches.find(b => b.id == filters?.selectedPickupBranch);
  const dropoffBranch = branches.find(b => b.id == filters?.selectedDropoffBranch);
  const selectedClientData = users.find(u => u.id == filters?.selectedClient);

  // Cargar datos necesarios
  useEffect(() => {
    const loadData = async () => {
      try {
        const branchesData = await getBranches();
        setBranches(branchesData);
        
        if (isAdmin) {
          const usersData = await getUsers();
          setUsers(usersData.content || usersData.users || []);
        }
      } catch (error) {
        console.error('Error cargando datos:', error);
      }
    };
    
    if (car) {
      loadData();
    }
  }, [car, isAdmin]);

  if (!car || !filters || !filters.startDate || !filters.endDate) return null;

  // Verificar autenticación
  if (!user) {
    return (
      <div className="fixed inset-0 bg-black/50 flex justify-center items-center z-50">
        <div className="bg-white rounded-lg shadow-lg w-full max-w-md mx-4 p-6">
          <h2 className="text-xl font-bold mb-4">Iniciar Sesión Requerido</h2>
          <p className="text-gray-600 mb-4">Debes iniciar sesión para realizar una reserva.</p>
          <div className="flex justify-end gap-3">
            <button onClick={onClose} className="px-4 py-2 border rounded-lg">Cancelar</button>
            <button 
              onClick={() => window.location.href = '/login'} 
              className="px-4 py-2 bg-primary text-white rounded-lg"
            >
              Iniciar Sesión
            </button>
          </div>
        </div>
      </div>
    );
  }

  const start = new Date(filters.startDate);
  const end = new Date(filters.endDate);
  const dias = Math.max(1, Math.ceil((end - start) / (1000 * 60 * 60 * 24)));
  const total = (car.pricePerDay || 0) * dias;

  const handleConfirm = async () => {
    if (!filters?.selectedPickupBranch || !filters?.selectedDropoffBranch) {
      alert('Selecciona las sedes de retiro y entrega en la página anterior');
      return;
    }

    if (isAdmin && !filters?.selectedClient) {
      alert('Selecciona el cliente para la reserva');
      return;
    }

    try {
      setLoading(true);
      let reservation;
      
      if (isAdmin) {
        // Admin creando reserva para cliente
        reservation = await createReservationForClient({
          carId: car.id,
          userId: parseInt(filters.selectedClient),
          startDate: filters.startDate,
          endDate: filters.endDate,
          pickupBranchId: parseInt(filters.selectedPickupBranch),
          dropoffBranchId: parseInt(filters.selectedDropoffBranch)
        });
      } else {
        // Cliente creando su propia reserva
        reservation = await createReservation({
          carId: car.id,
          startDate: filters.startDate,
          endDate: filters.endDate,
          pickupBranchId: parseInt(filters.selectedPickupBranch),
          dropoffBranchId: parseInt(filters.selectedDropoffBranch)
        });
      }
      
      onConfirm && onConfirm({ reservation, car, filters, dias, total });
    } catch (error) {
      alert('Error creando reserva: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex justify-center items-center z-50">
      <div className="bg-white rounded-lg shadow-lg w-full max-w-lg mx-4 sm:mx-6 lg:mx-8 p-6">
        {/* Header */}
        <h2 className="text-xl font-bold text-neutral-dark mb-4">Confirmar Reserva</h2>

        {/* Información del cliente (solo admin) */}
        {isAdmin && selectedClientData && (
          <div className="mb-4 p-3 bg-blue-50 rounded">
            <h4 className="font-medium text-sm text-gray-700 mb-2">Cliente seleccionado:</h4>
            <div className="text-sm text-gray-600 space-y-1">
              <p><span className="font-medium">Nombre:</span> {selectedClientData.firstName} {selectedClientData.lastName}</p>
              <p><span className="font-medium">Email:</span> {selectedClientData.email}</p>
              <p><span className="font-medium">ID:</span> #{selectedClientData.id}</p>
              {selectedClientData.licenseNumber && (
                <p><span className="font-medium">Licencia:</span> {selectedClientData.licenseNumber}</p>
              )}
            </div>
          </div>
        )}

        {/* Información de sedes seleccionadas */}
        <div className="mb-4 p-3 bg-gray-50 rounded">
          <h4 className="font-medium text-sm text-gray-700 mb-2">Sedes seleccionadas:</h4>
          <div className="text-sm text-gray-600 space-y-1">
            <p><span className="font-medium">Retiro:</span> {pickupBranch?.name} - {pickupBranch?.city}</p>
            <p><span className="font-medium">Entrega:</span> {dropoffBranch?.name} - {dropoffBranch?.city}</p>
          </div>
        </div>

        {/* Info del auto */}
        <div className="space-y-3">
          {car.image && (
            <img src={car.image} alt={`${car.brand} ${car.model}`} className="w-full h-32 object-cover rounded" />
          )}
          <div className="space-y-2 text-sm text-gray-700">
            <p><span className="font-semibold">Auto:</span> {car.brand} {car.model} ({car.year})</p>
            <p><span className="font-semibold">Categoría:</span> {car.categoryName}</p>
            <p><span className="font-semibold">Fecha Retiro:</span> {filters.startDate}</p>
            <p><span className="font-semibold">Fecha Entrega:</span> {filters.endDate}</p>
            <p><span className="font-semibold">Precio por día:</span> ${car.pricePerDay}</p>
            <p><span className="font-semibold">Días:</span> {dias}</p>
            <p className="font-bold text-lg text-primary mt-3">Total: ${total.toLocaleString()}</p>
          </div>
        </div>

        {/* Botones */}
        <div className="flex justify-end gap-3 mt-6">
          <button
            onClick={onClose}
            className="px-4 py-2 border border-gray-300 text-gray-600 rounded-lg hover:bg-gray-100 transition"
          >
            Cancelar
          </button>
          <button
            onClick={handleConfirm}
            disabled={loading}
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition disabled:opacity-50"
          >
            {loading ? 'Creando...' : 'Confirmar Reserva'}
          </button>
        </div>
      </div>
    </div>
  );
}
