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
```

### Códigos de Estado HTTP

- `200 OK` - Solicitud exitosa
- `201 Created` - Recurso creado exitosamente
- `400 Bad Request` - Error en la solicitud
- `401 Unauthorized` - No autenticado
- `403 Forbidden` - Sin permisos
- `404 Not Found` - Recurso no encontrado
- `409 Conflict` - Conflicto (ej: email duplicado)
- `422 Unprocessable Entity` - Validación fallida
- `500 Internal Server Error` - Error del servidor

---

## 1. AUTENTICACIÓN

### 1.1 Login

**Endpoint:** `POST /api/auth/login`  
**Acceso:** Público  
**Descripción:** Autentica un usuario y devuelve un token JWT

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
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
```

**Errores:**
- `400` - Email o contraseña vacíos
- `404` - Usuario no encontrado
- `401` - Contraseña incorrecta
- `422` - Formato de email inválido

---

### 1.2 Registro

**Endpoint:** `POST /api/auth/register`  
**Acceso:** Público  
**Descripción:** Registra un nuevo usuario con rol CLIENT

**Request Body:**
```json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@example.com",
  "password": "password123",
  "phone": "+57 300 123 4567"
}
```

**Response (201 Created):**
```json
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
```

**Errores:**
- `409` - Email ya registrado
- `422` - Formato de email inválido

---

## 2. CATEGORÍAS (Público)

### 2.1 Listar Todas las Categorías

**Endpoint:** `GET /api/categories`  
**Acceso:** Público  
**Descripción:** Obtiene todas las categorías de vehículos

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "SUV",
    "description": "Vehículos deportivos utilitarios",
    "image": "assets/categories/suv.jpg",
    "carCount": 5
  },
  {
    "id": 2,
    "name": "Sedán",
    "description": "Vehículos sedán de lujo",
    "image": "assets/categories/sedan.jpg",
    "carCount": 8
  }
]
```

---

### 2.2 Obtener Categoría por ID

**Endpoint:** `GET /api/categories/{id}`  
**Acceso:** Público  
**Descripción:** Obtiene una categoría específica

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "SUV",
  "description": "Vehículos deportivos utilitarios",
  "image": "assets/categories/suv.jpg",
  "carCount": 5
}
```

**Errores:**
- `404` - Categoría no encontrada

---

## 3. SEDES (Público)

### 3.1 Listar Todas las Sedes

**Endpoint:** `GET /api/branches`  
**Acceso:** Público  
**Descripción:** Obtiene todas las sedes disponibles

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Sede Bogotá Centro",
    "address": "Calle 100 #15-20",
    "city": "Bogotá",
    "phone": "+57 1 234 5678",
    "image": "assets/branches/bogota.jpg",
    "carCount": 15
  },
  {
    "id": 2,
    "name": "Sede Medellín",
    "address": "Carrera 43A #1-50",
    "city": "Medellín",
    "phone": "+57 4 567 8901",
    "image": "assets/branches/medellin.jpg",
    "carCount": 12
  }
]
```

---

### 3.2 Obtener Sede por ID

**Endpoint:** `GET /api/branches/{id}`  
**Acceso:** Público  
**Descripción:** Obtiene una sede específica

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Sede Bogotá Centro",
  "address": "Calle 100 #15-20",
  "city": "Bogotá",
  "phone": "+57 1 234 5678",
  "image": "assets/branches/bogota.jpg",
  "carCount": 15
}
```

**Errores:**
- `404` - Sede no encontrada

---

## 4. AUTOS (Público)

### 4.1 Listar Autos Disponibles

**Endpoint:** `GET /api/cars`  
**Acceso:** Público  
**Descripción:** Obtiene autos disponibles con filtros opcionales

**Query Parameters:**
- `page` (opcional, default: 0) - Número de página
- `size` (opcional, default: 10) - Tamaño de página
- `categoryId` (opcional) - Filtrar por categoría
- `branchId` (opcional) - Filtrar por sede

