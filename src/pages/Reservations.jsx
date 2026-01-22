// // src/pages/Reservations.jsx
// import { useState } from "react";
// import { mockReservations } from "../data/mockReservations";
// import ModalCarDetail from "../components/ModalCarDetail";
// import ModalReservaConfirmada from "../components/ModalReservaConfirmada";

// export default function Reservations() {
//   const [reservations] = useState(mockReservations);

//   const [selectedCar, setSelectedCar] = useState(null);
//   const [reservaDetalle, setReservaDetalle] = useState(null);

//   return (
//     <div className="bg-neutral-light min-h-screen">
//       <div className="space-y-12 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
//         <h1 className="text-xl sm:text-2xl font-bold text-neutral-dark">
//           📅 Mis Reservas
//         </h1>

//         {reservations.length > 0 ? (
//           <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
//             {reservations.map((reserva) => (
//               <div key={reserva.id} className="bg-white rounded-lg shadow p-4 flex flex-col">
//                 <img
//                   src={reserva.car.image}
//                   alt={reserva.car.name}
//                   className="h-40 w-full object-cover rounded"
//                 />
//                 <h2 className="mt-3 font-semibold text-lg text-neutral-dark">
//                   {reserva.car.name}
//                 </h2>
//                 <p className="text-sm text-gray-600">{reserva.car.category}</p>
//                 <p className="text-sm text-gray-600 mt-1">
//                   {reserva.filters.startDate} → {reserva.filters.endDate}
//                 </p>
//                 <p className="text-sm font-semibold text-gray-800 mt-2">
//                   Total: ${reserva.total}
//                 </p>

//                 <div className="mt-4 flex gap-2">
//                   <button
//                     onClick={() => setSelectedCar(reserva.car)}
//                     className="flex-1 text-xs sm:text-sm py-2 px-3 rounded bg-gray-200 hover:bg-gray-300 text-gray-800 transition"
//                   >
//                     Ver auto
//                   </button>
//                   <button
//                     onClick={() => setReservaDetalle(reserva)}
//                     className="flex-1 text-xs sm:text-sm py-2 px-3 rounded bg-primary text-white hover:bg-primary-dark transition"
//                   >
//                     Ver reserva
//                   </button>
//                 </div>
//               </div>
//             ))}
//           </div>
//         ) : (
//           <p className="text-gray-600 text-center py-12">
//             Aún no tienes reservas realizadas.
//           </p>
//         )}

//         {selectedCar && (
//           <ModalCarDetail
//             car={selectedCar}
//             filters={null}
//             onClose={() => setSelectedCar(null)}
//           />
//         )}

//         {reservaDetalle && (
//           <ModalReservaConfirmada
//             reserva={reservaDetalle}
//             onClose={() => setReservaDetalle(null)}
//           />
//         )}
//       </div>
//     </div>
//   );
// }

// src/pages/Reservations.jsx
import { Link } from "react-router-dom";
import { mockReservations } from "../data/mockReservations";

export default function Reservations() {
  const reservations = mockReservations;

  return (
    <div className="max-w-5xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <h1 className="text-2xl font-bold mb-6">📅 Mis Reservas</h1>

      {reservations.length === 0 ? (
        <p className="text-gray-600">No tienes reservas todavía.</p>
      ) : (
        <div className="space-y-4">
          {reservations.map((reserva) => (
            <div
              key={reserva.id}
              className="bg-white shadow rounded-lg p-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4"
            >
              <div>
                <h2 className="text-lg font-semibold text-neutral-dark">
                  {reserva.car.name}
                </h2>
                <p className="text-sm text-gray-600">
                  {reserva.filters.pickupCity} → {reserva.filters.dropoffCity}
                </p>
                <p className="text-sm text-gray-600">
                  {reserva.filters.startDate} - {reserva.filters.endDate}
                </p>
                <p className="text-sm font-medium text-primary">
                  Total: ${reserva.total}
                </p>
              </div>

              <Link
                to={`/reservas/${reserva.id}`}
                className="text-sm px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition"
              >
                Ver detalle
              </Link>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
