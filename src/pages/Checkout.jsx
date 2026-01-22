// // src/pages/Checkout.jsx
// import { useState } from "react";
// import { useNavigate } from "react-router-dom";
// import { mockReservations } from "../data/mockReservations";

// export default function Checkout() {
//   const navigate = useNavigate();
//   const reserva = mockReservations[0]; // ⚠️ luego se reemplaza por la reserva activa

//   const [paymentData, setPaymentData] = useState({
//     name: "",
//     cardNumber: "",
//     expiry: "",
//     cvv: "",
//   });

//   const handleChange = (e) => {
//     setPaymentData({ ...paymentData, [e.target.name]: e.target.value });
//   };

//   const handleSubmit = (e) => {
//     e.preventDefault();
//     // ⚠️ Lógica mock: si tarjeta empieza en 4 → pago exitoso, sino → fallido
//     if (paymentData.cardNumber.startsWith("4")) {
//       navigate("/reservas/confirmacion", { state: { reserva } });
//     } else {
//       navigate("/reservas/pago-fallido", { state: { reserva } });
//     }
//   };

//   return (
//     <div className="max-w-3xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
//       <h1 className="text-2xl font-bold mb-6">💳 Checkout</h1>

//       {/* Resumen de la reserva */}
//       <div className="bg-white shadow rounded-lg p-6 mb-8">
//         <h2 className="text-lg font-semibold mb-4">Resumen de la Reserva</h2>
//         <p><strong>Auto:</strong> {reserva.car.name}</p>
//         <p><strong>Retiro:</strong> {reserva.pickupCity} ({reserva.startDate})</p>
//         <p><strong>Entrega:</strong> {reserva.dropoffCity} ({reserva.endDate})</p>
//         <p><strong>Total:</strong> ${reserva.total}</p>
//       </div>

//       {/* Formulario de pago */}
//       <form
//         onSubmit={handleSubmit}
//         className="bg-white shadow rounded-lg p-6 space-y-4"
//       >
//         <h2 className="text-lg font-semibold">Datos de Pago</h2>

//         <div>
//           <label className="block text-sm font-medium">Nombre en la tarjeta</label>
//           <input
//             type="text"
//             name="name"
//             value={paymentData.name}
//             onChange={handleChange}
//             required
//             className="mt-1 w-full border rounded p-2"
//           />
//         </div>

//         <div>
//           <label className="block text-sm font-medium">Número de tarjeta</label>
//           <input
//             type="text"
//             name="cardNumber"
//             value={paymentData.cardNumber}
//             onChange={handleChange}
//             required
//             className="mt-1 w-full border rounded p-2"
//             placeholder="Ej: 4111111111111111"
//           />
//         </div>

//         <div className="flex gap-4">
//           <div className="flex-1">
//             <label className="block text-sm font-medium">Expiración</label>
//             <input
//               type="text"
//               name="expiry"
//               value={paymentData.expiry}
//               onChange={handleChange}
//               required
//               className="mt-1 w-full border rounded p-2"
//               placeholder="MM/AA"
//             />
//           </div>
//           <div className="flex-1">
//             <label className="block text-sm font-medium">CVV</label>
//             <input
//               type="text"
//               name="cvv"
//               value={paymentData.cvv}
//               onChange={handleChange}
//               required
//               className="mt-1 w-full border rounded p-2"
//               placeholder="123"
//             />
//           </div>
//         </div>

//         {/* Botones */}
//         <div className="flex justify-end gap-4 mt-6">
//           <button
//             type="button"
//             onClick={() => navigate("/reservas")}
//             className="px-4 py-2 border rounded hover:bg-gray-100"
//           >
//             Cancelar
//           </button>
//           <button
//             type="submit"
//             className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
//           >
//             Pagar
//           </button>
//         </div>
//       </form>
//     </div>
//   );
// }

// src/pages/Checkout.jsx
import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

export default function Checkout() {
  const navigate = useNavigate();
  const location = useLocation();

  // 👇 obtenemos la reserva activa desde la navegación
  const reserva = location.state?.reserva;

  const [paymentData, setPaymentData] = useState({
    name: "",
    cardNumber: "",
    expiry: "",
    cvv: "",
  });

  const handleChange = (e) => {
    setPaymentData({ ...paymentData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!reserva) {
      alert("No hay una reserva activa.");
      return;
    }

    // ⚠️ Lógica mock: si tarjeta empieza en 4 → éxito, sino → fallido
    if (paymentData.cardNumber.startsWith("4")) {
      navigate("/reservas/confirmacion", { state: { reserva } });
    } else {
      navigate("/reservas/pago-fallido", { state: { reserva } });
    }
  };

  // ⚠️ Si no hay reserva, mostramos un mensaje
  if (!reserva) {
    return (
      <div className="max-w-3xl mx-auto py-10 px-4 sm:px-6 lg:px-8 text-center">
        <h1 className="text-2xl font-bold mb-4">Checkout</h1>
        <p className="text-gray-600">No hay ninguna reserva activa.</p>
        <button
          onClick={() => navigate("/")}
          className="mt-6 px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
        >
          Volver al inicio
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <h1 className="text-2xl font-bold mb-6">💳 Checkout</h1>

      {/* Resumen de la reserva */}
      <div className="bg-white shadow rounded-lg p-6 mb-8">
        <h2 className="text-lg font-semibold mb-4">Resumen de la Reserva</h2>
        <p><strong>Auto:</strong> {reserva.car.name}</p>
        <p><strong>Retiro:</strong> {reserva.pickupCity} ({reserva.startDate})</p>
        <p><strong>Entrega:</strong> {reserva.dropoffCity} ({reserva.endDate})</p>
        <p><strong>Total:</strong> ${reserva.total}</p>
      </div>

      {/* Formulario de pago */}
      <form
        onSubmit={handleSubmit}
        className="bg-white shadow rounded-lg p-6 space-y-4"
      >
        <h2 className="text-lg font-semibold">Datos de Pago</h2>

        <div>
          <label className="block text-sm font-medium">Nombre en la tarjeta</label>
          <input
            type="text"
            name="name"
            value={paymentData.name}
            onChange={handleChange}
            required
            className="mt-1 w-full border rounded p-2"
          />
        </div>

        <div>
          <label className="block text-sm font-medium">Número de tarjeta</label>
          <input
            type="text"
            name="cardNumber"
            value={paymentData.cardNumber}
            onChange={handleChange}
            required
            className="mt-1 w-full border rounded p-2"
            placeholder="Ej: 4111111111111111"
          />
        </div>

        <div className="flex gap-4">
          <div className="flex-1">
            <label className="block text-sm font-medium">Expiración</label>
            <input
              type="text"
              name="expiry"
              value={paymentData.expiry}
              onChange={handleChange}
              required
              className="mt-1 w-full border rounded p-2"
              placeholder="MM/AA"
            />
          </div>
          <div className="flex-1">
            <label className="block text-sm font-medium">CVV</label>
            <input
              type="text"
              name="cvv"
              value={paymentData.cvv}
              onChange={handleChange}
              required
              className="mt-1 w-full border rounded p-2"
              placeholder="123"
            />
          </div>
        </div>

        {/* Botones */}
        <div className="flex justify-end gap-4 mt-6">
          <button
            type="button"
            onClick={() => navigate("/reservas")}
            className="px-4 py-2 border rounded hover:bg-gray-100"
          >
            Cancelar
          </button>
          <button
            type="submit"
            className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
          >
            Pagar
          </button>
        </div>
      </form>
    </div>
  );
}
