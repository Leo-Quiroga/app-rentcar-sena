# MANUAL DE MANTENIMIENTO - AUTORESERVE

## Información del Documento
- **Versión:** 1.0
- **Fecha:** Enero 2025
- **Proyecto:** AutoReserve - Sistema de Reserva de Vehículos

---

## 1. INTRODUCCIÓN

### 1.1 Propósito

Este manual proporciona guías para el mantenimiento operativo del sistema AutoReserve, incluyendo:
- Monitoreo del sistema
- Troubleshooting
- Backup y recuperación
- Optimización de rendimiento
- Mantenimiento preventivo

### 1.2 Audiencia

- Administradores de sistemas
- DevOps
- Equipo de soporte técnico
- Personal de operaciones

---

## 2. MONITOREO DEL SISTEMA

### 2.1 Servicios a Monitorear

| Servicio | Puerto | Comando de Verificación |
|----------|--------|------------------------|
| Backend (Spring Boot) | 8080 | `curl http://localhost:8080/api/categories` |
| Frontend (Nginx) | 80/443 | `curl http://localhost` |
| MySQL | 3306 | `mysql -u root -p -e "SELECT 1"` |

### 2.2 Verificar Estado de Servicios

**Linux (systemd):**
```bash
# Backend
sudo systemctl status autoreserve-backend

# Nginx
sudo systemctl status nginx

# MySQL
sudo systemctl status mysql
```

**Ver logs en tiempo real:**
```bash
# Backend
sudo journalctl -u autoreserve-backend -f

# Nginx
sudo tail -f /var/log/nginx/autoreserve-access.log
sudo tail -f /var/log/nginx/autoreserve-error.log

# MySQL
sudo tail -f /var/log/mysql/error.log
```

### 2.3 Métricas Clave

**CPU y Memoria:**
```bash
# Ver uso general
htop

# Ver proceso Java
ps aux | grep java

# Ver uso de memoria
free -h
```

**Disco:**
```bash
# Espacio disponible
df -h

# Archivos grandes
du -sh /var/log/*
du -sh /home/autoreserve/*
```

**Conexiones de red:**
```bash
# Puertos en escucha
sudo netstat -tulpn | grep LISTEN

# Conexiones activas
sudo netstat -an | grep :8080
```

**Base de datos:**
```sql
-- Conexiones activas
SHOW PROCESSLIST;

-- Tamaño de base de datos
SELECT 
    table_schema AS 'Database',
    ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS 'Size (MB)'
FROM information_schema.tables
WHERE table_schema = 'autoreserve_prod'
GROUP BY table_schema;

-- Queries lentas
SELECT * FROM mysql.slow_log ORDER BY query_time DESC LIMIT 10;
```

### 2.4 Alertas Automáticas

**Script de monitoreo básico:**

```bash
#!/bin/bash
# /usr/local/bin/monitor-autoreserve.sh

# Verificar backend
if ! curl -s http://localhost:8080/api/categories > /dev/null; then
    echo "ALERTA: Backend no responde" | mail -s "AutoReserve Alert" admin@example.com
fi

# Verificar espacio en disco
DISK_USAGE=$(df -h / | awk 'NR==2 {print $5}' | sed 's/%//')
if [ $DISK_USAGE -gt 80 ]; then
    echo "ALERTA: Disco al $DISK_USAGE%" | mail -s "AutoReserve Alert" admin@example.com
fi

# Verificar MySQL
if ! mysqladmin ping -h localhost -u root -p'password' > /dev/null 2>&1; then
    echo "ALERTA: MySQL no responde" | mail -s "AutoReserve Alert" admin@example.com
fi
```

**Programar con cron:**
```bash
# Ejecutar cada 5 minutos
*/5 * * * * /usr/local/bin/monitor-autoreserve.sh
```

---

## 3. BACKUP Y RECUPERACIÓN

### 3.1 Backup de Base de Datos

**Backup manual:**
```bash
# Backup completo
mysqldump -u autoreserve_user -p autoreserve_prod > backup_$(date +%Y%m%d_%H%M%S).sql

# Backup comprimido
mysqldump -u autoreserve_user -p autoreserve_prod | gzip > backup_$(date +%Y%m%d_%H%M%S).sql.gz

# Backup de tablas específicas
mysqldump -u autoreserve_user -p autoreserve_prod users reservations > backup_critical.sql
```

