FROM eclipse-temurin:17-jdk-jammy

#WORKDIR /app
#
## Instalar netcat para wait-for-it.sh
#RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*
#
## Copiar el script wait-for-it.sh
#COPY wait-for-it.sh wait-for-it.sh
#
## Dar permisos de ejecución al script
#RUN chmod +x wait-for-it.sh
#
## Copiamos el JAR generado al contenedor
## (ajusta el nombre si tu .jar no se llama app.jar)
#COPY target/*.jar app.jar
#
## Expone el puerto en el que escucha tu aplicación Spring Boot
## Cambia 8080 si tu server.port es distinto
#EXPOSE 8081
#
#
## Esperar a MySQL antes de arrancar la app
#ENTRYPOINT ["./wait-for-it.sh", "carpooling-api-database2", "3306", "--", "java", "-jar", "app.jar"]

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR generado al contenedor
# (ajusta el nombre si tu .jar no se llama app.jar)
COPY target/*.jar app.jar

# Expone el puerto en el que escucha tu aplicación Spring Boot
# Cambia 8080 si tu server.port es distinto
EXPOSE 8080

# Comando por defecto: ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]