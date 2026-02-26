# MANUAL DE INSTALACIÓN - AUTORESERVE

## Información del Documento
- **Versión:** 1.0
- **Fecha:** Enero 2025
- **Proyecto:** AutoReserve - Sistema de Reserva de Vehículos

---

## 1. REQUISITOS PREVIOS

### 1.1 Software Requerido

| Software | Versión Mínima | Versión Recomendada | Propósito |
|----------|----------------|---------------------|-----------|
| **Java JDK** | 17 | 21 | Ejecución del backend |
| **Node.js** | 18.x | 20.x o superior | Ejecución del frontend |
| **MySQL** | 8.0 | 8.0 o superior | Base de datos |
| **Maven** | 3.8 | 3.9 o superior | Gestión de dependencias backend |
| **Git** | 2.30 | Última versión | Control de versiones |

### 1.2 Hardware Recomendado

- **Procesador:** Intel Core i5 o equivalente (mínimo)
- **RAM:** 8 GB (mínimo), 16 GB (recomendado)
- **Disco Duro:** 10 GB de espacio libre
- **Conexión a Internet:** Requerida para descargar dependencias

### 1.3 Sistemas Operativos Soportados

- Windows 10/11
- macOS 10.15 o superior
- Linux (Ubuntu 20.04+, Debian, CentOS)

---

## 2. INSTALACIÓN DE DEPENDENCIAS

### 2.1 Instalación de Java JDK

#### Windows:
1. Descargar JDK desde: https://www.oracle.com/java/technologies/downloads/
2. Ejecutar el instalador
3. Configurar variable de entorno `JAVA_HOME`:
   ```cmd
   setx JAVA_HOME "C:\Program Files\Java\jdk-21"
   setx PATH "%PATH%;%JAVA_HOME%\bin"
   ```
4. Verificar instalación:
   ```cmd
   java -version
   ```

#### Linux/macOS:
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# macOS (con Homebrew)
brew install openjdk@21

# Verificar
java -version
```

### 2.2 Instalación de Node.js

#### Windows:
1. Descargar desde: https://nodejs.org/
2. Ejecutar el instalador (incluye npm)
3. Verificar:
   ```cmd
   node -v
   npm -v
   ```

#### Linux/macOS:
```bash
# Ubuntu/Debian
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt-get install -y nodejs

# macOS (con Homebrew)
brew install node

# Verificar
node -v
npm -v
```

### 2.3 Instalación de MySQL

#### Windows:
1. Descargar MySQL Installer desde: https://dev.mysql.com/downloads/installer/
2. Ejecutar el instalador
3. Seleccionar "Developer Default"
4. Configurar contraseña root
5. Iniciar MySQL Server

#### Linux:
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation

# Iniciar servicio
sudo systemctl start mysql
sudo systemctl enable mysql
```

#### macOS:
```bash
brew install mysql
brew services start mysql
mysql_secure_installation
```

### 2.4 Instalación de Maven

#### Windows:
1. Descargar desde: https://maven.apache.org/download.cgi
2. Extraer en `C:\Program Files\Apache\maven`
3. Configurar variables de entorno:
   ```cmd
   setx MAVEN_HOME "C:\Program Files\Apache\maven"
   setx PATH "%PATH%;%MAVEN_HOME%\bin"
   ```
4. Verificar:
   ```cmd
   mvn -version
   ```

#### Linux/macOS:
```bash
# Ubuntu/Debian
sudo apt install maven

# macOS
brew install maven

# Verificar
mvn -version
```

---

## 3. CONFIGURACIÓN DE LA BASE DE DATOS

### 3.1 Crear Base de Datos

```sql
-- Conectar a MySQL
mysql -u root -p

-- Crear base de datos
CREATE DATABASE autoreserve_app_bd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario (opcional, recomendado para producción)
CREATE USER 'autoreserve_user'@'localhost' IDENTIFIED BY 'password_seguro';
GRANT ALL PRIVILEGES ON autoreserve_app_bd.* TO 'autoreserve_user'@'localhost';
FLUSH PRIVILEGES;

-- Verificar
SHOW DATABASES;
USE autoreserve_app_bd;
```

### 3.2 Configuración de Conexión

El archivo `application.properties` ya está configurado. Si usas credenciales diferentes, edita:

**Ubicación:** `autoreserve-backend/src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/autoreserve_app_bd?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=1234
```

---

## 4. INSTALACIÓN DEL BACKEND

### 4.1 Clonar/Ubicar el Proyecto

```bash
cd c:\Users\NatyLeo\Desktop\autoreserve-app-rentcar\autoreserve-backend
```

### 4.2 Instalar Dependencias Maven

```bash
mvn clean install
```

Este comando:
- Descarga todas las dependencias
- Compila el código
- Ejecuta los tests
- Genera el archivo JAR

**Tiempo estimado:** 2-5 minutos (primera vez)

### 4.3 Verificar Configuración

Revisar que el archivo `application.properties` tenga las credenciales correctas de MySQL.

### 4.4 Ejecutar el Backend

```bash
# Opción 1: Con Maven
mvn spring-boot:run

# Opción 2: Con el JAR generado
java -jar target/autoreserve-backend-0.0.1-SNAPSHOT.jar
```

### 4.5 Verificar que el Backend está Corriendo

