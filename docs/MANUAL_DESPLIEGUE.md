# MANUAL DE DESPLIEGUE - AUTORESERVE

## Información del Documento
- **Versión:** 1.0
- **Fecha:** Enero 2025
- **Proyecto:** AutoReserve - Sistema de Reserva de Vehículos

---

## 1. INTRODUCCIÓN

Este manual describe el proceso de despliegue de AutoReserve en ambientes de producción, incluyendo configuraciones de seguridad, optimización y monitoreo.

### 1.1 Arquitectura de Despliegue

```
┌─────────────────┐
│   Navegador     │
└────────┬────────┘
         │ HTTPS
         ▼
┌─────────────────┐
│  Nginx/Apache   │ (Reverse Proxy)
│   Puerto 80/443 │
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌────────┐ ┌────────┐
│Frontend│ │Backend │
│ :5173  │ │ :8080  │
└────────┘ └───┬────┘
               │
               ▼
          ┌────────┐
          │ MySQL  │
          │ :3306  │
          └────────┘
```

---

## 2. PREPARACIÓN PARA PRODUCCIÓN

### 2.1 Checklist Pre-Despliegue

- [ ] Código revisado y aprobado
- [ ] Tests ejecutados exitosamente
- [ ] Base de datos de producción creada
- [ ] Credenciales de producción generadas
- [ ] Certificado SSL obtenido
- [ ] Servidor de producción configurado
- [ ] Backup de datos existentes (si aplica)
- [ ] Plan de rollback definido

### 2.2 Requisitos del Servidor

#### Servidor Mínimo
- **CPU:** 2 cores
- **RAM:** 4 GB
- **Disco:** 50 GB SSD
- **SO:** Ubuntu 20.04 LTS o superior

#### Servidor Recomendado
- **CPU:** 4 cores
- **RAM:** 8 GB
- **Disco:** 100 GB SSD
- **SO:** Ubuntu 22.04 LTS

---

## 3. CONFIGURACIÓN DEL SERVIDOR

### 3.1 Actualizar Sistema Operativo

```bash
sudo apt update
sudo apt upgrade -y
sudo apt install -y curl wget git vim ufw
```

### 3.2 Configurar Firewall

```bash
# Habilitar UFW
sudo ufw enable

# Permitir SSH
sudo ufw allow 22/tcp

# Permitir HTTP y HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Verificar estado
sudo ufw status
```

### 3.3 Crear Usuario de Aplicación

```bash
# Crear usuario sin privilegios root
sudo adduser autoreserve
sudo usermod -aG sudo autoreserve

# Cambiar a usuario
su - autoreserve
```

---

## 4. DESPLIEGUE DEL BACKEND

### 4.1 Instalar Java en Producción

```bash
sudo apt install openjdk-21-jdk -y
java -version
```

### 4.2 Configurar Variables de Entorno

```bash
# Crear archivo de variables de entorno
sudo nano /etc/environment
```

Agregar:
```bash
JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
AUTORESERVE_DB_URL="jdbc:mysql://localhost:3306/autoreserve_prod"
AUTORESERVE_DB_USER="autoreserve_user"
AUTORESERVE_DB_PASSWORD="password_seguro_produccion"
JWT_SECRET="clave_jwt_super_segura_minimo_256_bits_produccion"
```

### 4.3 Configurar application-prod.properties

Crear archivo de configuración de producción:

**Ubicación:** `autoreserve-backend/src/main/resources/application-prod.properties`

```properties
# Perfil de producción
spring.profiles.active=prod

# Base de datos
spring.datasource.url=${AUTORESERVE_DB_URL}
spring.datasource.username=${AUTORESERVE_DB_USER}
spring.datasource.password=${AUTORESERVE_DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

# Pool de conexiones
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# Logging
logging.level.root=WARN
logging.level.com.autoreserve=INFO
logging.file.name=/var/log/autoreserve/application.log
logging.file.max-size=10MB
logging.file.max-history=30

# Seguridad
server.port=8080
server.error.include-message=never
server.error.include-stacktrace=never

# CORS (ajustar según dominio)
cors.allowed-origins=https://tudominio.com

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=3600000

# Compresión
server.compression.enabled=true
server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain

# SSL (si se maneja en Spring Boot)
# server.ssl.enabled=true
# server.ssl.key-store=/path/to/keystore.p12
# server.ssl.key-store-password=password
# server.ssl.key-store-type=PKCS12
```

