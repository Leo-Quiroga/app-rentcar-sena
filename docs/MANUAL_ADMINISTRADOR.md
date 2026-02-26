# MANUAL DE ADMINISTRADOR - AUTORESERVE

## Información del Documento
- **Versión:** 1.0
- **Fecha:** Enero 2025
- **Proyecto:** AutoReserve - Sistema de Reserva de Vehículos

---

## 1. INTRODUCCIÓN

### 1.1 Propósito del Manual

Este manual está dirigido a los administradores del sistema AutoReserve y describe todas las funcionalidades del panel administrativo para gestionar:
- Usuarios y roles
- Vehículos
- Categorías
- Sedes
- Reservas
- Estadísticas del sistema

### 1.2 Roles en el Sistema

| Rol | Permisos | Acceso |
|-----|----------|--------|
| **ADMIN** | Acceso completo al panel administrativo | Todas las funciones |
| **CLIENT** | Funciones de usuario final | Solo área pública y perfil |

### 1.3 Credenciales de Administrador por Defecto

**⚠️ IMPORTANTE:** Cambiar estas credenciales después del primer acceso

- **Email:** `admin@autoreserve.com`
- **Contraseña:** `admin123`

---

## 2. ACCESO AL PANEL ADMINISTRATIVO

### 2.1 Iniciar Sesión como Administrador

**Paso 1:** Accede a la página principal de AutoReserve

**Paso 2:** Haz clic en "Iniciar Sesión"

**Paso 3:** Ingresa las credenciales de administrador

**Paso 4:** Serás redirigido automáticamente al Dashboard Administrativo

**URL del Panel:** `https://tudominio.com/admin/dashboard`

### 2.2 Navegación del Panel Administrativo

El panel administrativo tiene un menú lateral con las siguientes secciones:

```
📊 Dashboard
👥 Usuarios
🚗 Autos
📁 Categorías
🏢 Sedes
📅 Reservas
📈 Estadísticas
⚙️ Configuración
```

---

## 3. DASHBOARD ADMINISTRATIVO

### 3.1 Vista General

El dashboard muestra métricas clave del sistema:

**Tarjetas de Resumen:**
- **Total de Usuarios:** Cantidad de usuarios registrados
- **Total de Autos:** Vehículos en el sistema
- **Reservas Activas:** Reservas confirmadas y en progreso
- **Ingresos del Mes:** Total facturado en el mes actual

**Gráficos:**
- Reservas por mes (últimos 6 meses)
- Autos más rentados
- Categorías más populares
- Distribución de reservas por sede

**Actividad Reciente:**
- Últimas reservas creadas
- Nuevos usuarios registrados
- Autos agregados recientemente

### 3.2 Filtros de Fecha

Puedes filtrar las estadísticas por:
- Hoy
- Esta semana
- Este mes
- Últimos 3 meses
- Último año
- Rango personalizado

---

## 4. GESTIÓN DE USUARIOS

### 4.1 Listar Usuarios

**Ruta:** Panel Admin → Usuarios

**Información mostrada:**
- ID del usuario
- Nombre completo
- Correo electrónico
- Teléfono
- Rol (CLIENT/ADMIN)
- Fecha de registro
- Acciones disponibles

**Funciones:**
- 🔍 Buscar por nombre o email
- 📄 Paginación (10, 20, 50 usuarios por página)
- ⬇️ Exportar lista a CSV
- ➕ Crear nuevo usuario

### 4.2 Crear Usuario

**Paso 1:** Haz clic en "Nuevo Usuario"

**Paso 2:** Completa el formulario:
- **Nombre:** Nombre del usuario
- **Apellido:** Apellido del usuario
- **Email:** Correo electrónico único
- **Teléfono:** Número de contacto
- **Contraseña:** Contraseña inicial (mínimo 6 caracteres)
- **Rol:** Seleccionar CLIENT o ADMIN

**Paso 3:** Haz clic en "Crear Usuario"

**Paso 4:** El usuario recibirá un correo de bienvenida (si SMTP está configurado)

