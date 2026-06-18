# ==========================
# ETAPA 1 - Compilación
# ==========================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar primero el pom para aprovechar la caché de Docker
COPY pom.xml .

# Descargar dependencias
RUN mvn dependency:go-offline

# Copiar el resto del proyecto
COPY . .

# Compilar
RUN mvn clean package -DskipTests

# ==========================
# ETAPA 2 - Ejecución
# ==========================
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]