### 4.4 Compilar para Producción

```bash
cd /home/autoreserve/autoreserve-backend

# Compilar con perfil de producción
mvn clean package -DskipTests -Pprod

# El JAR se genera en target/
ls -lh target/*.jar
```

### 4.5 Crear Servicio Systemd

```bash
sudo nano /etc/systemd/system/autoreserve-backend.service
```

Contenido:
```ini
[Unit]
Description=AutoReserve Backend Service
After=syslog.target network.target mysql.service

[Service]
User=autoreserve
Group=autoreserve
Type=simple

WorkingDirectory=/home/autoreserve/autoreserve-backend
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod /home/autoreserve/autoreserve-backend/target/autoreserve-backend-0.0.1-SNAPSHOT.jar

StandardOutput=journal
StandardError=journal
SyslogIdentifier=autoreserve-backend

Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### 4.6 Iniciar Servicio Backend

```bash
# Recargar systemd
sudo systemctl daemon-reload

# Habilitar inicio automático
sudo systemctl enable autoreserve-backend

# Iniciar servicio
sudo systemctl start autoreserve-backend

# Verificar estado
sudo systemctl status autoreserve-backend

# Ver logs
sudo journalctl -u autoreserve-backend -f
```

---

## 5. DESPLIEGUE DEL FRONTEND

### 5.1 Instalar Node.js en Producción

```bash
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt-get install -y nodejs
node -v
npm -v
```

### 5.2 Configurar Variables de Entorno Frontend

Crear archivo `.env.production`:

```bash
cd /home/autoreserve/autoreserve-frontend
nano .env.production
```

Contenido:
```bash
VITE_API_URL=https://api.tudominio.com
VITE_APP_NAME=AutoReserve
VITE_ENV=production
```

### 5.3 Compilar Frontend para Producción

```bash
cd /home/autoreserve/autoreserve-frontend

# Instalar dependencias
npm ci --production

# Compilar
npm run build

# Los archivos se generan en dist/
ls -lh dist/
```

### 5.4 Instalar y Configurar Nginx

```bash
# Instalar Nginx
sudo apt install nginx -y

# Crear configuración del sitio
sudo nano /etc/nginx/sites-available/autoreserve
```

Contenido:
```nginx
# Redirigir HTTP a HTTPS
server {
    listen 80;
    server_name tudominio.com www.tudominio.com;
    return 301 https://$server_name$request_uri;
}

# Servidor HTTPS
server {
    listen 443 ssl http2;
    server_name tudominio.com www.tudominio.com;

    # Certificados SSL
    ssl_certificate /etc/letsencrypt/live/tudominio.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/tudominio.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Logs
    access_log /var/log/nginx/autoreserve-access.log;
    error_log /var/log/nginx/autoreserve-error.log;

    # Frontend (React)
    location / {
        root /home/autoreserve/autoreserve-frontend/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
        
        # Cache para assets estáticos
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }

    # Backend API (Proxy reverso)
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Seguridad
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;

    # Tamaño máximo de upload
    client_max_body_size 10M;
}
```

### 5.5 Activar Sitio y Reiniciar Nginx

```bash
# Crear enlace simbólico
sudo ln -s /etc/nginx/sites-available/autoreserve /etc/nginx/sites-enabled/

# Eliminar sitio por defecto
sudo rm /etc/nginx/sites-enabled/default

# Verificar configuración
sudo nginx -t

# Reiniciar Nginx
sudo systemctl restart nginx
sudo systemctl enable nginx
```

---

## 6. CONFIGURAR SSL CON LET'S ENCRYPT

### 6.1 Instalar Certbot

```bash
sudo apt install certbot python3-certbot-nginx -y
```

### 6.2 Obtener Certificado SSL

```bash
# Obtener certificado
sudo certbot --nginx -d tudominio.com -d www.tudominio.com

# Seguir las instrucciones en pantalla
# Certbot configurará automáticamente Nginx
```

### 6.3 Renovación Automática

```bash
# Verificar renovación automática
sudo certbot renew --dry-run

# Certbot crea un cron job automáticamente
sudo systemctl status certbot.timer
```

---

## 7. CONFIGURACIÓN DE BASE DE DATOS EN PRODUCCIÓN

### 7.1 Instalar MySQL

```bash
sudo apt install mysql-server -y
sudo mysql_secure_installation
```

### 7.2 Crear Base de Datos y Usuario

```sql
-- Conectar como root
sudo mysql -u root -p