**Validaciones:**
- ✅ Email debe ser único
- ✅ Todos los campos son obligatorios
- ✅ Email debe tener formato válido

### 4.3 Editar Usuario

**Paso 1:** En la lista de usuarios, haz clic en el ícono de editar ✏️

**Paso 2:** Modifica los campos necesarios:
- Nombre y apellido
- Teléfono
- Rol
- Contraseña (opcional, dejar vacío para no cambiar)

**Paso 3:** Haz clic en "Actualizar Usuario"

**Nota:** No se puede cambiar el email de un usuario existente.

### 4.4 Eliminar Usuario

**Paso 1:** En la lista de usuarios, haz clic en el ícono de eliminar 🗑️

**Paso 2:** Confirma la eliminación en el diálogo

**Restricciones:**
- ❌ No se puede eliminar usuarios con rol ADMIN
- ❌ No se puede eliminar usuarios con reservas activas
- ✅ Se pueden eliminar usuarios CLIENT sin reservas activas

### 4.5 Ver Detalle de Usuario

**Paso 1:** Haz clic en el nombre del usuario

**Información completa:**
- Datos personales
- Historial de reservas
- Autos favoritos
- Fecha de último acceso
- Total gastado

**Acciones disponibles:**
- Editar información
- Ver todas sus reservas
- Cambiar rol
- Desactivar cuenta

### 4.6 Asignar Roles

**Para cambiar el rol de un usuario:**

**Paso 1:** Edita el usuario

**Paso 2:** Selecciona el nuevo rol:
- **CLIENT:** Usuario regular con permisos básicos
- **ADMIN:** Acceso completo al panel administrativo

**Paso 3:** Guarda los cambios

**⚠️ PRECAUCIÓN:** Asignar rol ADMIN da acceso completo al sistema.

---

## 5. GESTIÓN DE AUTOS

### 5.1 Listar Autos

**Ruta:** Panel Admin → Autos

**Información mostrada:**
- Imagen del vehículo
- Marca y modelo
- Año
- Placa
- Precio por día
- Estado (DISPONIBLE, RENTADO, MANTENIMIENTO)
- Categoría
- Sede
- Acciones

**Filtros disponibles:**
- Por categoría
- Por sede
- Por estado
- Por rango de precio

### 5.2 Crear Auto

**Paso 1:** Haz clic en "Nuevo Auto"

**Paso 2:** Completa el formulario:

**Información Básica:**
- **Marca:** Ej: Toyota, Honda, BMW
- **Modelo:** Ej: RAV4, CR-V, Serie 5
- **Año:** Año del vehículo (2015-2025)
- **Placa:** Placa única del vehículo

**Información Comercial:**
- **Precio por día:** En pesos colombianos
- **Categoría:** Seleccionar de la lista
- **Sede:** Ubicación del vehículo

**Multimedia:**
- **Imagen:** URL o ruta de la imagen principal
  - Formato: `assets/cars/nombre.jpg`
  - Tamaño recomendado: 800x600px

**Paso 3:** Haz clic en "Crear Auto"

**Validaciones:**
- ✅ Placa debe ser única
- ✅ Precio debe ser mayor a 0
- ✅ Categoría y sede deben existir

### 5.3 Editar Auto

**Paso 1:** Haz clic en el ícono de editar ✏️

**Paso 2:** Modifica los campos necesarios

**Paso 3:** Haz clic en "Actualizar Auto"

**Campos editables:**
- Marca, modelo, año
- Precio por día
- Categoría
- Sede
- Imagen
- Estado

### 5.4 Cambiar Estado de Auto

Los estados disponibles son:

| Estado | Descripción | Visible para Clientes |
|--------|-------------|----------------------|
| **AVAILABLE** | Disponible para renta | ✅ Sí |
| **RENTED** | Actualmente rentado | ❌ No |
| **MAINTENANCE** | En mantenimiento | ❌ No |
| **OUT_OF_SERVICE** | Fuera de servicio | ❌ No |

**Para cambiar el estado:**

**Paso 1:** Edita el auto

**Paso 2:** Selecciona el nuevo estado