**Script de backup automático:**
```bash
#!/bin/bash
# /usr/local/bin/backup-database.sh

BACKUP_DIR="/home/autoreserve/backups/database"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="autoreserve_prod"
DB_USER="autoreserve_user"
DB_PASS="password"
RETENTION_DAYS=30

mkdir -p $BACKUP_DIR

# Crear backup
mysqldump -u $DB_USER -p$DB_PASS $DB_NAME | gzip > $BACKUP_DIR/db_$DATE.sql.gz

# Eliminar backups antiguos
find $BACKUP_DIR -name "*.sql.gz" -mtime +$RETENTION_DAYS -delete

echo "Backup completado: db_$DATE.sql.gz"
```

**Programar backup diario:**
```bash
# Crontab - Backup a las 2:00 AM
0 2 * * * /usr/local/bin/backup-database.sh >> /var/log/autoreserve/backup.log 2>&1
```

### 3.2 Backup de Aplicación

**Backup de código y configuración:**
```bash
#!/bin/bash
# /usr/local/bin/backup-application.sh

BACKUP_DIR="/home/autoreserve/backups/application"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# Backup del backend
tar -czf $BACKUP_DIR/backend_$DATE.tar.gz \
    /home/autoreserve/autoreserve-backend \
    --exclude='target' \
    --exclude='*.log'

# Backup del frontend
tar -czf $BACKUP_DIR/frontend_$DATE.tar.gz \
    /home/autoreserve/autoreserve-frontend/dist

# Backup de configuraciones
tar -czf $BACKUP_DIR/config_$DATE.tar.gz \
    /etc/nginx/sites-available/autoreserve \
    /etc/systemd/system/autoreserve-backend.service

echo "Backup de aplicación completado"
```

### 3.3 Restaurar desde Backup

**Restaurar base de datos:**
```bash
# Descomprimir y restaurar
gunzip < backup_20250115_020000.sql.gz | mysql -u autoreserve_user -p autoreserve_prod

# O directamente desde archivo comprimido
zcat backup_20250115_020000.sql.gz | mysql -u autoreserve_user -p autoreserve_prod
```

**Restaurar aplicación:**
```bash
# Detener servicios
sudo systemctl stop autoreserve-backend
sudo systemctl stop nginx

# Restaurar backend
tar -xzf backend_20250115_020000.tar.gz -C /

# Restaurar frontend
tar -xzf frontend_20250115_020000.tar.gz -C /

# Reiniciar servicios
sudo systemctl start autoreserve-backend
sudo systemctl start nginx
```

### 3.4 Backup Remoto

**Sincronizar con servidor remoto:**
```bash
# Usando rsync
rsync -avz /home/autoreserve/backups/ user@backup-server:/backups/autoreserve/

# Usando scp
scp /home/autoreserve/backups/*.gz user@backup-server:/backups/autoreserve/
```

---

## 4. TROUBLESHOOTING

### 4.1 Backend no Responde

**Síntomas:**
- Error 502 Bad Gateway
- Timeout en requests
- No se puede acceder a la API

**Diagnóstico:**
```bash
# Verificar si el proceso está corriendo
ps aux | grep java

# Verificar logs
sudo journalctl -u autoreserve-backend -n 100

# Verificar puerto
sudo netstat -tulpn | grep 8080
```

**Soluciones:**

**1. Reiniciar servicio:**
```bash
sudo systemctl restart autoreserve-backend
sudo systemctl status autoreserve-backend
```

**2. Verificar memoria:**
```bash
free -h
# Si hay poca memoria, aumentar swap o RAM
```

**3. Verificar logs de errores:**
```bash
sudo journalctl -u autoreserve-backend | grep ERROR
```

**4. Verificar conexión a base de datos:**
```bash
mysql -u autoreserve_user -p -e "SELECT 1"
```

### 4.2 Error de Conexión a Base de Datos

**Síntomas:**
- "Communications link failure"
- "Access denied for user"
- Backend no inicia