-- Crear base de datos
CREATE DATABASE autoreserve_prod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario
CREATE USER 'autoreserve_user'@'localhost' IDENTIFIED BY 'password_seguro_produccion';

-- Otorgar permisos
GRANT ALL PRIVILEGES ON autoreserve_prod.* TO 'autoreserve_user'@'localhost';
FLUSH PRIVILEGES;

-- Verificar
SHOW DATABASES;
SELECT User, Host FROM mysql.user WHERE User='autoreserve_user';
```

### 7.3 Optimizar MySQL para Producción

```bash
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf
```

Agregar/modificar:
```ini
[mysqld]
# Conexiones
max_connections = 200

# Buffer pool (70% de RAM disponible para MySQL)
innodb_buffer_pool_size = 2G

# Logs
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow-query.log
long_query_time = 2

# Charset
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
```

Reiniciar MySQL:
```bash
sudo systemctl restart mysql
```

### 7.4 Migrar Datos (si aplica)

```bash
# Exportar desde desarrollo
mysqldump -u root -p autoreserve_app_bd > backup_dev.sql

# Importar a producción
mysql -u autoreserve_user -p autoreserve_prod < backup_dev.sql
```

---

## 8. MONITOREO Y LOGS

### 8.1 Configurar Logs

```bash
# Crear directorio de logs
sudo mkdir -p /var/log/autoreserve
sudo chown autoreserve:autoreserve /var/log/autoreserve