**Ejemplos:**
- `GET /api/cars` - Todos los autos disponibles
- `GET /api/cars?categoryId=1` - Autos de categoría SUV
- `GET /api/cars?branchId=2` - Autos en sede Medellín
- `GET /api/cars?categoryId=1&branchId=2` - Autos SUV en Medellín
- `GET /api/cars?page=0&size=20` - Primera página con 20 resultados

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "brand": "Toyota",
    "model": "RAV4",
    "year": 2023,
    "plate": "ABC123",
    "pricePerDay": 150000,
    "status": "AVAILABLE",
    "categoryName": "SUV",
    "branchName": "Sede Bogotá Centro",
    "image": "assets/cars/rav4.jpg"
  }
]
```

---

### 4.2 Obtener Auto por ID

**Endpoint:** `GET /api/cars/{id}`  
**Acceso:** Público  
**Descripción:** Obtiene detalles de un auto específico

**Response (200 OK):**
```json
{
  "id": 1,
  "brand": "Toyota",
  "model": "RAV4",
  "year": 2023,
  "plate": "ABC123",
  "pricePerDay": 150000,
  "status": "AVAILABLE",
  "categoryName": "SUV",
  "branchName": "Sede Bogotá Centro",
  "image": "assets/cars/rav4.jpg"
}
```

**Errores:**
- `404` - Auto no encontrado

---

## 5. BÚSQUEDA DE AUTOS

### 5.1 Buscar Autos Disponibles por Fechas

**Endpoint:** `GET /api/search/cars`  
**Acceso:** Público  
**Descripción:** Busca autos disponibles en un rango de fechas

**Query Parameters:**
- `startDate` (requerido) - Fecha de inicio (formato: YYYY-MM-DD)
- `endDate` (requerido) - Fecha de fin (formato: YYYY-MM-DD)
- `categoryId` (opcional) - Filtrar por categoría

**Ejemplo:**
```
GET /api/search/cars?startDate=2024-01-15&endDate=2024-01-20&categoryId=1
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "brand": "Toyota",
    "model": "RAV4",
    "year": 2023,
    "plate": "ABC123",
    "pricePerDay": 150000,
    "status": "AVAILABLE",
    "categoryName": "SUV",
    "branchName": "Sede Bogotá Centro",
    "image": "assets/cars/rav4.jpg"
  }
]
```

**Errores:**
- `400` - Parámetros de fecha faltantes o inválidos

---

## 6. RESERVAS (Cliente Autenticado)

**Autenticación Requerida:** Bearer Token con rol CLIENT

### 6.1 Obtener Mis Reservas

**Endpoint:** `GET /api/reservations/my`  
**Acceso:** CLIENT  
**Descripción:** Obtiene todas las reservas del usuario autenticado

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
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
      "paymentStatus": "PENDING",
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
  ]
}
```

---

### 6.2 Obtener Reserva por ID

**Endpoint:** `GET /api/reservations/{id}`  
**Acceso:** CLIENT  
**Descripción:** Obtiene detalles de una reserva específica del usuario

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Reserva obtenida exitosamente",
  "data": {
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
    "paymentStatus": "PENDING",
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
}
```

**Errores:**
- `400` - No tienes permisos para ver esta reserva
- `404` - Reserva no encontrada

---

### 6.3 Crear Reserva

**Endpoint:** `POST /api/reservations`  
**Acceso:** CLIENT  
**Descripción:** Crea una nueva reserva para el usuario autenticado

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "carId": 5,
  "startDate": "2024-01-15",
  "endDate": "2024-01-20",
  "pickupBranchId": 1,
  "dropoffBranchId": 1
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Reserva creada exitosamente",
  "data": {
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
    "paymentStatus": "PENDING",
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
}
```

**Errores:**
- `400` - Auto no disponible / Fechas inválidas
- `404` - Auto o sede no encontrados

---

### 6.4 Cancelar Reserva

**Endpoint:** `PUT /api/reservations/{id}/cancel`  
**Acceso:** CLIENT  
**Descripción:** Cancela una reserva confirmada del usuario

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Reserva cancelada exitosamente",
  "reservationId": 1
}
```

**Errores:**
- `400` - No tienes permisos / Solo se pueden cancelar reservas confirmadas
- `404` - Reserva no encontrada

---

## 7. FAVORITOS (Cliente Autenticado)

**Autenticación Requerida:** Bearer Token con rol CLIENT

### 7.1 Obtener Mis Favoritos

**Endpoint:** `GET /api/favorites/my`  
**Acceso:** CLIENT  
**Descripción:** Obtiene la lista de autos favoritos del usuario

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "brand": "Toyota",
    "model": "RAV4",
    "year": 2023,
    "plate": "ABC123",
    "pricePerDay": 150000,
    "status": "AVAILABLE",
    "categoryName": "SUV",
    "branchName": "Sede Bogotá Centro",
    "image": null
  }
]
```