**Diagnóstico:**
```bash
# Verificar MySQL
sudo systemctl status mysql

# Verificar conexión
mysql -u autoreserve_user -p

# Ver logs de MySQL
sudo tail -f /var/log/mysql/error.log
```

**Soluciones:**

**1. MySQL no está corriendo:**
```bash
sudo systemctl start mysql
sudo systemctl enable mysql
```

**2. Credenciales incorrectas:**
```bash
# Verificar en application.properties
cat /home/autoreserve/autoreserve-backend/src/main/resources/application.properties

# Resetear contraseña si es necesario
mysql -u root -p
ALTER USER 'autoreserve_user'@'localhost' IDENTIFIED BY 'nueva_password';
FLUSH PRIVILEGES;
```

**3. Base de datos no existe:**
```sql
CREATE DATABASE autoreserve_prod;
```

**4. Demasiadas conexiones:**
```sql
SHOW VARIABLES LIKE 'max_connections';
SET GLOBAL max_connections = 200;
```

### 4.3 Frontend no Carga

**Síntomas:**
- Página en blanco
- Error 404
- Assets no cargan

**Diagnóstico:**
```bash
# Verificar Nginx
sudo systemctl status nginx

# Verificar logs
sudo tail -f /var/log/nginx/autoreserve-error.log

# Verificar archivos
ls -la /home/autoreserve/autoreserve-frontend/dist/
```

**Soluciones:**

**1. Nginx no está corriendo:**
```bash
sudo systemctl start nginx
```

**2. Configuración incorrecta:**
```bash
# Verificar sintaxis
sudo nginx -t

# Recargar configuración
sudo systemctl reload nginx
```

**3. Permisos incorrectos:**
```bash
sudo chown -R www-data:www-data /home/autoreserve/autoreserve-frontend/dist/
sudo chmod -R 755 /home/autoreserve/autoreserve-frontend/dist/
```

### 4.4 Rendimiento Lento

**Síntomas:**
- Respuestas lentas
- Timeouts
- Alta carga del servidor

**Diagnóstico:**
```bash
# CPU
top
htop

# Memoria
free -h

# Disco I/O
iostat -x 1

# Conexiones de red
netstat -an | wc -l
```

**Soluciones:**

**1. Optimizar JVM:**
```bash
# Editar servicio
sudo nano /etc/systemd/system/autoreserve-backend.service

# Agregar opciones JVM
ExecStart=/usr/bin/java -Xms512m -Xmx2g -XX:+UseG1GC -jar ...

sudo systemctl daemon-reload
sudo systemctl restart autoreserve-backend
```

**2. Optimizar MySQL:**
```sql
-- Ver queries lentas
SELECT * FROM mysql.slow_log ORDER BY query_time DESC LIMIT 10;

-- Agregar índices
CREATE INDEX idx_reservation_dates ON reservations(start_date, end_date);
CREATE INDEX idx_car_status ON cars(status);
```

**3. Limpiar logs:**
```bash
# Rotar logs manualmente
sudo logrotate -f /etc/logrotate.d/autoreserve

# Limpiar logs antiguos
sudo find /var/log -name "*.log" -mtime +30 -delete
```

**4. Reiniciar servicios:**
```bash
sudo systemctl restart autoreserve-backend
sudo systemctl restart nginx
sudo systemctl restart mysql
```

### 4.5 Errores de SSL/HTTPS

**Síntomas:**
- "Your connection is not private"
- Certificado expirado
- Error SSL

**Diagnóstico:**
```bash
# Verificar certificado
sudo certbot certificates

# Verificar fecha de expiración
openssl x509 -in /etc/letsencrypt/live/tudominio.com/cert.pem -noout -dates
```

**Soluciones:**

**1. Renovar certificado:**
```bash
sudo certbot renew
sudo systemctl reload nginx
```

**2. Forzar renovación:**
```bash
sudo certbot renew --force-renewal
```

**3. Verificar configuración Nginx:**
```bash
sudo nginx -t
```

### 4.6 Espacio en Disco Lleno

**Síntomas:**
- "No space left on device"
- Servicios no inician
- No se pueden crear archivos

**Diagnóstico:**
```bash
# Ver uso de disco
df -h

# Encontrar archivos grandes
sudo du -sh /* | sort -h
sudo find / -type f -size +100M
```

