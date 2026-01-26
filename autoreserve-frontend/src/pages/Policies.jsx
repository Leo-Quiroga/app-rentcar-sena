export default function Policies() {
  return (
    <div className="max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8 space-y-10">
      {/* Encabezado */}
      <header className="text-center">
        <h1 className="text-3xl font-bold text-neutral-dark mb-4">
          📜 Políticas de AutoReserve
        </h1>
        <p className="text-gray-600">
          Aquí encontrarás las condiciones y políticas que aplican a todas las
          reservas de vehículos realizadas en nuestra plataforma.
        </p>
      </header>

      {/* Políticas de uso */}
      <section>
        <h2 className="text-xl font-semibold text-neutral-dark mb-3">
          1. Uso de la plataforma
        </h2>
        <p className="text-gray-700 leading-relaxed">
          El uso de la plataforma AutoReserve está sujeto a las leyes locales e
          internacionales. Los usuarios deben ser mayores de edad y contar con
          una licencia de conducción válida para poder realizar una reserva.
        </p>
      </section>

      {/* Cancelaciones */}
      <section>
        <h2 className="text-xl font-semibold text-neutral-dark mb-3">
          2. Cancelaciones y reembolsos
        </h2>
        <p className="text-gray-700 leading-relaxed">
          Las reservas pueden ser canceladas sin costo hasta 48 horas antes de
          la fecha de inicio. Después de este periodo, podrían aplicarse cargos
          por cancelación según el proveedor del vehículo.
        </p>
      </section>

      {/* Seguros */}
      <section>
        <h2 className="text-xl font-semibold text-neutral-dark mb-3">
          3. Seguros y cobertura
        </h2>
        <p className="text-gray-700 leading-relaxed">
          Todos los vehículos incluyen un seguro básico obligatorio. El cliente
          podrá adquirir coberturas adicionales durante el proceso de reserva o
          directamente con el proveedor al momento de retirar el vehículo.
        </p>
      </section>

      {/* Responsabilidad */}
      <section>
        <h2 className="text-xl font-semibold text-neutral-dark mb-3">
          4. Responsabilidad del usuario
        </h2>
        <p className="text-gray-700 leading-relaxed">
          El usuario es responsable del cuidado del vehículo durante el periodo
          de la reserva y deberá responder por cualquier daño ocasionado por un
          mal uso del mismo.
        </p>
      </section>

      {/* Footer de sección */}
      <footer className="text-sm text-gray-500 text-center">
        Estas políticas pueden actualizarse en cualquier momento. Te
        recomendamos revisarlas periódicamente.
      </footer>
    </div>
  );
}