---

### 7.2 Agregar a Favoritos

**Endpoint:** `POST /api/favorites?carId={carId}`  
**Acceso:** CLIENT  
**Descripción:** Agrega un auto a la lista de favoritos

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `carId` (requerido) - ID del auto a agregar

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Auto agregado a favoritos exitosamente",
  "carId": 1
}
```

**Errores:**
- `400` - El auto ya está en favoritos
- `404` - Auto no encontrado

---

### 7.3 Eliminar de Favoritos

**Endpoint:** `DELETE /api/favorites/{carId}`  
**Acceso:** CLIENT  
**Descripción:** Elimina un auto de la lista de favoritos

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Auto removido de favoritos exitosamente",
  "carId": 1
}
```

**Errores:**
- `400` - El auto no está en favoritos
- `404` - Auto no encontrado

---

## 8. PERFIL DE USUARIO (Autenticado)

**Autenticación Requerida:** Bearer Token (CLIENT o ADMIN)

### 8.1 Obtener Mi Perfil

**Endpoint:** `GET /api/admin/users/me`  
**Acceso:** CLIENT, ADMIN  
**Descripción:** Obtiene el perfil completo del usuario autenticado

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
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
```

---

### 8.2 Actualizar Mi Perfil

**Endpoint:** `PUT /api/admin/users/me`  
**Acceso:** CLIENT, ADMIN  
**Descripción:** Actualiza el perfil del usuario autenticado

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
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
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Perfil actualizado exitosamente"
}
```

---

## 9. ADMINISTRACIÓN DE USUARIOS (Admin)

**Autenticación Requerida:** Bearer Token con rol ADMIN

### 9.1 Listar Usuarios

**Endpoint:** `GET /api/admin/users`  
**Acceso:** ADMIN  
**Descripción:** Lista todos los usuarios con paginación

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `page` (opcional, default: 0) - Número de página
- `size` (opcional, default: 10) - Tamaño de página

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Usuarios obtenidos exitosamente",
  "data": {
    "users": [
      {
        "id": 1,
        "firstName": "Juan",
        "lastName": "Pérez",
        "email": "juan@example.com",
        "phone": "+57 300 123 4567",
        "role": "CLIENT",
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 25,
    "totalPages": 3
  }
}
```

---

### 9.2 Obtener Usuario por ID

**Endpoint:** `GET /api/admin/users/{id}`  
**Acceso:** ADMIN  
**Descripción:** Obtiene detalles de un usuario específico

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Usuario obtenido exitosamente",
  "data": {
    "id": 1,
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan@example.com",
    "phone": "+57 300 123 4567",
    "role": "CLIENT",
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

**Errores:**
- `404` - Usuario no encontrado

---

### 9.3 Crear Usuario

**Endpoint:** `POST /api/admin/users`  
**Acceso:** ADMIN  
**Descripción:** Crea un nuevo usuario (CLIENT o ADMIN)

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "firstName": "María",
  "lastName": "García",
  "email": "maria@example.com",
  "password": "password123",
  "phone": "+57 300 987 6543",
  "role": "CLIENT"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Usuario creado exitosamente",
  "data": {
    "id": 3,
    "firstName": "María",
    "lastName": "García",
    "email": "maria@example.com",
    "phone": "+57 300 987 6543",
    "role": "CLIENT",
    "createdAt": "2024-01-15T14:30:00"
  }
}
```

**Errores:**
- `400` - Email ya registrado
- `404` - Rol no encontrado

---

### 9.4 Actualizar Usuario

**Endpoint:** `PUT /api/admin/users/{id}`  
**Acceso:** ADMIN  
**Descripción:** Actualiza los datos de un usuario existente

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "firstName": "María",
  "lastName": "García",
  "email": "maria@example.com",
  "phone": "+57 300 987 6543",
  "role": "CLIENT",
  "password": "newpassword123"
}
```

**Nota:** El campo `password` es opcional. Si no se envía, la contraseña no se modifica.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Usuario actualizado exitosamente",
  "data": {
    "id": 3,
    "firstName": "María",
    "lastName": "García",
    "email": "maria@example.com",
    "phone": "+57 300 987 6543",
    "role": "CLIENT",
    "createdAt": "2024-01-15T14:30:00"
  }
}
```