**Soluciones:**

**1. Limpiar logs:**
```bash
sudo journalctl --vacuum-time=7d
sudo find /var/log -name "*.log" -mtime +7 -delete
```

**2. Limpiar backups antiguos:**
```bash
sudo find /home/autoreserve/backups -mtime +30 -delete
```

**3. Limpiar caché:**
```bash
sudo apt clean
sudo apt autoremove
```

**4. Limpiar archivos temporales:**
```bash
sudo rm -rf /tmp/*
```

---

## 5. MANTENIMIENTO PREVENTIVO

### 5.1 Tareas Diarias

- [ ] Verificar estado de servicios
- [ ] Revisar logs de errores
- [ ] Verificar espacio en disco
- [ ] Verificar backup automático

**Script de verificación diaria:**
```bash
#!/bin/bash
# /usr/local/bin/daily-check.sh

echo "=== Verificación Diaria AutoReserve ==="
echo "Fecha: $(date)"

# Servicios
echo -e "\n--- Servicios ---"
systemctl is-active autoreserve-backend && echo "Backend: OK" || echo "Backend: ERROR"
systemctl is-active nginx && echo "Nginx: OK" || echo "Nginx: ERROR"
systemctl is-active mysql && echo "MySQL: OK" || echo "MySQL: ERROR"

# Disco
echo -e "\n--- Espacio en Disco ---"
df -h / | tail -1

# Memoria
echo -e "\n--- Memoria ---"
free -h | grep Mem

# Último backup
echo -e "\n--- Último Backup ---"
ls -lht /home/autoreserve/backups/database/ | head -2
```

### 5.2 Tareas Semanales

- [ ] Revisar logs de acceso
- [ ] Analizar métricas de rendimiento
- [ ] Verificar integridad de backups
- [ ] Actualizar estadísticas de base de datos

**Optimizar base de datos:**
```sql
-- Analizar tablas
ANALYZE TABLE cars, reservations, users;

-- Optimizar tablas
OPTIMIZE TABLE cars, reservations, users;

-- Verificar integridad
CHECK TABLE cars, reservations, users;
```

### 5.3 Tareas Mensuales

- [ ] Actualizar sistema operativo
- [ ] Actualizar dependencias
- [ ] Revisar políticas de seguridad
- [ ] Auditar logs de acceso
- [ ] Limpiar datos obsoletos

**Actualizar sistema:**
```bash
sudo apt update
sudo apt upgrade -y
sudo apt autoremove -y
```

**Limpiar datos obsoletos:**
```sql
-- Eliminar reservas canceladas antiguas (más de 1 año)
DELETE FROM reservations 
WHERE status = 'CANCELLED' 
AND created_at < DATE_SUB(NOW(), INTERVAL 1 YEAR);

-- Eliminar notificaciones antiguas
DELETE FROM notifications 
WHERE created_at < DATE_SUB(NOW(), INTERVAL 6 MONTH);
```

### 5.4 Tareas Trimestrales

- [ ] Revisar capacidad del servidor
- [ ] Planificar escalamiento
- [ ] Auditoría de seguridad
- [ ] Actualizar documentación
- [ ] Revisar plan de disaster recovery

---

## 6. OPTIMIZACIÓN DE RENDIMIENTO

### 6.1 Optimización de Base de Datos

**Índices recomendados:**
```sql
-- Reservas
CREATE INDEX idx_reservation_dates ON reservations(start_date, end_date);
CREATE INDEX idx_reservation_status ON reservations(status);
CREATE INDEX idx_reservation_user ON reservations(user_id);
CREATE INDEX idx_reservation_car ON reservations(car_id);

-- Autos
CREATE INDEX idx_car_status ON cars(status);
CREATE INDEX idx_car_category ON cars(category_id);
CREATE INDEX idx_car_branch ON cars(branch_id);

-- Usuarios
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_role ON users(role_id);
```

**Configuración MySQL:**
```ini
[mysqld]
# Buffer pool (70-80% de RAM dedicada a MySQL)
innodb_buffer_pool_size = 2G

# Conexiones
max_connections = 200

# Query cache (si MySQL < 8.0)
query_cache_size = 64M
query_cache_type = 1

# Logs
slow_query_log = 1
long_query_time = 2
```

