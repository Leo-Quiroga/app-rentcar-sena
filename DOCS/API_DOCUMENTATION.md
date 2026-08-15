# AutoReserve Backend - API Documentation

## Información General

**Base URL:** `http://localhost:8080`  
**Formato de Respuesta:** JSON  
**Autenticación:** JWT Bearer Token (excepto endpoints públicos)

### Formato de Respuesta Estándar

```json
{
  "success": true/false,
  "message": "Mensaje descriptivo",
  "data": { /* datos de respuesta */ }
}

Copy

Insert at cursor
markdown
Códigos de Estado HTTP
200 OK - Solicitud exitosa

201 Created - Recurso creado exitosamente

400 Bad Request - Error en la solicitud

401 Unauthorized - No autenticado

403 Forbidden - Sin permisos

404 Not Found - Recurso no encontrado

409 Conflict - Conflicto (ej: email duplicado)

422 Unprocessable Entity - Validación fallida

500 Internal Server Error - Error del servidor

1. AUTENTICACIÓN
1.1 Login
Endpoint: POST /api/auth/login
Acceso: Público

Request Body:

{
  "email": "user@example.com",
  "password": "password123"
}

Copy

Insert at cursor
json
Response (200 OK):

{
  "success": true,
  "message": "Inicio de sesión exitoso",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "role": "CLIENT",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}

Copy

Insert at cursor
json
Errores:

400 - Email o contraseña vacíos

401 - Contraseña incorrecta

404 - Usuario no encontrado

422 - Formato de email inválido

1.2 Registro
Endpoint: POST /api/auth/register
Acceso: Público

Request Body:

{
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@example.com",
  "password": "password123",
  "phone": "+57 300 123 4567"
}

Copy

Insert at cursor
json
Response (201 Created):

{
  "success": true,
  "message": "Usuario registrado exitosamente",
  "data": {
    "id": 2,
    "email": "juan@example.com",
    "role": "CLIENT",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}

Copy

Insert at cursor
json
Errores:

409 - Email ya registrado

422 - Formato de email inválido

2. CATEGORÍAS (Público)
2.1 Listar Todas las Categorías
Endpoint: GET /api/categories
Acceso: Público

Response (200 OK):

[
  {
    "id": 1,
    "name": "SUV",
    "description": "Vehículos deportivos utilitarios",
    "image": "assets/categories/suv.jpg",
    "carCount": 5
  }
]

Copy

Insert at cursor
json
2.2 Obtener Categoría por ID
Endpoint: GET /api/categories/{id}
Acceso: Público

Response (200 OK):

{
  "id": 1,
  "name": "SUV",
  "description": "Vehículos deportivos utilitarios",
  "image": "assets/categories/suv.jpg",
  "carCount": 5
}

Copy

Insert at cursor
json
Errores:

404 - Categoría no encontrada

3. SEDES (Público)
3.1 Listar Todas las Sedes
Endpoint: GET /api/branches
Acceso: Público

Response (200 OK):

[
  {
    "id": 1,
    "name": "Sede Bogotá Centro",
    "address": "Calle 100 #15-20",
    "city": "Bogotá",
    "phone": "+57 1 234 5678",
    "image": "assets/branches/bogota.jpg",
    "carCount": 15
  }
]

Copy

Insert at cursor
json
3.2 Obtener Sede por ID
Endpoint: GET /api/branches/{id}
Acceso: Público

Errores:

404 - Sede no encontrada

4. AUTOS (Público)
4.1 Listar Modelos Disponibles
Endpoint: GET /api/cars
Acceso: Público

Query Parameters:

page (opcional, default: 0)

size (opcional, default: 10)

categoryId (opcional)

branchId (opcional)

Response (200 OK):

[
  {
    "id": 1,
    "brand": "Toyota",
    "model": "RAV4",
    "year": 2023,
    "pricePerDay": 150000,
    "image": "assets/cars/rav4.jpg",
    "description": "SUV familiar",
    "categoryName": "SUV",
    "categoryId": 1,
    "availableUnits": 3,
    "totalUnits": 5
  }
]

Copy

Insert at cursor
json
4.2 Obtener Auto por ID
Endpoint: GET /api/cars/{id}
Acceso: Público

Errores:

404 - Auto no encontrado

5. BÚSQUEDA DE AUTOS
5.1 Buscar Modelos Disponibles por Fechas
Endpoint: GET /api/search/cars
Acceso: Público

Query Parameters:

startDate (requerido) - Formato: YYYY-MM-DD

endDate (requerido) - Formato: YYYY-MM-DD

categoryId (opcional)

Ejemplo:

GET /api/search/cars?startDate=2024-01-15&endDate=2024-01-20&categoryId=1

Copy

Insert at cursor
Response (200 OK):

[
  {
    "id": 1,
    "brand": "Toyota",
    "model": "RAV4",
    "year": 2023,
    "pricePerDay": 150000,
    "image": "assets/cars/rav4.jpg",
    "description": "SUV familiar",
    "categoryName": "SUV",
    "categoryId": 1,
    "availableUnits": 3,
    "totalUnits": 5
  }
]

Copy

Insert at cursor
json
Errores:

400 - Fechas inválidas o fecha fin anterior a fecha inicio

6. RESERVAS (Cliente Autenticado)
Autenticación Requerida: Bearer Token con rol CLIENT

6.1 Obtener Mis Reservas
Endpoint: GET /api/reservations/my
Acceso: CLIENT

Response (200 OK):

{
  "success": true,
  "message": "Reservas obtenidas exitosamente",
  "data": [
    {
      "id": 1,
      "carId": 5,
      "carBrand": "Toyota",
      "carModel": "RAV4",
      "carYear": 2023,
      "carImage": "assets/cars/rav4.jpg",
      "categoryName": "SUV",
      "startDate": "2024-01-15",
      "endDate": "2024-01-20",
      "status": "CONFIRMED",
      "paymentStatus": "PAID",
      "totalAmount": 750000,
      "totalDays": 5,
      "pricePerDay": 150000,
      "pickupBranchName": "Sede Bogotá Centro",
      "dropoffBranchName": "Sede Bogotá Centro",
      "userId": 2,
      "userFirstName": "Juan",
      "userLastName": "Pérez",
      "userEmail": "juan@example.com"
    }
  ],
  "count": 1
}


Copy

Insert at cursor
json
6.2 Obtener Reserva por ID
Endpoint: GET /api/reservations/{id}
Acceso: CLIENT

Errores:

403 - Sin permisos para ver esta reserva

404 - Reserva no encontrada

6.3 Crear Reserva
Endpoint: POST /api/reservations
Acceso: CLIENT
Descripción: Crea una reserva en estado PENDING. El auto físico se asigna al confirmar el pago.

Request Body:

{
  "carId": 1,
  "startDate": "2024-01-15",
  "endDate": "2024-01-20",
  "pickupBranchId": 1,
  "dropoffBranchId": 1
}

Copy

Insert at cursor
json
Nota: carId corresponde al ID del modelo de auto (CarModel), no de una unidad física.

Response (200 OK):

{
  "success": true,
  "message": "Reserva creada. Tienes 24 horas para completar el pago. El auto se asignará al confirmar el pago.",
  "data": { /* ReservationResponse */ }
}

Copy

Insert at cursor
json
Errores:

400 - Fechas inválidas o carId inválido

404 - Modelo, sede de retiro o sede de entrega no encontrados

6.4 Confirmar Pago
Endpoint: PUT /api/reservations/{id}/confirm-payment
Acceso: CLIENT
Descripción: Busca una unidad física disponible del modelo, la asigna a la reserva y cambia el estado a CONFIRMED.

Response (200 OK):

{
  "success": true,
  "message": "Pago confirmado exitosamente. Tu reserva está confirmada.",
  "data": { /* ReservationResponse */ }
}

Copy

Insert at cursor
json
Errores:

400 - Reserva no está en estado PENDING

400 - No hay unidades disponibles del modelo para esas fechas

403 - Sin permisos

6.5 Cancelar Reserva
Endpoint: PUT /api/reservations/{id}/cancel
Acceso: CLIENT
Descripción: Reglas de cancelación:

Estado PENDING: se cancela libremente

Estado CONFIRMED: solo si faltan 7 o más días para el inicio, genera reembolso pendiente

Estado IN_PROGRESS o COMPLETED: no se puede cancelar

Response (200 OK):

{
  "success": true,
  "message": "Reserva cancelada exitosamente.",
  "reservationId": 1
}

Copy

Insert at cursor
json
Errores:

400 - Reserva en curso, completada, ya cancelada, o menos de 7 días para el inicio

403 - Sin permisos

7. FAVORITOS (Cliente Autenticado)
Autenticación Requerida: Bearer Token con rol CLIENT

7.1 Obtener Mis Favoritos
Endpoint: GET /api/favorites/my
Acceso: CLIENT

Response (200 OK):

[
  {
    "id": 1,
    "brand": "Toyota",
    "model": "RAV4",
    "year": 2023,
    "pricePerDay": 150000,
    "categoryName": "SUV",
    "image": "assets/cars/rav4.jpg"
  }
]

Copy

Insert at cursor
json
7.2 Agregar a Favoritos
Endpoint: POST /api/favorites?carId={carModelId}
Acceso: CLIENT

Errores:

400 - El modelo ya está en favoritos

404 - Modelo no encontrado

7.3 Eliminar de Favoritos
Endpoint: DELETE /api/favorites/{carModelId}
Acceso: CLIENT

Errores:

400 - El modelo no está en favoritos

404 - Modelo no encontrado

8. PERFIL DE USUARIO (Autenticado)
Autenticación Requerida: Bearer Token (CLIENT o ADMIN)

8.1 Obtener Mi Perfil
Endpoint: GET /api/admin/users/me
Acceso: CLIENT, ADMIN

Response (200 OK):

{
  "success": true,
  "message": "Perfil obtenido exitosamente",
  "data": {
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan@example.com",
    "phone": "+57 300 123 4567",
    "address": "Calle 123 #45-67",
    "city": "Bogotá",
    "birthDate": "1990-05-15",
    "drivingLicense": "12345678",
    "createdAt": "2024-01-01T10:00:00"
  }
}

Copy

Insert at cursor
json
8.2 Actualizar Mi Perfil
Endpoint: PUT /api/admin/users/me
Acceso: CLIENT, ADMIN

Request Body:

{
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@example.com",
  "phone": "+57 300 123 4567",
  "address": "Calle 123 #45-67",
  "city": "Bogotá",
  "birthDate": "1990-05-15",
  "drivingLicense": "12345678"
}

Copy

Insert at cursor
json
9. MENSAJES DE SOPORTE
9.1 Crear Ticket (Cliente o Público)
Endpoint: POST /api/contact
Acceso: Público o autenticado
Descripción: Si el usuario está autenticado, sus datos se toman del token. Si es anónimo, se toman del body.

Request Body:

{
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "subject": "Consulta sobre reserva",
  "type": "PREGUNTA",
  "message": "Quisiera saber si puedo cambiar las fechas de mi reserva."
}

Copy

Insert at cursor
json
Response (200 OK):

{
  "success": true,
  "message": "Tu mensaje fue enviado exitosamente. Te responderemos pronto.",
  "ticketId": 1
}

Copy

Insert at cursor
json
9.2 Mis Tickets
Endpoint: GET /api/contact/my
Acceso: CLIENT

Response (200 OK):

[
  {
    "id": 1,
    "senderName": "Juan Pérez",
    "senderEmail": "juan@example.com",
    "subject": "Consulta sobre reserva",
    "type": "PREGUNTA",
    "status": "OPEN",
    "createdAt": "2024-01-15T10:00:00",
    "updatedAt": "2024-01-15T10:00:00",
    "userId": 2
  }
]

Copy

Insert at cursor
json
9.3 Ver Hilo de un Ticket (Cliente)
Endpoint: GET /api/contact/my/{id}
Acceso: CLIENT
Descripción: Al leer el hilo, si el ticket estaba en ANSWERED pasa automáticamente a IN_PROGRESS.

Response (200 OK):

{
  "ticket": { /* resumen del ticket */ },
  "replies": [
    {
      "id": 1,
      "content": "Quisiera saber si puedo cambiar las fechas.",
      "sentBy": "CLIENT",
      "authorName": "Juan Pérez",
      "createdAt": "2024-01-15T10:00:00"
    }
  ]
}

Copy

Insert at cursor
json
Errores:

403 - Sin permisos

404 - Ticket no encontrado

9.4 Responder en el Hilo (Cliente)
Endpoint: POST /api/contact/my/{id}/reply
Acceso: CLIENT

Request Body:

{ "message": "Gracias por la respuesta, tengo otra duda." }

Copy

Insert at cursor
json
Errores:

400 - Ticket cerrado

403 - Sin permisos

9.5 Cerrar Ticket (Cliente)
Endpoint: PUT /api/contact/my/{id}/close
Acceso: CLIENT

9.6 Conteo de Mensajes No Leídos
Endpoint: GET /api/contact/unread-count
Acceso: CLIENT, ADMIN
Descripción: Para CLIENT devuelve tickets en estado ANSWERED. Para ADMIN devuelve tickets en estado OPEN.

Response (200 OK):

{ "unreadCount": 3 }

Copy

Insert at cursor
json
9.7 Listar Todos los Tickets (Admin)
Endpoint: GET /api/contact/admin
Acceso: ADMIN

9.8 Ver Hilo de un Ticket (Admin)
Endpoint: GET /api/contact/admin/{id}
Acceso: ADMIN
Descripción: Al leer el hilo, si el ticket estaba en OPEN pasa automáticamente a IN_PROGRESS.

9.9 Responder en el Hilo (Admin)
Endpoint: POST /api/contact/admin/{id}/reply
Acceso: ADMIN

Request Body:

{ "message": "Hola Juan, sí puedes cambiar las fechas desde tu perfil." }

Copy

Insert at cursor
json
9.10 Cerrar Ticket (Admin)
Endpoint: PUT /api/contact/admin/{id}/close
Acceso: ADMIN

9.11 Admin Inicia Conversación con Cliente
Endpoint: POST /api/contact/admin/new
Acceso: ADMIN

Request Body:

{
  "userId": 2,
  "subject": "Información sobre tu reserva",
  "type": "SOLICITUD",
  "message": "Hola, te contactamos para informarte sobre tu reserva."
}

Copy

Insert at cursor
json
10. POLÍTICAS (Público / Admin)
10.1 Listar Todas las Políticas
Endpoint: GET /api/policies
Acceso: Público

Response (200 OK):

[
  {
    "id": 1,
    "slug": "policy-1234567890",
    "title": "Política de cancelación",
    "content": "Las reservas pueden cancelarse con 7 días de anticipación...",
    "sortOrder": 1,
    "updatedAt": "2024-01-01T10:00:00"
  }
]

Copy

Insert at cursor
json
10.2 Crear Política
Endpoint: POST /api/policies
Acceso: ADMIN

Request Body:

{
  "title": "Política de cancelación",
  "content": "Las reservas pueden cancelarse con 7 días de anticipación."
}

Copy

Insert at cursor
json
10.3 Actualizar Política
Endpoint: PUT /api/policies/{id}
Acceso: ADMIN

Request Body:

{
  "title": "Política de cancelación actualizada",
  "content": "Nuevo contenido."
}

Copy

Insert at cursor
json
Errores:

404 - Política no encontrada

10.4 Eliminar Política
Endpoint: DELETE /api/policies/{id}
Acceso: ADMIN
Descripción: Elimina la política y reordena automáticamente las restantes.

Errores:

404 - Política no encontrada

11. PREGUNTAS FRECUENTES - FAQ (Público / Admin)
11.1 Listar Todas las FAQ
Endpoint: GET /api/faq
Acceso: Público

Response (200 OK):

[
  {
    "id": 1,
    "question": "¿Cómo puedo cancelar mi reserva?",
    "answer": "Puedes cancelar desde la sección Mis Reservas con al menos 7 días de anticipación.",
    "sortOrder": 1,
    "updatedAt": "2024-01-01T10:00:00"
  }
]

Copy

Insert at cursor
json
11.2 Crear FAQ
Endpoint: POST /api/faq
Acceso: ADMIN

Request Body:

{
  "question": "¿Cómo puedo cancelar mi reserva?",
  "answer": "Puedes cancelar desde la sección Mis Reservas."
}

Copy

Insert at cursor
json
11.3 Actualizar FAQ
Endpoint: PUT /api/faq/{id}
Acceso: ADMIN

11.4 Eliminar FAQ
Endpoint: DELETE /api/faq/{id}
Acceso: ADMIN
Descripción: Elimina la FAQ y reordena automáticamente las restantes.

12. ADMINISTRACIÓN DE USUARIOS (Admin)
12.1 Listar Usuarios
Endpoint: GET /api/admin/users
Acceso: ADMIN

Query Parameters:

page (opcional, default: 0)

size (opcional, default: 10)

Response (200 OK):

{
  "success": true,
  "message": "Usuarios obtenidos exitosamente",
  "data": {
    "users": [ /* lista de UserResponse */ ],
    "page": 0,
    "size": 10,
    "totalElements": 25,
    "totalPages": 3
  }
}

Copy

Insert at cursor
json
12.2 Obtener Usuario por ID
Endpoint: GET /api/admin/users/{id}
Acceso: ADMIN

Errores:

404 - Usuario no encontrado

12.3 Crear Usuario
Endpoint: POST /api/admin/users
Acceso: ADMIN

Request Body:

{
  "firstName": "María",
  "lastName": "García",
  "email": "maria@example.com",
  "password": "password123",
  "phone": "+57 300 987 6543",
  "role": "CLIENT"
}

Copy

Insert at cursor
json
Errores:

400 - Email ya registrado o rol no encontrado

12.4 Actualizar Usuario
Endpoint: PUT /api/admin/users/{id}
Acceso: ADMIN
Nota: El campo password es opcional. Si no se envía, la contraseña no se modifica.

Errores:

404 - Usuario o rol no encontrado

12.5 Eliminar Usuario
Endpoint: DELETE /api/admin/users/{id}
Acceso: ADMIN

Errores:

400 - No se puede eliminar un usuario con rol ADMIN

404 - Usuario no encontrado

13. ADMINISTRACIÓN DE AUTOS (Admin)
13.1 Listar Todos los Autos
Endpoint: GET /api/admin/cars
Acceso: ADMIN

13.2 Obtener Auto por ID
Endpoint: GET /api/admin/cars/{id}
Acceso: ADMIN

13.3 Crear Auto
Endpoint: POST /api/admin/cars
Acceso: ADMIN

13.4 Actualizar Auto
Endpoint: PUT /api/admin/cars/{id}
Acceso: ADMIN

13.5 Eliminar Auto
Endpoint: DELETE /api/admin/cars/{id}
Acceso: ADMIN

14. ADMINISTRACIÓN DE CATEGORÍAS (Admin)
14.1 Listar Categorías
Endpoint: GET /api/admin/categories
Acceso: ADMIN

14.2 Crear Categoría
Endpoint: POST /api/admin/categories
Acceso: ADMIN

14.3 Actualizar Categoría
Endpoint: PUT /api/admin/categories/{id}
Acceso: ADMIN

14.4 Eliminar Categoría
Endpoint: DELETE /api/admin/categories/{id}
Acceso: ADMIN

Errores:

400 - No se puede eliminar una categoría con autos asociados

15. ADMINISTRACIÓN DE SEDES (Admin)
15.1 Listar Sedes
Endpoint: GET /api/admin/branches
Acceso: ADMIN

15.2 Crear Sede
Endpoint: POST /api/admin/branches
Acceso: ADMIN

15.3 Actualizar Sede
Endpoint: PUT /api/admin/branches/{id}
Acceso: ADMIN

15.4 Eliminar Sede
Endpoint: DELETE /api/admin/branches/{id}
Acceso: ADMIN

Errores:

400 - No se puede eliminar una sede con autos asociados

16. ADMINISTRACIÓN DE RESERVAS (Admin)
16.1 Listar Todas las Reservas
Endpoint: GET /api/admin/reservations
Acceso: ADMIN

Query Parameters:

page (opcional, default: 0)

size (opcional, default: 20)

16.2 Crear Reserva para Cliente
Endpoint: POST /api/admin/reservations
Acceso: ADMIN

Request Body:

{
  "userId": 2,
  "carId": 1,
  "startDate": "2024-01-15",
  "endDate": "2024-01-20",
  "pickupBranchId": 1,
  "dropoffBranchId": 1
}

Copy

Insert at cursor
json
16.3 Actualizar Estado de Reserva
Endpoint: PUT /api/admin/reservations/{id}/status?status={status}
Acceso: ADMIN

Estados válidos: CONFIRMED, CANCELLED, COMPLETED, IN_PROGRESS

17. ESTADÍSTICAS DEL DASHBOARD (Admin)
17.1 Obtener Estadísticas Generales
Endpoint: GET /api/admin/stats
Acceso: ADMIN

Response (200 OK):

{
  "totalUsers": 25,
  "totalCars": 40,
  "availableCars": 30,
  "rentedCars": 8,
  "maintenanceCars": 2,
  "totalReservations": 120,
  "totalCategories": 5,
  "totalBranches": 3,
  "message": "Estadísticas obtenidas exitosamente"
}

Copy

Insert at cursor
json
18. ESTADÍSTICAS DE FAVORITOS (Admin)
18.1 Estadísticas Generales de Favoritos
Endpoint: GET /api/admin/favorites/stats
Acceso: ADMIN

Response (200 OK):

{
  "success": true,
  "generalStats": {
    "totalFavorites": 85,
    "totalModels": 12,
    "modelsWithFavorites": 9,
    "averageFavoritesPerModel": 9.4
  },
  "modelStats": [ /* lista ordenada por favoritos */ ],
  "topModels": [ /* top 10 */ ]
}

Copy

Insert at cursor
json
18.2 Top Modelos Más Populares
Endpoint: GET /api/admin/favorites/top-models
Acceso: ADMIN

18.3 Usuarios que Tienen un Modelo como Favorito
Endpoint: GET /api/admin/favorites/model/{carModelId}/users
Acceso: ADMIN

ESTADOS Y ENUMERACIONES
Estados de Auto (CarStatus)
AVAILABLE - Disponible

RENTED - Rentado

MAINTENANCE - En mantenimiento

OUT_OF_SERVICE - Fuera de servicio

Estados de Reserva (ReservationStatus)
PENDING - Pendiente de pago

CONFIRMED - Confirmada y pagada

IN_PROGRESS - En curso

COMPLETED - Completada

CANCELLED - Cancelada

Estados de Pago (PaymentStatus)
NO_PAYMENT - Sin pago (reserva pendiente)

PAID - Pagado

REFUND_PENDING - Reembolso en proceso

Estados de Ticket (MessageStatus)
OPEN - Abierto, esperando respuesta del admin

IN_PROGRESS - En progreso

ANSWERED - Respondido por el admin

CLOSED - Cerrado

Tipos de Ticket (MessageType)
PREGUNTA

SOLICITUD

QUEJA

OTRO

Roles de Usuario
CLIENT - Cliente regular

ADMIN - Administrador

NOTAS IMPORTANTES
Autenticación JWT: Todos los endpoints protegidos requieren el header Authorization: Bearer {token}

Formato de Fechas: Usar formato ISO 8601 (YYYY-MM-DD)

Paginación: Los endpoints con paginación usan parámetros page (base 0) y size

Modelo vs Unidad: El sistema distingue entre CarModel (modelo de auto, ej: Toyota RAV4 2023) y Car (unidad física con placa). Las reservas se hacen sobre modelos; las unidades físicas se asignan al confirmar el pago.

Seguridad: Contraseñas almacenadas con BCrypt

CORS: Configurado para aceptar peticiones desde http://localhost:5173

Versión: 2.0
Última Actualización: Agosto de 2026
Desarrollado por: AutoReserve Team