**Errores:**
- `404` - Usuario o rol no encontrado

---

### 9.5 Eliminar Usuario

**Endpoint:** `DELETE /api/admin/users/{id}`  
**Acceso:** ADMIN  
**Descripción:** Elimina un usuario del sistema (no permite eliminar ADMIN)

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Usuario eliminado exitosamente",
  "deletedId": 3
}
```

**Errores:**
- `400` - No se puede eliminar un usuario ADMIN
- `404` - Usuario no encontrado

---

## 10. ADMINISTRACIÓN DE AUTOS (Admin)

**Autenticación Requerida:** Bearer Token con rol ADMIN

### 10.1 Listar Todos los Autos

**Endpoint:** `GET /api/admin/cars`  
**Acceso:** ADMIN  
**Descripción:** Lista todos los autos (disponibles y no disponibles)

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `page` (opcional, default: 0) - Número de página
- `size` (opcional, default: 10) - Tamaño de página

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "brand": "Toyota",
    "model": "RAV4",
    "year": 2023,
    "plate": "ABC123",
    "pricePerDay": 150000,
    "status": "AVAILABLE",
    "categoryName": "SUV",
    "branchName": "Sede Bogotá Centro",
    "image": "assets/cars/rav4.jpg"
  }
]
```

---

### 10.2 Obtener Auto por ID

**Endpoint:** `GET /api/admin/cars/{id}`  
**Acceso:** ADMIN  
**Descripción:** Obtiene detalles de un auto específico

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "brand": "Toyota",
  "model": "RAV4",
  "year": 2023,
  "plate": "ABC123",
  "pricePerDay": 150000,
  "status": "AVAILABLE",
  "categoryName": "SUV",
  "branchName": "Sede Bogotá Centro",
  "image": "assets/cars/rav4.jpg"
}
```

**Errores:**
- `404` - Auto no encontrado

---

### 10.3 Crear Auto

**Endpoint:** `POST /api/admin/cars`  
**Acceso:** ADMIN  
**Descripción:** Crea un nuevo auto en el sistema

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "brand": "Honda",
  "model": "CR-V",
  "year": 2024,
  "plate": "XYZ789",
  "pricePerDay": 180000,
  "categoryId": 1,
  "branchId": 2,
  "image": "assets/cars/crv.jpg"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Auto creado exitosamente",
  "data": {
    "id": 10,
    "brand": "Honda",
    "model": "CR-V",
    "year": 2024,
    "plate": "XYZ789",
    "pricePerDay": 180000,
    "status": "AVAILABLE",
    "categoryName": "SUV",
    "branchName": "Sede Medellín",
    "image": "assets/cars/crv.jpg"
  }
}
```

**Errores:**
- `404` - Categoría o sede no encontrada

---

### 10.4 Actualizar Auto

**Endpoint:** `PUT /api/admin/cars/{id}`  
**Acceso:** ADMIN  
**Descripción:** Actualiza los datos de un auto existente

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "brand": "Honda",
  "model": "CR-V",
  "year": 2024,
  "plate": "XYZ789",
  "pricePerDay": 190000,
  "categoryId": 1,
  "branchId": 2,
  "image": "assets/cars/crv.jpg"
}
```

**Response (200 OK):**
```json
{
  "id": 10,
  "brand": "Honda",
  "model": "CR-V",
  "year": 2024,
  "plate": "XYZ789",
  "pricePerDay": 190000,
  "status": "AVAILABLE",
  "categoryName": "SUV",
  "branchName": "Sede Medellín",
  "image": "assets/cars/crv.jpg"
}
```

**Errores:**
- `404` - Auto, categoría o sede no encontrados

---

### 10.5 Eliminar Auto

**Endpoint:** `DELETE /api/admin/cars/{id}`  
**Acceso:** ADMIN  
**Descripción:** Elimina un auto del sistema

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Auto eliminado exitosamente",
  "deletedId": 10
}
```

**Errores:**
- `404` - Auto no encontrado

---

## 11. ADMINISTRACIÓN DE CATEGORÍAS (Admin)

**Autenticación Requerida:** Bearer Token con rol ADMIN

### 11.1 Listar Categorías

