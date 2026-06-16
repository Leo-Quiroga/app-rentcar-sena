# AutoReserve Backend - Guía de Configuración de Ambientes

## 📋 Configuraciones Disponibles

### 🏭 **Producción/Docker** (Predeterminado)
**Archivo**: `application.properties`  
**Uso**: Docker Compose, despliegue en contenedores
```bash
java -jar target/autoreserve-backend-0.0.1-SNAPSHOT.jar
```

### 🛠️ **Desarrollo Local** 
**Archivo**: `application-dev.properties`  
**Uso**: Desarrollo con MySQL local en localhost
```bash
java -jar target/autoreserve-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### 🧪 **Tests**
**Archivo**: `application-test.properties`  
**Uso**: Automático durante la ejecución de tests (H2 en memoria)
```bash
.\mvnw.cmd test
```

---

## 🚀 Comandos de Build

### **Build Completo** (Recomendado)
Ejecuta todos los tests y crea el JAR:
```bash
.\mvnw.cmd clean package
```

### **Build Rápido** (Sin Tests)
Solo compilación y empaquetado:
```bash
.\mvnw.cmd clean package -DskipTests
```

### **Solo Tests**
Verificar que todo funciona:
```bash
.\mvnw.cmd test
```

### **Tests con Perfil Específico**
```bash
.\mvnw.cmd test -Dspring.profiles.active=test
```

---

## 🗄️ Configuración de Base de Datos por Ambiente

| Ambiente | Base de Datos | Host | Puerto | Schema |
|----------|---------------|------|--------|--------|
| **Producción** | MySQL | `mysql` | 3306 | `autoreserve_app_bd` |
| **Desarrollo** | MySQL | `localhost` | 3306 | `autoreserve_app_bd` |
| **Tests** | H2 | En memoria | - | `testdb` |

---

## ✅ Verificar Build Exitoso

### **Indicadores de Éxito:**
```
[INFO] BUILD SUCCESS
[INFO] Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
```

### **Archivo JAR Creado:**
```
target/autoreserve-backend-0.0.1-SNAPSHOT.jar
```

### **Ejecutar la Aplicación:**
```bash
# Producción (Docker)
java -jar target/autoreserve-backend-0.0.1-SNAPSHOT.jar

# Desarrollo local
java -jar target/autoreserve-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

---

## 🔧 Solución de Problemas

### **Error: "Connection refused" durante build**
- **Causa**: Tests intentan conectarse a MySQL
- **Solución**: Usar `.\mvnw.cmd clean package -DskipTests`

### **Error: "Table doesn't exist" durante tests**
- **Causa**: Configuración H2 incorrecta  
- **Solución**: Verificar `application-test.properties`

### **Error: "Access denied" en producción**
- **Causa**: Credenciales MySQL incorrectas
- **Solución**: Verificar `application.properties` o variables de entorno

---

## 🐳 Docker Compose

La aplicación está configurada para usar `mysql` como hostname en producción, compatible con Docker Compose:

```yaml
services:
  mysql:
    image: mysql:8.0
  backend:
    build: .
    depends_on:
      - mysql
```

---

## 🎯 Resumen de Archivos

```
src/main/resources/
├── application.properties          # 🏭 Producción/Docker  
├── application-dev.properties      # 🛠️ Desarrollo local
└── application-test.properties     # 🧪 Tests (H2)
```

**¡Configuración profesional lista para todos los ambientes!** ✅