**Paso 3:** Guarda los cambios

**Nota:** El sistema cambia automáticamente a RENTED cuando hay una reserva activa.

### 5.5 Eliminar Auto

**Paso 1:** Haz clic en el ícono de eliminar 🗑️

**Paso 2:** Confirma la eliminación

**Restricciones:**
- ❌ No se puede eliminar autos con reservas activas
- ❌ No se puede eliminar autos en estado RENTED
- ✅ Se pueden eliminar autos AVAILABLE sin reservas

### 5.6 Ver Calendario de Disponibilidad

**Paso 1:** Haz clic en "Ver Calendario" en el auto

**Paso 2:** Verás un calendario con:
- Días disponibles (verde)
- Días reservados (rojo)
- Reservas futuras con detalles

**Funciones:**
- Navegar por meses
- Ver detalles de cada reserva
- Identificar períodos de alta demanda

### 5.7 Gestión de Imágenes

**Formatos soportados:**
- JPG, JPEG, PNG
- Tamaño máximo: 5MB
- Resolución recomendada: 800x600px

**Ubicación de imágenes:**
- Frontend: `autoreserve-frontend/src/assets/cars/`
- Formato de ruta: `assets/cars/nombre.jpg`

**Para agregar nuevas imágenes:**

**Paso 1:** Coloca la imagen en la carpeta `assets/cars/`

**Paso 2:** Al crear/editar el auto, usa la ruta: `assets/cars/nombre.jpg`

---

## 6. GESTIÓN DE CATEGORÍAS

### 6.1 Listar Categorías

**Ruta:** Panel Admin → Categorías

**Información mostrada:**
- Imagen de la categoría
- Nombre
- Descripción
- Cantidad de autos
- Acciones

### 6.2 Crear Categoría

**Paso 1:** Haz clic en "Nueva Categoría"

**Paso 2:** Completa el formulario:
- **Nombre:** Ej: SUV, Sedán, Deportivo
- **Descripción:** Descripción breve de la categoría
- **Imagen:** Ruta de la imagen representativa
  - Formato: `assets/categories/nombre.jpg`

**Paso 3:** Haz clic en "Crear Categoría"

**Ejemplos de categorías:**
- **SUV:** Vehículos deportivos utilitarios
- **Sedán:** Autos elegantes y cómodos
- **Compacto:** Autos pequeños y económicos
- **Pickup:** Camionetas de carga
- **Lujo:** Vehículos premium

### 6.3 Editar Categoría

**Paso 1:** Haz clic en el ícono de editar ✏️

**Paso 2:** Modifica nombre, descripción o imagen

**Paso 3:** Haz clic en "Actualizar Categoría"

### 6.4 Eliminar Categoría

**Paso 1:** Haz clic en el ícono de eliminar 🗑️

**Paso 2:** Confirma la eliminación

**Restricciones:**
- ❌ No se puede eliminar categorías con autos asociados
- ✅ Primero debes reasignar o eliminar los autos de esa categoría

### 6.5 Ver Autos por Categoría

**Paso 1:** Haz clic en "Ver Autos" en la categoría

**Paso 2:** Verás todos los autos de esa categoría

**Funciones:**
- Editar autos directamente
- Cambiar categoría de múltiples autos
- Ver estadísticas de la categoría

---

## 7. GESTIÓN DE SEDES

### 7.1 Listar Sedes

**Ruta:** Panel Admin → Sedes

**Información mostrada:**
- Imagen de la sede
- Nombre
- Dirección completa
- Ciudad
- Teléfono
- Cantidad de autos
- Acciones

### 7.2 Crear Sede

**Paso 1:** Haz clic en "Nueva Sede"

**Paso 2:** Completa el formulario:
- **Nombre:** Ej: Sede Bogotá Centro
- **Dirección:** Dirección completa
- **Ciudad:** Ciudad donde está ubicada
- **Teléfono:** Teléfono de contacto
- **Imagen:** Ruta de la imagen
  - Formato: `assets/branches/nombre.jpg`

**Paso 3:** Haz clic en "Crear Sede"