**Endpoint:** `GET /api/admin/categories`  
**Acceso:** ADMIN  
**Descripción:** Lista todas las categorías

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "SUV",
    "description": "Vehículos deportivos utilitarios",
    "image": "assets/categories/suv.jpg",
    "carCount": 5
  }
]
```

---

### 11.2 Obtener Categoría por ID

**Endpoint:** `GET /api/admin/categories/{id}`  
**Acceso:** ADMIN  
**Descripción:** Obtiene una categoría específica

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "SUV",
  "description": "Vehículos deportivos utilitarios",
  "image": "assets/categories/suv.jpg",
  "carCount": 5
}
```

**Errores:**
- `404` - Categoría no encontrada

---

### 11.3 Crear Categoría

**Endpoint:** `POST /api/admin/categories`  
**Acceso:** ADMIN  
**Descripción:** Crea una nueva categoría

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "name": "Deportivo",
  "description": "Vehículos deportivos de alto rendimiento",
  "image": "assets/categories/deportivo.jpg"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Categoría creada exitosamente",
  "data": {
    "id": 5,
    "name": "Deportivo",
    "description": "Vehículos deportivos de alto rendimiento",
    "image": "assets/categories/deportivo.jpg",
    "carCount": 0
  }
}
```

---

### 11.4 Actualizar Categoría

**Endpoint:** `PUT /api/admin/categories/{id}`  
**Acceso:** ADMIN  
**Descripción:** Actualiza una categoría existente

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "name": "Deportivo",
  "description": "Vehículos deportivos de lujo",
  "image": "assets/categories/deportivo.jpg"
}
```

**Response (200 OK):**
```json
{
  "id": 5,
  "name": "Deportivo",
  "description": "Vehículos deportivos de lujo",
  "image": "assets/categories/deportivo.jpg",
  "carCount": 0
}
```

**Errores:**
- `404` - Categoría no encontrada

---

### 11.5 Eliminar Categoría

**Endpoint:** `DELETE /api/admin/categories/{id}`  
**Acceso:** ADMIN  
**Descripción:** Elimina una categoría (solo si no tiene autos asociados)

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Categoría eliminada exitosamente",
  "deletedId": 5
}
```

**Errores:**
- `400` - No se puede eliminar una categoría con autos asociados
- `404` - Categoría no encontrada

---

## 12. ADMINISTRACIÓN DE SEDES (Admin)

**Autenticación Requerida:** Bearer Token con rol ADMIN

### 12.1 Listar Sedes

**Endpoint:** `GET /api/admin/branches`  
**Acceso:** ADMIN  
**Descripción:** Lista todas las sedes

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
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
```

---

### 12.2 Obtener Sede por ID

**Endpoint:** `GET /api/admin/branches/{id}`  
**Acceso:** ADMIN  
**Descripción:** Obtiene una sede específica

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Sede Bogotá Centro",
  "address": "Calle 100 #15-20",
  "city": "Bogotá",
  "phone": "+57 1 234 5678",
  "image": "assets/branches/bogota.jpg",
  "carCount": 15
}
```

**Errores:**
- `404` - Sede no encontrada

---

### 12.3 Crear Sede

**Endpoint:** `POST /api/admin/branches`  
**Acceso:** ADMIN  
**Descripción:** Crea una nueva sede

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "name": "Sede Cali",
  "address": "Avenida 6N #28-50",
  "city": "Cali",
  "phone": "+57 2 345 6789",
  "image": "assets/branches/cali.jpg"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Sede creada exitosamente",
  "data": {
    "id": 4,
    "name": "Sede Cali",
    "address": "Avenida 6N #28-50",
    "city": "Cali",
    "phone": "+57 2 345 6789",
    "image": "assets/branches/cali.jpg",
    "carCount": 0
  }
}
```

---

### 12.4 Actualizar Sede

**Endpoint:** `PUT /api/admin/branches/{id}`  
**Acceso:** ADMIN  
**Descripción:** Actualiza una sede existente

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "name": "Sede Cali Centro",
  "address": "Avenida 6N #28-50",
  "city": "Cali",
  "phone": "+57 2 345 6789",
  "image": "assets/branches/cali.jpg"
}
```

**Response (200 OK):**
```json
{
  "id": 4,
  "name": "Sede Cali Centro",
  "address": "Avenida 6N #28-50",
  "city": "Cali",
  "phone": "+57 2 345 6789",
  "image": "assets/branches/cali.jpg",
  "carCount": 0
}
```

**Errores:**
- `404` - Sede no encontrada

---

### 12.5 Eliminar Sede

**Endpoint:** `DELETE /api/admin/branches/{id}`  
**Acceso:** ADMIN  
**Descripción:** Elimina una sede (solo si no tiene autos asociados)

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Sede eliminada exitosamente",
  "deletedId": 4
}
```