### 6.2 Optimización de Backend

**Configuración JVM:**
```bash
# En /etc/systemd/system/autoreserve-backend.service
ExecStart=/usr/bin/java \
  -Xms512m \
  -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/log/autoreserve/heap_dump.hprof \
  -jar /home/autoreserve/autoreserve-backend/target/autoreserve-backend.jar
```

**Pool de conexiones:**
```properties
# application-prod.properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### 6.3 Optimización de Nginx

**Configuración de caché:**
```nginx
# En /etc/nginx/nginx.conf
http {
    # Caché de proxy
    proxy_cache_path /var/cache/nginx levels=1:2 keys_zone=api_cache:10m max_size=1g inactive=60m;
    
    # Compresión
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/json application/javascript;
    
    # Buffers
    client_body_buffer_size 128k;
    client_max_body_size 10m;
}
```

---

## 7. SEGURIDAD Y AUDITORÍA

### 7.1 Logs de Auditoría

**Ver accesos recientes:**
```bash
# Nginx access log
sudo tail -100 /var/log/nginx/autoreserve-access.log

# Filtrar por IP
sudo grep "192.168.1.100" /var/log/nginx/autoreserve-access.log

# Filtrar errores 4xx y 5xx
sudo grep " 4[0-9][0-9] " /var/log/nginx/autoreserve-access.log
sudo grep " 5[0-9][0-9] " /var/log/nginx/autoreserve-access.log
```

**Logs de aplicación:**
```bash
# Ver logs de backend
sudo journalctl -u autoreserve-backend --since "1 hour ago"

# Filtrar por nivel
sudo journalctl -u autoreserve-backend -p err

# Buscar patrón específico
sudo journalctl -u autoreserve-backend | grep "Authentication failed"
```

### 7.2 Verificar Intentos de Acceso

**Revisar intentos de login fallidos:**
```sql
SELECT 
    email,
    COUNT(*) as attempts,
    MAX(created_at) as last_attempt
FROM login_attempts
WHERE success = false
AND created_at > DATE_SUB(NOW(), INTERVAL 1 DAY)
GROUP BY email
HAVING attempts > 5
ORDER BY attempts DESC;
```

### 7.3 Actualizar Contraseñas

**Cambiar contraseña de base de datos:**
```sql
ALTER USER 'autoreserve_user'@'localhost' IDENTIFIED BY 'nueva_password_segura';
FLUSH PRIVILEGES;
```

**Actualizar en aplicación:**
```bash
sudo nano /home/autoreserve/autoreserve-backend/src/main/resources/application-prod.properties
# Cambiar spring.datasource.password

sudo systemctl restart autoreserve-backend
```

---

## 8. DISASTER RECOVERY

### 8.1 Plan de Recuperación

**RTO (Recovery Time Objective):** 4 horas  
**RPO (Recovery Point Objective):** 24 horas

**Pasos de recuperación:**

1. **Evaluar el daño**
2. **Restaurar desde backup más reciente**
3. **Verificar integridad de datos**
4. **Reiniciar servicios**
5. **Verificar funcionalidad**
6. **Notificar a usuarios**

### 8.2 Escenarios de Disaster

**Escenario 1: Falla de disco**
- Restaurar desde backup remoto
- Montar nuevo disco
- Restaurar datos

**Escenario 2: Corrupción de base de datos**
- Detener aplicación
- Restaurar backup de BD
- Verificar integridad
- Reiniciar aplicación

**Escenario 3: Servidor comprometido**
- Aislar servidor
- Analizar logs
- Reinstalar desde cero
- Restaurar desde backup limpio
- Cambiar todas las contraseñas

---

## 9. CONTACTOS DE EMERGENCIA

| Rol | Nombre | Teléfono | Email |
|-----|--------|----------|-------|
| Líder Técnico | _______ | _______ | _______ |
| DevOps | _______ | _______ | _______ |
| DBA | _______ | _______ | _______ |
| Soporte 24/7 | _______ | _______ | _______ |

---

**Documento elaborado por:** Equipo AutoReserve  
**Última actualización:** Enero 2025  
**Versión:** 1.0