**Ejemplo:**
```
Nombre: Sede Bogotá Centro
Dirección: Calle 100 #15-20
Ciudad: Bogotá
Teléfono: +57 1 234 5678
Imagen: assets/branches/bogota.jpg
```

### 7.3 Editar Sede

**Paso 1:** Haz clic en el ícono de editar ✏️

**Paso 2:** Modifica los campos necesarios

**Paso 3:** Haz clic en "Actualizar Sede"

### 7.4 Eliminar Sede

**Paso 1:** Haz clic en el ícono de eliminar 🗑️

**Paso 2:** Confirma la eliminación

**Restricciones:**
- ❌ No se puede eliminar sedes con autos asociados
- ❌ No se puede eliminar sedes con reservas activas
- ✅ Primero debes reasignar los autos a otra sede

### 7.5 Ver Autos por Sede

**Paso 1:** Haz clic en "Ver Autos" en la sede

**Paso 2:** Verás todos los autos de esa sede

**Funciones:**
- Transferir autos entre sedes
- Ver disponibilidad por sede
- Estadísticas de la sede

---

## 8. GESTIÓN DE RESERVAS

### 8.1 Listar Todas las Reservas

**Ruta:** Panel Admin → Reservas

**Información mostrada:**
- ID de reserva
- Cliente (nombre y email)
- Vehículo (marca y modelo)
- Fechas de inicio y fin
- Estado
- Total
- Acciones

**Filtros disponibles:**
- Por estado (CONFIRMADA, CANCELADA, COMPLETADA, EN_PROGRESO)
- Por fecha
- Por cliente
- Por vehículo
- Por sede

### 8.2 Estados de Reserva

| Estado | Descripción | Acciones Disponibles |
|--------|-------------|---------------------|
| **CONFIRMED** | Reserva confirmada | Cambiar a EN_PROGRESO, CANCELAR |
| **IN_PROGRESS** | Cliente usando el auto | Cambiar a COMPLETADA |
| **COMPLETED** | Reserva finalizada | Ver detalles, generar factura |
| **CANCELLED** | Reserva cancelada | Ver detalles |

### 8.3 Ver Detalle de Reserva

**Paso 1:** Haz clic en el ID de la reserva

**Información completa:**
- Datos del cliente
- Datos del vehículo
- Fechas exactas
- Sedes de recogida/devolución
- Precio desglosado
- Estado de pago
- Historial de cambios

### 8.4 Crear Reserva para Cliente

**Paso 1:** Haz clic en "Nueva Reserva"

**Paso 2:** Completa el formulario:
- **Cliente:** Seleccionar de la lista
- **Vehículo:** Seleccionar auto disponible
- **Fecha de inicio:** Fecha de recogida
- **Fecha de fin:** Fecha de devolución
- **Sede de recogida:** Dónde recoge el auto
- **Sede de devolución:** Dónde devuelve el auto

**Paso 3:** Revisa el resumen:
- Días totales
- Precio por día
- Total a pagar

**Paso 4:** Haz clic en "Crear Reserva"

**Validaciones:**
- ✅ El auto debe estar disponible en esas fechas
- ✅ Las fechas no pueden estar en el pasado
- ✅ La fecha de fin debe ser posterior a la de inicio

### 8.5 Cambiar Estado de Reserva

**Para cambiar el estado:**

**Paso 1:** Desde el detalle de la reserva, selecciona el nuevo estado

**Paso 2:** Confirma el cambio

**Flujo típico:**
```
CONFIRMED → IN_PROGRESS → COMPLETED
     ↓
  CANCELLED
```

**Reglas:**
- Solo CONFIRMED puede cambiar a IN_PROGRESS
- Solo IN_PROGRESS puede cambiar a COMPLETED
- CONFIRMED puede cambiar a CANCELLED
- COMPLETED y CANCELLED son estados finales

### 8.6 Cancelar Reserva

**Paso 1:** Desde el detalle de la reserva, haz clic en "Cancelar Reserva"

**Paso 2:** Ingresa el motivo de cancelación

