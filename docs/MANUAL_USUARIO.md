# MANUAL DE USUARIO - AUTORESERVE

## Información del Documento
- **Versión:** 1.0
- **Fecha:** Enero 2025
- **Proyecto:** AutoReserve - Sistema de Reserva de Vehículos

---

## 1. INTRODUCCIÓN

### 1.1 ¿Qué es AutoReserve?

AutoReserve es una plataforma web que te permite:
- Explorar un catálogo de vehículos disponibles
- Reservar autos para fechas específicas
- Gestionar tus reservas
- Guardar tus vehículos favoritos
- Ver información de sedes y categorías

### 1.2 Requisitos para Usar AutoReserve

- **Navegador web moderno:** Chrome, Firefox, Edge o Safari
- **Conexión a Internet**
- **Correo electrónico válido** (para registro)

### 1.3 Acceso al Sistema

- **URL:** `https://tudominio.com` (o `http://localhost:5173` en desarrollo)
- **Soporte:** Disponible 24/7

---

## 2. PRIMEROS PASOS

### 2.1 Página de Inicio

Al ingresar a AutoReserve verás:

1. **Barra de navegación superior:**
   - Logo de AutoReserve
   - Menú: Inicio, Categorías, Sedes, Contacto
   - Botones: Iniciar Sesión / Registrarse

2. **Buscador de autos:**
   - Campo de búsqueda por marca o modelo
   - Filtros por categoría

3. **Catálogo de vehículos:**
   - Tarjetas con foto, marca, modelo y precio
   - Botón "Ver detalles"

4. **Categorías destacadas:**
   - SUV, Sedán, Compacto, Pickup, Lujo

### 2.2 Navegación sin Registro

Puedes explorar el catálogo sin crear una cuenta:
- Ver todos los autos disponibles
- Ver detalles de cada vehículo
- Ver categorías y sedes
- Buscar autos por nombre

**Nota:** Para reservar un auto necesitas crear una cuenta.

---

## 3. REGISTRO DE USUARIO

### 3.1 Crear una Cuenta

**Paso 1:** Haz clic en "Registrarse" en la esquina superior derecha

**Paso 2:** Completa el formulario:
- **Nombre:** Tu nombre
- **Apellido:** Tu apellido
- **Correo electrónico:** Debe ser válido y único
- **Teléfono:** Número de contacto
- **Contraseña:** Mínimo 6 caracteres

**Paso 3:** Haz clic en "Registrarse"

**Paso 4:** Si el registro es exitoso:
- Serás redirigido automáticamente a la página principal
- Estarás autenticado en el sistema
- Verás tu nombre en la barra de navegación

### 3.2 Errores Comunes al Registrarse

| Error | Causa | Solución |
|-------|-------|----------|
| "Email ya registrado" | El correo ya existe en el sistema | Usa otro correo o inicia sesión |
| "Formato de email inválido" | Email mal escrito | Verifica el formato: usuario@dominio.com |
| "Todos los campos son requeridos" | Falta información | Completa todos los campos |

---

## 4. INICIAR SESIÓN

### 4.1 Acceder a tu Cuenta

**Paso 1:** Haz clic en "Iniciar Sesión"

**Paso 2:** Ingresa tus credenciales:
- **Correo electrónico**
- **Contraseña**

**Paso 3:** Haz clic en "Iniciar Sesión"

**Paso 4:** Si es exitoso:
- Serás redirigido a la página principal
- Verás tu nombre en la barra de navegación
- Tendrás acceso a funciones adicionales

### 4.2 ¿Olvidaste tu Contraseña?

1. Haz clic en "¿Olvidaste tu contraseña?"
2. Ingresa tu correo electrónico
3. Recibirás un enlace para restablecer tu contraseña
4. Sigue las instrucciones del correo

---

## 5. EXPLORAR VEHÍCULOS

### 5.1 Ver Catálogo de Autos

En la página principal verás todas las opciones disponibles:

**Información en cada tarjeta:**
- Foto del vehículo
- Marca y modelo
- Año
- Precio por día
- Categoría
- Sede donde está disponible

### 5.2 Buscar Vehículos

**Búsqueda por texto:**
1. Usa la barra de búsqueda en la parte superior
2. Escribe marca o modelo (ej: "Toyota", "RAV4")
3. Los resultados se filtran automáticamente