# Configurar rotación de logs
sudo nano /etc/logrotate.d/autoreserve
```

Contenido:
```
/var/log/autoreserve/*.log {
    daily
    rotate 30
    compress
    delaycompress
    notifempty
    create 0640 autoreserve autoreserve
    sharedscripts
    postrotate
        systemctl reload autoreserve-backend > /dev/null 2>&1 || true
    endscript
}
```

### 8.2 Monitorear Servicios

```bash
# Ver logs del backend
sudo journalctl -u autoreserve-backend -f

# Ver logs de Nginx
sudo tail -f /var/log/nginx/autoreserve-access.log
sudo tail -f /var/log/nginx/autoreserve-error.log

# Ver logs de MySQL
sudo tail -f /var/log/mysql/error.log

# Ver estado de servicios
sudo systemctl status autoreserve-backend
sudo systemctl status nginx
sudo systemctl status mysql
```

### 8.3 Monitoreo de Recursos

```bash
# CPU y memoria
htop

# Espacio en disco
df -h

# Conexiones de red
sudo netstat -tulpn | grep LISTEN

# Procesos Java
ps aux | grep java
```

---

## 9. BACKUP Y RECUPERACIÓN

### 9.1 Script de Backup Automático

```bash
sudo nano /usr/local/bin/backup-autoreserve.sh
```

Contenido:
```bash
#!/bin/bash

# Configuración
BACKUP_DIR="/home/autoreserve/backups"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="autoreserve_prod"
DB_USER="autoreserve_user"
DB_PASS="password_seguro_produccion"
RETENTION_DAYS=30

# Crear directorio si no existe
mkdir -p $BACKUP_DIR

# Backup de base de datos
mysqldump -u $DB_USER -p$DB_PASS $DB_NAME | gzip > $BACKUP_DIR/db_backup_$DATE.sql.gz

# Backup de archivos de aplicación
tar -czf $BACKUP_DIR/app_backup_$DATE.tar.gz \
    /home/autoreserve/autoreserve-backend \
    /home/autoreserve/autoreserve-frontend/dist

# Eliminar backups antiguos
find $BACKUP_DIR -name "*.gz" -mtime +$RETENTION_DAYS -delete

echo "Backup completado: $DATE"
```

Dar permisos:
```bash
sudo chmod +x /usr/local/bin/backup-autoreserve.sh
```

### 9.2 Programar Backup con Cron

```bash
sudo crontab -e
```

Agregar:
```bash
# Backup diario a las 2:00 AM
0 2 * * * /usr/local/bin/backup-autoreserve.sh >> /var/log/autoreserve/backup.log 2>&1
```

### 9.3 Restaurar desde Backup

```bash
# Restaurar base de datos
gunzip < /home/autoreserve/backups/db_backup_YYYYMMDD_HHMMSS.sql.gz | mysql -u autoreserve_user -p autoreserve_prod

# Restaurar aplicación
tar -xzf /home/autoreserve/backups/app_backup_YYYYMMDD_HHMMSS.tar.gz -C /
sudo systemctl restart autoreserve-backend
```

---

## 10. SEGURIDAD ADICIONAL

### 10.1 Configurar Fail2Ban

```bash
# Instalar
sudo apt install fail2ban -y

# Configurar
sudo nano /etc/fail2ban/jail.local
```

Contenido:
```ini
[DEFAULT]
bantime = 3600
findtime = 600
maxretry = 5

[sshd]
enabled = true

[nginx-http-auth]
enabled = true
```

Iniciar:
```bash
sudo systemctl enable fail2ban
sudo systemctl start fail2ban
```

### 10.2 Actualizar JWT Secret

Generar secret seguro:
```bash
openssl rand -base64 64
```

Actualizar en `/etc/environment` y reiniciar backend.

### 10.3 Deshabilitar Root Login SSH

```bash
sudo nano /etc/ssh/sshd_config
```

Cambiar:
```
PermitRootLogin no
PasswordAuthentication no
```

Reiniciar SSH:
```bash
sudo systemctl restart sshd
```

---

## 11. OPTIMIZACIÓN DE RENDIMIENTO

### 11.1 Habilitar Caché en Nginx

```nginx
# Agregar en /etc/nginx/nginx.conf
http {
    proxy_cache_path /var/cache/nginx levels=1:2 keys_zone=api_cache:10m max_size=1g inactive=60m;
}
```

### 11.2 Configurar Compresión Gzip

Ya está configurado en la configuración de Nginx anterior.

### 11.3 Optimizar JVM

Modificar servicio systemd:
```bash
sudo nano /etc/systemd/system/autoreserve-backend.service
```

Cambiar ExecStart:
```ini
ExecStart=/usr/bin/java -Xms512m -Xmx2g -XX:+UseG1GC -jar -Dspring.profiles.active=prod /home/autoreserve/autoreserve-backend/target/autoreserve-backend-0.0.1-SNAPSHOT.jar
```

---

## 12. VERIFICACIÓN POST-DESPLIEGUE

### 12.1 Checklist de Verificación

- [ ] Backend responde en `https://tudominio.com/api/categories`
- [ ] Frontend carga correctamente en `https://tudominio.com`
- [ ] SSL funciona correctamente (candado verde)
- [ ] Login funciona
- [ ] Registro funciona
- [ ] Reservas funcionan
- [ ] Panel admin accesible
- [ ] Logs se están generando
- [ ] Backup automático configurado
- [ ] Monitoreo activo

### 12.2 Pruebas de Carga

```bash
# Instalar Apache Bench
sudo apt install apache2-utils -y

# Prueba simple
ab -n 1000 -c 10 https://tudominio.com/api/categories
```

---

## 13. PLAN DE ROLLBACK

En caso de problemas:

```bash
# 1. Detener servicios
sudo systemctl stop autoreserve-backend
sudo systemctl stop nginx

# 2. Restaurar backup
gunzip < /home/autoreserve/backups/db_backup_ANTERIOR.sql.gz | mysql -u autoreserve_user -p autoreserve_prod

# 3. Restaurar versión anterior del código
cd /home/autoreserve/autoreserve-backend
git checkout <commit-anterior>
mvn clean package -DskipTests

# 4. Reiniciar servicios
sudo systemctl start autoreserve-backend
sudo systemctl start nginx

# 5. Verificar
curl https://tudominio.com/api/categories
```

---

## 14. MANTENIMIENTO CONTINUO

### 14.1 Actualizaciones de Seguridad

```bash
# Actualizar sistema semanalmente
sudo apt update
sudo apt upgrade -y
sudo apt autoremove -y
```

### 14.2 Monitoreo de Logs

Revisar diariamente:
- Errores en logs de aplicación
- Errores 500 en Nginx
- Queries lentas en MySQL
- Uso de disco

### 14.3 Renovación SSL

Certbot renueva automáticamente, pero verificar:
```bash
sudo certbot renew --dry-run
```

---

## 15. CONTACTO Y SOPORTE

Para problemas en producción:
- Revisar logs: `/var/log/autoreserve/`
- Verificar estado de servicios
- Consultar Manual de Mantenimiento
- Contactar al equipo de desarrollo

---

**Documento elaborado por:** Equipo AutoReserve  
**Última actualización:** Enero 2025  
**Versión:** 1.0
