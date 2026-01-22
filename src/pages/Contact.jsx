import { useState } from "react";

export default function Contact() {
  const [form, setForm] = useState({ name: "", email: "", message: "" });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    alert("✅ Mensaje enviado (mock). En el futuro se conectará al backend.");
    setForm({ name: "", email: "", message: "" });
  };

  return (
    <div className="max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8 space-y-12">
      {/* Encabezado */}
      <header className="text-center">
        <h1 className="text-3xl font-bold text-neutral-dark mb-4">📞 Contacto</h1>
        <p className="text-gray-600">
          ¿Tienes dudas o necesitas ayuda? Escríbenos y te responderemos lo antes posible.
        </p>
      </header>

      {/* Formulario de contacto */}
      <section className="bg-white shadow rounded-lg p-6 space-y-4">
        <h2 className="text-xl font-semibold text-neutral-dark mb-2">
          Envíanos un mensaje
        </h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="text"
            name="name"
            placeholder="Tu nombre"
            value={form.name}
            onChange={handleChange}
            required
            className="w-full border border-gray-300 rounded px-3 py-2"
          />
          <input
            type="email"
            name="email"
            placeholder="Tu correo"
            value={form.email}
            onChange={handleChange}
            required
            className="w-full border border-gray-300 rounded px-3 py-2"
          />
          <textarea
            name="message"
            placeholder="Tu mensaje"
            rows="4"
            value={form.message}
            onChange={handleChange}
            required
            className="w-full border border-gray-300 rounded px-3 py-2"
          ></textarea>
          <button
            type="submit"
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
          >
            Enviar mensaje
          </button>
        </form>
      </section>

      {/* FAQ */}
      <section>
        <h2 className="text-xl font-semibold text-neutral-dark mb-4">Preguntas Frecuentes</h2>
        <div className="space-y-4">
          <details className="bg-white shadow rounded-lg p-4">
            <summary className="font-medium cursor-pointer">¿Cómo puedo modificar una reserva?</summary>
            <p className="text-gray-600 mt-2">
              Desde tu perfil, entra a <strong>“Mis Reservas”</strong> y selecciona la reserva
              que quieras modificar. En el futuro se habilitarán cambios de fecha y auto.
            </p>
          </details>
          <details className="bg-white shadow rounded-lg p-4">
            <summary className="font-medium cursor-pointer">¿Qué pasa si cancelo una reserva?</summary>
            <p className="text-gray-600 mt-2">
              Podrás cancelar desde tu perfil en la sección de reservas. Las políticas de
              reembolso estarán disponibles en la página de <strong>Políticas</strong>.
            </p>
          </details>
          <details className="bg-white shadow rounded-lg p-4">
            <summary className="font-medium cursor-pointer">¿Cómo contacto soporte técnico?</summary>
            <p className="text-gray-600 mt-2">
              Puedes usar este mismo formulario o abrir un ticket en la página de{" "}
              <strong>Soporte</strong>.
            </p>
          </details>
        </div>
      </section>
    </div>
  );
}