**Filtrar por categoría:**
1. Haz clic en "Categorías" en el menú
2. Selecciona una categoría (SUV, Sedán, etc.)
3. Verás solo los autos de esa categoría

### 5.3 Ver Detalles de un Auto

**Paso 1:** Haz clic en "Ver detalles" en cualquier tarjeta

**Paso 2:** Se abrirá un modal con:
- Galería de imágenes
- Información completa del vehículo
- Precio por día
- Categoría y sede
- Botón "Reservar ahora"
- Botón "Agregar a favoritos" (si estás autenticado)

**Paso 3:** Puedes:
- Navegar por las imágenes
- Cerrar el modal con la X
- Proceder a reservar

---

## 6. REALIZAR UNA RESERVA

### 6.1 Proceso de Reserva

**Requisito:** Debes estar autenticado

**Paso 1:** Desde el detalle del auto, haz clic en "Reservar ahora"

**Paso 2:** Selecciona las fechas:
- **Fecha de inicio:** Cuándo recoges el auto
- **Fecha de fin:** Cuándo devuelves el auto
- **Sede de recogida:** Dónde recoges el auto
- **Sede de devolución:** Dónde devuelves el auto

**Paso 3:** Revisa el resumen:
- Días totales de renta
- Precio por día
- Total a pagar
- Fechas seleccionadas

**Paso 4:** Haz clic en "Confirmar Reserva"

**Paso 5:** Verás un mensaje de confirmación:
- Número de reserva
- Detalles de la reserva
- Botón para ver tus reservas

### 6.2 Validaciones de Reserva

El sistema valida automáticamente:
- ✅ Las fechas no pueden estar en el pasado
- ✅ La fecha de fin debe ser posterior a la de inicio
- ✅ El auto debe estar disponible en esas fechas
- ✅ Debes seleccionar sedes válidas

### 6.3 Estados de Reserva

| Estado | Significado | Acciones Disponibles |
|--------|-------------|---------------------|
| **CONFIRMADA** | Reserva activa | Cancelar |
| **EN PROGRESO** | Estás usando el auto | Ninguna |
| **COMPLETADA** | Reserva finalizada | Ver detalles |
| **CANCELADA** | Reserva cancelada | Ninguna |

---

## 7. GESTIONAR RESERVAS

### 7.1 Ver Mis Reservas

**Paso 1:** Haz clic en tu nombre en la barra de navegación

**Paso 2:** Selecciona "Mis Reservas"

**Paso 3:** Verás una lista con todas tus reservas:
- Reservas activas (confirmadas y en progreso)
- Reservas pasadas (completadas)
- Reservas canceladas

**Información mostrada:**
- Foto del vehículo
- Marca y modelo
- Fechas de reserva
- Estado
- Total pagado
- Sedes de recogida/devolución

### 7.2 Ver Detalle de una Reserva

**Paso 1:** Haz clic en "Ver detalles" en cualquier reserva

**Paso 2:** Verás información completa:
- Datos del vehículo
- Fechas exactas
- Precio desglosado
- Estado de pago
- Información de contacto

### 7.3 Cancelar una Reserva

**Requisito:** Solo puedes cancelar reservas en estado "CONFIRMADA"

**Paso 1:** Desde el detalle de la reserva, haz clic en "Cancelar Reserva"

**Paso 2:** Confirma la cancelación en el diálogo

**Paso 3:** La reserva cambiará a estado "CANCELADA"

**Nota:** Las políticas de reembolso dependen de la fecha de cancelación.

---

## 8. FAVORITOS

### 8.1 Agregar a Favoritos

**Paso 1:** Desde el detalle de un auto, haz clic en el ícono de corazón ❤️

**Paso 2:** El auto se agregará a tu lista de favoritos

**Paso 3:** El ícono cambiará de color indicando que está en favoritos

### 8.2 Ver Mis Favoritos

**Paso 1:** Haz clic en tu nombre en la barra de navegación

**Paso 2:** Selecciona "Favoritos"

**Paso 3:** Verás todos los autos que has marcado como favoritos

**Desde aquí puedes:**
- Ver detalles de cada auto
- Reservar directamente
- Eliminar de favoritos

