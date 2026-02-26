# DOCUMENTACIÓN TÉCNICA - AUTORESERVE

## Índice de Manuales

Esta carpeta contiene toda la documentación técnica del proyecto AutoReserve. A continuación se describe cada manual y su propósito.

---

## 📚 Manuales Disponibles

### 1. [Manual de Instalación](MANUAL_INSTALACION.md)
**Audiencia:** Desarrolladores, Administradores de Sistemas

**Contenido:**
- Requisitos previos (software y hardware)
- Instalación de dependencias (Java, Node.js, MySQL, Maven)
- Configuración de base de datos
- Instalación del backend
- Instalación del frontend
- Verificación de la instalación
- Solución de problemas comunes
- Configuración adicional (SMTP, logs, pool de conexiones)

**Cuándo usar:** Al instalar AutoReserve por primera vez en un ambiente de desarrollo o pruebas.

---

### 2. [Manual de Despliegue](MANUAL_DESPLIEGUE.md)
**Audiencia:** DevOps, Administradores de Sistemas

**Contenido:**
- Preparación para producción
- Configuración del servidor
- Despliegue del backend (Spring Boot como servicio)
- Despliegue del frontend (Nginx)
- Configuración de SSL con Let's Encrypt
- Base de datos en producción
- Monitoreo y logs
- Backup y recuperación
- Seguridad adicional
- Optimización de rendimiento
- Plan de rollback

**Cuándo usar:** Al desplegar AutoReserve en un servidor de producción.

---

### 3. [Manual de Usuario](MANUAL_USUARIO.md)
**Audiencia:** Usuarios finales (clientes)

**Contenido:**
- Introducción al sistema
- Registro e inicio de sesión
- Explorar vehículos
- Realizar reservas
- Gestionar reservas
- Favoritos
- Perfil de usuario
- Explorar categorías y sedes
- Búsqueda avanzada
- Contacto y soporte
- Políticas y términos
- Preguntas frecuentes

**Cuándo usar:** Para capacitar a usuarios finales o como guía de referencia para clientes.

---

### 4. [Manual de Administrador](MANUAL_ADMINISTRADOR.md)
**Audiencia:** Administradores del sistema

**Contenido:**
- Acceso al panel administrativo
- Dashboard y estadísticas
- Gestión de usuarios (crear, editar, eliminar, asignar roles)
- Gestión de autos (CRUD, estados, calendario)
- Gestión de categorías
- Gestión de sedes
- Gestión de reservas
- Estadísticas y reportes
- Configuración del sistema
- Mejores prácticas
- Solución de problemas
- Auditoría y cumplimiento

**Cuándo usar:** Para administradores que gestionan el sistema diariamente.

---

### 5. [Manual de Desarrollador](MANUAL_DESARROLLADOR.md)
**Audiencia:** Desarrolladores

**Contenido:**
- Arquitectura del sistema
- Estructura del proyecto (backend y frontend)
- Convenciones de código
- Guía de desarrollo
- Testing (JUnit, React Testing Library)
- Git workflow y convenciones de commits
- API REST guidelines
- Seguridad (JWT, validación)
- Performance y optimización
- Deployment
- Troubleshooting
- Recursos adicionales

**Cuándo usar:** Para desarrolladores que contribuirán al código del proyecto.

---

### 6. [Manual de Mantenimiento](MANUAL_MANTENIMIENTO.md)
**Audiencia:** Administradores de Sistemas, DevOps, Soporte Técnico

**Contenido:**
- Monitoreo del sistema
- Backup y recuperación
- Troubleshooting detallado
- Mantenimiento preventivo (diario, semanal, mensual)
- Optimización de rendimiento
- Seguridad y auditoría
- Disaster recovery
- Contactos de emergencia

**Cuándo usar:** Para operaciones diarias, troubleshooting y mantenimiento del sistema en producción.

---

## 🗂️ Documentación Adicional

### [API Documentation](../autoreserve-backend/API_DOCUMENTATION.md)
Documentación completa de todos los endpoints REST del backend, incluyendo:
- Autenticación
- Categorías
- Sedes
- Autos
- Búsqueda
- Reservas
- Favoritos
- Perfil de usuario
- Administración (usuarios, autos, categorías, sedes, reservas)

---

## 📋 Guía Rápida de Uso

### Para Instalación Inicial
1. Leer [Manual de Instalación](MANUAL_INSTALACION.md)
2. Seguir los pasos en orden
3. Verificar que todo funcione correctamente

### Para Despliegue en Producción
1. Completar instalación en desarrollo
2. Leer [Manual de Despliegue](MANUAL_DESPLIEGUE.md)
3. Preparar servidor de producción
4. Seguir checklist de despliegue
5. Configurar monitoreo y backups

### Para Desarrollo
1. Leer [Manual de Instalación](MANUAL_INSTALACION.md)
2. Leer [Manual de Desarrollador](MANUAL_DESARROLLADOR.md)
3. Configurar entorno de desarrollo
4. Revisar [API Documentation](../autoreserve-backend/API_DOCUMENTATION.md)
5. Seguir convenciones de código

### Para Operaciones
1. Leer [Manual de Mantenimiento](MANUAL_MANTENIMIENTO.md)
2. Configurar monitoreo
3. Configurar backups automáticos
4. Establecer rutinas de mantenimiento

---

## 🔄 Actualizaciones de Documentación

**Versión Actual:** 1.0  
**Última Actualización:** Enero 2025

### Historial de Cambios

| Versión | Fecha | Cambios |
|---------|-------|---------|
| 1.0 | Enero 2025 | Versión inicial de todos los manuales |

---

## 📞 Soporte

Para preguntas sobre la documentación:
- **Email:** docs@autoreserve.com
- **Equipo de Desarrollo:** dev@autoreserve.com

Para reportar errores en la documentación:
- Crear un issue en el repositorio
- Enviar correo a docs@autoreserve.com

---

## 📝 Contribuir a la Documentación

Si encuentras errores o deseas mejorar la documentación:

1. Identifica el manual a actualizar
2. Realiza los cambios necesarios
3. Actualiza la versión y fecha
4. Documenta los cambios en el historial
5. Crea un pull request

---

## ✅ Checklist de Documentación

### Para Nuevas Funcionalidades
- [ ] Actualizar Manual de Usuario (si aplica)
- [ ] Actualizar Manual de Administrador (si aplica)
- [ ] Actualizar Manual de Desarrollador
- [ ] Actualizar API Documentation
- [ ] Actualizar este README si es necesario

### Para Cambios en Infraestructura
- [ ] Actualizar Manual de Instalación
- [ ] Actualizar Manual de Despliegue
- [ ] Actualizar Manual de Mantenimiento

---

## 📖 Convenciones de Documentación

- Usar Markdown para todos los documentos
- Incluir tabla de contenidos en documentos largos
- Usar ejemplos de código cuando sea apropiado
- Incluir capturas de pantalla para interfaces de usuario
- Mantener un tono profesional pero accesible
- Actualizar la fecha y versión en cada cambio

---

**Proyecto:** AutoReserve - Sistema de Reserva de Vehículos  
**Equipo:** AutoReserve Development Team  
**Última Actualización:** Febrero 2026