**Paso 3:** Confirma la cancelación

**Efectos:**
- El auto vuelve a estar disponible
- Se notifica al cliente (si SMTP está configurado)
- Se aplica política de reembolso según fecha

### 8.7 Generar Factura

**Paso 1:** Desde una reserva COMPLETED, haz clic en "Generar Factura"

**Paso 2:** La factura se genera automáticamente con:
- Datos del cliente
- Detalles de la reserva
- Desglose de precios
- Total pagado
- Fecha de emisión

**Paso 3:** Puedes:
- Descargar en PDF
- Enviar por correo al cliente
- Imprimir

---

## 9. ESTADÍSTICAS Y REPORTES

### 9.1 Dashboard de Estadísticas

**Ruta:** Panel Admin → Estadísticas

**Métricas disponibles:**

**Usuarios:**
- Total de usuarios registrados
- Nuevos usuarios por mes
- Usuarios activos vs inactivos
- Distribución por ciudad

**Autos:**
- Total de autos en el sistema
- Autos por categoría
- Autos por sede
- Tasa de ocupación por auto

**Reservas:**
- Total de reservas
- Reservas por mes
- Tasa de cancelación
- Duración promedio de renta

**Ingresos:**
- Ingresos totales
- Ingresos por mes
- Ingresos por categoría
- Ingresos por sede
- Ticket promedio

### 9.2 Reportes Predefinidos

**Reporte de Ventas:**
- Ingresos por período
- Comparación con períodos anteriores
- Proyecciones

**Reporte de Ocupación:**
- Porcentaje de ocupación por auto
- Autos más rentados
- Autos menos rentados

**Reporte de Clientes:**
- Clientes más frecuentes
- Clientes nuevos
- Clientes inactivos

**Reporte de Sedes:**
- Rendimiento por sede
- Comparación entre sedes
- Autos por sede

### 9.3 Exportar Reportes

**Formatos disponibles:**
- CSV (Excel)
- PDF
- JSON

**Paso 1:** Selecciona el reporte

**Paso 2:** Configura filtros y fechas

**Paso 3:** Haz clic en "Exportar"

**Paso 4:** Selecciona el formato

---

## 10. CONFIGURACIÓN DEL SISTEMA

### 10.1 Configuración General

**Ruta:** Panel Admin → Configuración

**Opciones disponibles:**

**Información de la Empresa:**
- Nombre de la empresa
- Logo
- Información de contacto
- Redes sociales

**Configuración de Reservas:**
- Días mínimos de anticipación
- Días máximos de renta
- Hora de recogida/devolución
- Política de cancelación

**Configuración de Precios:**
- Descuentos por días
- Recargos por temporada alta
- Impuestos aplicables

### 10.2 Configuración de Notificaciones

**Correos Electrónicos:**
- Confirmación de registro
- Confirmación de reserva
- Recordatorio de recogida
- Confirmación de devolución
- Cancelación de reserva

**Para cada tipo de correo puedes:**
- Activar/desactivar
- Editar plantilla
- Configurar destinatarios adicionales

### 10.3 Configuración de Seguridad

**Opciones:**
- Tiempo de expiración de sesión
- Intentos de login permitidos
- Política de contraseñas
- Autenticación de dos factores (2FA)

### 10.4 Mantenimiento del Sistema

**Tareas de mantenimiento:**

**Limpiar Datos:**
- Eliminar reservas antiguas canceladas
- Limpiar logs antiguos
- Optimizar base de datos

**Backup:**
- Configurar backup automático
- Descargar backup manual
- Restaurar desde backup

**Logs del Sistema:**
- Ver logs de errores
- Ver logs de acceso
- Ver logs de auditoría

---

## 11. MEJORES PRÁCTICAS

### 11.1 Gestión de Usuarios

✅ **Revisar periódicamente** los usuarios inactivos

✅ **Asignar rol ADMIN** solo a personal de confianza

✅ **Cambiar contraseñas** de administradores cada 3 meses

✅ **Auditar acciones** de administradores regularmente

### 11.2 Gestión de Autos