- El servidor debe iniciar en: `http://localhost:8080`
- Verifica en la consola el mensaje: `Started AutoreserveBackendApplication`
- Prueba el endpoint: `http://localhost:8080/api/categories`

---

## 5. INSTALACIÓN DEL FRONTEND

### 5.1 Navegar al Directorio Frontend

```bash
cd c:\Users\NatyLeo\Desktop\autoreserve-app-rentcar\autoreserve-frontend
```

### 5.2 Instalar Dependencias NPM

```bash
npm install
```

Este comando instala:
- React 19
- React Router DOM
- TailwindCSS
- Vite
- Todas las dependencias del proyecto

**Tiempo estimado:** 1-3 minutos

### 5.3 Configurar Variables de Entorno (Opcional)

Si necesitas cambiar la URL del backend, crea un archivo `.env`:

```bash
# .env
VITE_API_URL=http://localhost:8080
```

### 5.4 Ejecutar el Frontend

```bash
npm run dev
```

### 5.5 Verificar que el Frontend está Corriendo

- El servidor debe iniciar en: `http://localhost:5173`
- Abre el navegador en esa URL
- Deberías ver la página principal de AutoReserve

---

## 6. VERIFICACIÓN DE LA INSTALACIÓN

### 6.1 Checklist de Verificación

- [ ] Backend corriendo en `http://localhost:8080`
- [ ] Frontend corriendo en `http://localhost:5173`
- [ ] Base de datos MySQL activa
- [ ] Puedes ver la lista de categorías en el frontend
- [ ] Puedes registrar un nuevo usuario
- [ ] Puedes iniciar sesión

### 6.2 Datos de Prueba

El sistema crea automáticamente:
- **Usuario Admin:**
  - Email: `admin@autoreserve.com`
  - Password: `admin123`
  
- **Categorías:** SUV, Sedán, Compacto, Pickup, Lujo
- **Sedes:** Bogotá, Medellín, Cali
- **Autos:** 25 vehículos de ejemplo

### 6.3 Probar Funcionalidades Básicas

1. **Acceso Público:**
   - Ver catálogo de autos
   - Ver categorías
   - Ver sedes

2. **Registro/Login:**
   - Registrar nuevo usuario
   - Iniciar sesión
   - Ver perfil

3. **Cliente Autenticado:**
   - Agregar favoritos
   - Crear reserva
   - Ver historial de reservas

4. **Administrador:**
   - Login con credenciales admin
   - Acceder a panel administrativo
   - Gestionar autos, categorías, usuarios

---

## 7. SOLUCIÓN DE PROBLEMAS COMUNES

### 7.1 Backend no Inicia

**Problema:** Error de conexión a MySQL
```
Communications link failure
```

**Solución:**
- Verificar que MySQL esté corriendo: `mysql -u root -p`
- Verificar credenciales en `application.properties`
- Verificar que el puerto 3306 esté disponible

---

**Problema:** Puerto 8080 en uso
```
Port 8080 was already in use
```

**Solución:**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/macOS
lsof -i :8080
kill -9 <PID>
```

---

**Problema:** Error de compilación Maven
```
Failed to execute goal
```

**Solución:**
```bash
# Limpiar caché de Maven
mvn clean
rm -rf ~/.m2/repository

# Reinstalar
mvn clean install -U
```

### 7.2 Frontend no Inicia

**Problema:** Error de dependencias
```
Cannot find module
```

**Solución:**
```bash
# Eliminar node_modules y reinstalar
rm -rf node_modules package-lock.json
npm install
```

---

**Problema:** Puerto 5173 en uso
```
Port 5173 is in use
```

**Solución:**
- Vite automáticamente usará el siguiente puerto disponible (5174, 5175, etc.)
- O especifica otro puerto: `npm run dev -- --port 3000`

### 7.3 Problemas de Base de Datos

**Problema:** Tablas no se crean automáticamente

**Solución:**
```sql
-- Verificar que la BD existe
SHOW DATABASES;

-- Verificar configuración en application.properties
spring.jpa.hibernate.ddl-auto=update
```

---

**Problema:** Error de encoding en caracteres especiales

**Solución:**
```sql
ALTER DATABASE autoreserve_app_bd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

## 8. CONFIGURACIÓN ADICIONAL (OPCIONAL)

### 8.1 Configurar SMTP para Envío de Correos

Editar `application.properties`:

```properties
# Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 8.2 Configurar Logs

```properties
# Nivel de logs
logging.level.root=INFO
logging.level.com.autoreserve=DEBUG

# Archivo de logs
logging.file.name=logs/autoreserve.log
```

### 8.3 Configurar Pool de Conexiones

```properties
# HikariCP (incluido por defecto)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

---

## 9. SIGUIENTES PASOS

Después de completar la instalación:

1. **Leer el Manual de Usuario** para entender las funcionalidades
2. **Leer el Manual de Administrador** para gestionar el sistema
3. **Revisar la Documentación de API** para integraciones
4. **Configurar backup de base de datos** (ver Manual de Mantenimiento)

---

## 10. SOPORTE Y CONTACTO

Para problemas durante la instalación:
- Revisar logs del backend en la consola
- Revisar logs del frontend en la consola del navegador (F12)
- Consultar el Manual de Mantenimiento para troubleshooting avanzado

---

**Documento elaborado por:** Equipo AutoReserve  
**Última actualización:** Enero 2025  
**Versión:** 1.0