**Errores:**
- `400` - No se puede eliminar una sede con autos asociados
- `404` - Sede no encontrada

---

## 13. ADMINISTRACIÓN DE RESERVAS (Admin)

**Autenticación Requerida:** Bearer Token con rol ADMIN

### 13.1 Listar Todas las Reservas

**Endpoint:** `GET /api/admin/reservations`  
**Acceso:** ADMIN  
**Descripción:** Lista todas las reservas del sistema

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `page` (opcional, default: 0) - Número de página
- `size` (opcional, default: 20) - Tamaño de página

**Response (200 OK):**
```json
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
      "paymentStatus": "PENDING",
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
  ]
}
```

---

### 13.2 Crear Reserva para Cliente

**Endpoint:** `POST /api/admin/reservations`  
**Acceso:** ADMIN  
**Descripción:** Crea una reserva para cualquier cliente

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "userId": 2,
  "carId": 5,
  "startDate": "2024-01-15",
  "endDate": "2024-01-20",
  "pickupBranchId": 1,
  "dropoffBranchId": 1
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Reserva creada exitosamente para el cliente",
  "data": {
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
    "paymentStatus": "PENDING",
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
}
```

**Errores:**
- `400` - Auto no disponible / Fechas inválidas
- `404` - Usuario, auto o sede no encontrados

---

### 13.3 Actualizar Estado de Reserva

**Endpoint:** `PUT /api/admin/reservations/{id}/status?status={status}`  
**Acceso:** ADMIN  
**Descripción:** Actualiza el estado de una reserva

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `status` (requerido) - Nuevo estado: CONFIRMED, CANCELLED, COMPLETED, IN_PROGRESS

**Ejemplo:**
```
PUT /api/admin/reservations/1/status?status=COMPLETED
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Estado de reserva actualizado exitosamente",
  "reservationId": 1,
  "newStatus": "COMPLETED"
}
```

**Errores:**
- `400` - Estado inválido
- `404` - Reserva no encontrada

---

## ESTADOS Y ENUMERACIONES

### Estados de Auto (CarStatus)
- `AVAILABLE` - Disponible para renta
- `RENTED` - Actualmente rentado
- `MAINTENANCE` - En mantenimiento
- `OUT_OF_SERVICE` - Fuera de servicio

### Estados de Reserva (ReservationStatus)
- `CONFIRMED` - Reserva confirmada
- `CANCELLED` - Reserva cancelada
- `COMPLETED` - Reserva completada
- `IN_PROGRESS` - Reserva en progreso

### Estados de Pago (PaymentStatus)
- `PENDING` - Pago pendiente
- `PAID` - Pagado
- `REFUNDED` - Reembolsado

### Roles de Usuario
- `CLIENT` - Cliente regular
- `ADMIN` - Administrador del sistema

---

## NOTAS IMPORTANTES

1. **Autenticación JWT**: Todos los endpoints protegidos requieren el header `Authorization: Bearer {token}`
2. **Formato de Fechas**: Usar formato ISO 8601 (YYYY-MM-DD) para fechas
3. **Paginación**: Los endpoints con paginación usan parámetros `page` (base 0) y `size`
4. **Imágenes**: Las rutas de imágenes pueden ser:
   - Rutas relativas: `assets/cars/rav4.jpg`
   - URLs completas: `http://example.com/image.jpg`
   - Rutas de Windows: `C:\Users\...\image.jpg` (convertidas automáticamente en frontend)
5. **Validaciones**: Todos los endpoints validan los datos de entrada y retornan errores descriptivos
6. **CORS**: El backend está configurado para aceptar peticiones desde `http://localhost:5173`
7. **Seguridad**: Las contraseñas se almacenan encriptadas con BCrypt
8. **Tokens JWT**: Los tokens tienen una duración configurable y contienen email y rol del usuario

---

**Versión:** 1.0  
**Última Actualización:** Enero 2024  
**Desarrollado por:** AutoReserve Team