### 8.3 Eliminar de Favoritos

**Opción 1:** Desde la lista de favoritos, haz clic en el ícono de corazón

**Opción 2:** Desde el detalle del auto, haz clic en el ícono de corazón nuevamente

---

## 9. PERFIL DE USUARIO

### 9.1 Ver Mi Perfil

**Paso 1:** Haz clic en tu nombre en la barra de navegación

**Paso 2:** Selecciona "Mi Perfil"

**Información mostrada:**
- Nombre completo
- Correo electrónico
- Teléfono
- Dirección (si la has agregado)
- Ciudad
- Fecha de nacimiento
- Licencia de conducir
- Fecha de registro

### 9.2 Editar Mi Perfil

**Paso 1:** Desde tu perfil, haz clic en "Editar Perfil"

**Paso 2:** Actualiza la información que desees:
- Nombre y apellido
- Teléfono
- Dirección
- Ciudad
- Fecha de nacimiento
- Número de licencia de conducir

**Paso 3:** Haz clic en "Guardar Cambios"

**Nota:** No puedes cambiar tu correo electrónico desde aquí.

### 9.3 Cambiar Contraseña

**Paso 1:** Desde tu perfil, haz clic en "Cambiar Contraseña"

**Paso 2:** Ingresa:
- Contraseña actual
- Nueva contraseña
- Confirmar nueva contraseña

**Paso 3:** Haz clic en "Actualizar Contraseña"

---

## 10. EXPLORAR CATEGORÍAS

### 10.1 Ver Todas las Categorías

**Paso 1:** Haz clic en "Categorías" en el menú principal

**Paso 2:** Verás tarjetas con cada categoría:
- **SUV:** Vehículos deportivos utilitarios
- **Sedán:** Autos elegantes y cómodos
- **Compacto:** Autos pequeños y económicos
- **Pickup:** Camionetas de carga
- **Lujo:** Vehículos premium

**Información mostrada:**
- Imagen representativa
- Nombre de la categoría
- Descripción
- Cantidad de autos disponibles

### 10.2 Ver Autos por Categoría

**Paso 1:** Haz clic en cualquier categoría

**Paso 2:** Verás solo los autos de esa categoría

**Paso 3:** Puedes:
- Ver detalles de cada auto
- Reservar
- Agregar a favoritos
- Volver a todas las categorías

---

## 11. EXPLORAR SEDES

### 11.1 Ver Todas las Sedes

**Paso 1:** Haz clic en "Sedes" en el menú principal

**Paso 2:** Verás tarjetas con cada sede:
- Bogotá Centro
- Medellín
- Cali
- Barranquilla
- Cartagena

**Información mostrada:**
- Imagen de la sede
- Nombre
- Dirección completa
- Ciudad
- Teléfono de contacto
- Cantidad de autos disponibles

### 11.2 Ver Autos por Sede

**Paso 1:** Haz clic en "Ver autos" en cualquier sede

**Paso 2:** Verás solo los autos disponibles en esa sede

**Paso 3:** Puedes reservar cualquier auto de esa sede

---

## 12. BÚSQUEDA AVANZADA

### 12.1 Buscar por Disponibilidad

**Paso 1:** En la página principal, usa el buscador avanzado

**Paso 2:** Ingresa:
- Fecha de inicio
- Fecha de fin
- Categoría (opcional)

**Paso 3:** Haz clic en "Buscar"

**Paso 4:** Verás solo los autos disponibles en esas fechas

### 12.2 Filtros Combinados

Puedes combinar:
- Búsqueda por texto + categoría
- Búsqueda por fechas + categoría
- Búsqueda por sede + categoría

---

## 13. CONTACTO Y SOPORTE

### 13.1 Página de Contacto

**Paso 1:** Haz clic en "Contacto" en el menú

**Paso 2:** Verás información de contacto:
- Teléfono de soporte
- Correo electrónico
- Dirección de oficinas
- Horarios de atención

**Paso 3:** Puedes enviar un mensaje:
- Nombre
- Correo
- Asunto
- Mensaje

### 13.2 Preguntas Frecuentes

**¿Puedo modificar una reserva?**
- No directamente. Debes cancelar y crear una nueva.