✅ **Mantener actualizado** el estado de los autos

✅ **Programar mantenimiento** preventivo

✅ **Actualizar precios** según temporada

✅ **Revisar disponibilidad** antes de confirmar reservas manuales

### 11.3 Gestión de Reservas

✅ **Confirmar reservas** dentro de las 24 horas

✅ **Contactar al cliente** antes de la fecha de recogida

✅ **Verificar documentos** al momento de la entrega

✅ **Inspeccionar el auto** antes y después de cada renta

### 11.4 Seguridad

✅ **Cerrar sesión** al terminar

✅ **No compartir** credenciales de administrador

✅ **Revisar logs** de acceso regularmente

✅ **Mantener actualizado** el sistema

---

## 12. SOLUCIÓN DE PROBLEMAS

### 12.1 No Puedo Eliminar un Auto

**Problema:** "No se puede eliminar un auto con reservas activas"

**Solución:**
1. Verifica si el auto tiene reservas activas
2. Espera a que las reservas se completen
3. O cambia el estado a OUT_OF_SERVICE en lugar de eliminar

### 12.2 No Puedo Cambiar el Estado de una Reserva

**Problema:** "Estado inválido"

**Solución:**
- Verifica el flujo de estados permitido
- Solo puedes cambiar según las reglas definidas

### 12.3 Las Estadísticas no se Actualizan

**Problema:** Los números no coinciden

**Solución:**
1. Refresca la página
2. Limpia el caché del navegador
3. Verifica los filtros de fecha aplicados

### 12.4 No Puedo Crear una Reserva

**Problema:** "Auto no disponible"

**Solución:**
1. Verifica el calendario del auto
2. Asegúrate de que no haya reservas en esas fechas
3. Verifica que el auto esté en estado AVAILABLE

---

## 13. ATAJOS DE TECLADO

| Atajo | Acción |
|-------|--------|
| `Ctrl + K` | Búsqueda rápida |
| `Ctrl + N` | Nuevo registro (según sección) |
| `Ctrl + S` | Guardar cambios |
| `Esc` | Cerrar modal |
| `F5` | Refrescar datos |

---

## 14. GLOSARIO ADMINISTRATIVO

- **Dashboard:** Panel principal con métricas
- **CRUD:** Crear, Leer, Actualizar, Eliminar
- **Estado:** Situación actual de un registro
- **Rol:** Nivel de permisos de un usuario
- **Sede:** Ubicación física de operaciones
- **Tasa de ocupación:** Porcentaje de tiempo que un auto está rentado
- **Ticket promedio:** Valor promedio de una reserva

---

## 15. AUDITORÍA Y CUMPLIMIENTO

### 15.1 Logs de Auditoría

El sistema registra automáticamente:
- Quién hizo qué acción
- Cuándo se realizó
- Desde qué IP
- Qué datos se modificaron

**Para ver logs:**
Panel Admin → Configuración → Logs de Auditoría

### 15.2 Reportes de Cumplimiento

Genera reportes para:
- Auditorías internas
- Cumplimiento fiscal
- Análisis de rendimiento
- Toma de decisiones

---

## 16. SOPORTE TÉCNICO

### 16.1 Contacto para Administradores

**Soporte Técnico Prioritario:**
- Email: admin-support@autoreserve.com
- Teléfono: +57 1 234 5678 (Ext. 100)
- Horario: 24/7

**Documentación Técnica:**
- Manual de Instalación
- Manual de Despliegue
- Documentación de API
- Manual de Mantenimiento

### 16.2 Escalamiento de Problemas

**Nivel 1:** Problemas de uso → Consultar este manual

**Nivel 2:** Problemas técnicos → Contactar soporte técnico

**Nivel 3:** Problemas críticos → Contactar equipo de desarrollo

---

**¡Gracias por administrar AutoReserve!**

Este manual se actualiza regularmente. Consulta la versión más reciente en la documentación del proyecto.

---

**Documento elaborado por:** Equipo AutoReserve  
**Última actualización:** Enero 2025  
**Versión:** 1.0
