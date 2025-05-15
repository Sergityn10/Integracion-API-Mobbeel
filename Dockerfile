## Usa una imagen oficial de JDK como base
#FROM eclipse-temurin:17-jdk
#
## Crea un directorio de trabajo
#WORKDIR /app
#
## Copia el jar generado (ajusta si tu jar tiene otro nombre)
#COPY target/MobbScan-0.0.1-SNAPSHOT.jar app.jar
#
## Expone el puerto de la aplicación
#EXPOSE 8082
#
## Ejecuta la aplicación
#ENTRYPOINT ["java", "-jar", "app.jar"]

# Etapa 1: Construcción
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiar pom.xml y descargar dependencias por separado
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el resto del código
COPY . .

# Compilar el proyecto sin tests
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de producción
FROM eclipse-temurin:17-jdk-jammy

# Crear directorio para el app
WORKDIR /app

# Copiar el .jar desde el builder
COPY --from=builder /app/target/*.jar app.jar

# Puerto expuesto por la app (usa 8082 porque así está configurado en application.properties)
EXPOSE 8082

# Iniciar el contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]