**¿Cuánto tiempo antes debo reservar?**
- Puedes reservar con al menos 1 día de anticipación.

**¿Puedo recoger en una sede y devolver en otra?**
- Sí, selecciona diferentes sedes al reservar.

**¿Qué necesito para recoger el auto?**
- Licencia de conducir vigente
- Documento de identidad
- Confirmación de reserva

**¿Cuándo se cobra la reserva?**
- Al momento de recoger el vehículo en la sede.

---

## 14. POLÍTICAS Y TÉRMINOS

### 14.1 Política de Cancelación

- **Más de 48 horas antes:** Reembolso del 100%
- **24-48 horas antes:** Reembolso del 50%
- **Menos de 24 horas:** Sin reembolso

### 14.2 Requisitos para Rentar

- Ser mayor de 21 años
- Licencia de conducir vigente (mínimo 2 años)
- Documento de identidad
- Tarjeta de crédito o débito

### 14.3 Seguro y Cobertura

- Seguro básico incluido en el precio
- Cobertura adicional disponible en la sede
- Deducible según tipo de vehículo

---

## 15. CONSEJOS Y MEJORES PRÁCTICAS

### 15.1 Para una Mejor Experiencia

✅ **Reserva con anticipación** para mejores precios y disponibilidad

✅ **Completa tu perfil** con toda tu información

✅ **Revisa las políticas** antes de reservar

✅ **Llega puntual** a recoger tu vehículo

✅ **Inspecciona el auto** antes de salir de la sede

✅ **Devuelve con el tanque lleno** para evitar cargos

### 15.2 Seguridad de tu Cuenta

🔒 **Usa una contraseña segura** (mínimo 8 caracteres, letras y números)

🔒 **No compartas tu contraseña** con nadie

🔒 **Cierra sesión** en computadoras públicas

🔒 **Verifica la URL** antes de ingresar tus datos

---

## 16. CERRAR SESIÓN

**Paso 1:** Haz clic en tu nombre en la barra de navegación

**Paso 2:** Selecciona "Cerrar Sesión"

**Paso 3:** Serás redirigido a la página principal

**Nota:** Tus datos están seguros y puedes volver a iniciar sesión cuando quieras.

---

## 17. SOLUCIÓN DE PROBLEMAS

### 17.1 No Puedo Iniciar Sesión

**Problema:** "Usuario no encontrado"
- **Solución:** Verifica que el correo esté bien escrito o regístrate

**Problema:** "Contraseña incorrecta"
- **Solución:** Usa "¿Olvidaste tu contraseña?" para restablecerla

### 17.2 No Puedo Reservar

**Problema:** "Auto no disponible"
- **Solución:** El auto está reservado en esas fechas, elige otras fechas u otro auto

**Problema:** "Debes iniciar sesión"
- **Solución:** Crea una cuenta o inicia sesión

### 17.3 Problemas con el Sitio

**Problema:** La página no carga
- **Solución:** Refresca la página (F5) o limpia el caché del navegador

**Problema:** Las imágenes no se ven
- **Solución:** Verifica tu conexión a Internet

---

## 18. GLOSARIO

- **Reserva:** Solicitud para rentar un vehículo en fechas específicas
- **Sede:** Ubicación física donde puedes recoger/devolver autos
- **Categoría:** Tipo de vehículo (SUV, Sedán, etc.)
- **Favoritos:** Lista personal de autos que te interesan
- **Estado:** Situación actual de tu reserva
- **Perfil:** Tu información personal en el sistema

---

## 19. INFORMACIÓN DE CONTACTO

**Soporte Técnico:**
- Email: soporte@autoreserve.com
- Teléfono: +57 1 234 5678
- Horario: Lunes a Viernes, 8:00 AM - 6:00 PM

**Emergencias (24/7):**
- Teléfono: +57 300 123 4567

**Redes Sociales:**
- Facebook: @AutoReserve
- Instagram: @AutoReserve
- Twitter: @AutoReserve

---

**¡Gracias por usar AutoReserve!**

Esperamos que disfrutes tu experiencia rentando vehículos con nosotros.

---

**Documento elaborado por:** Equipo AutoReserve  
**Última actualización:** Enero 2025  
**Versión:** 1.0
