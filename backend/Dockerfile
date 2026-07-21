# Build multi-etapa. Opcional: el equipo puede desplegar el JAR directamente si prefiere.
#
# IMPORTANTE: el contexto de build es la RAIZ del repositorio, no backend/.
# El pom.xml empaqueta las migraciones desde ../database/migrations, asi que la
# imagen necesita ver tanto backend/ como database/. Ver docker-compose.yml.
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app/backend

# Primero solo lo necesario para resolver dependencias: la capa de descarga se
# reutiliza mientras no cambien el pom ni el wrapper.
COPY backend/.mvn/ .mvn/
COPY backend/mvnw backend/pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Las migraciones viven fuera de backend/ y entran al JAR como db/migration.
COPY database/ /app/database/
COPY backend/src ./src
RUN ./mvnw clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/backend/target/*.jar app.jar
USER app
EXPOSE 8080

# Limite de heap pensado para contenedores de 512 MB (tier gratuito).
# PDFBox necesita margen: no generar varios PDF en paralelo con esta memoria.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC"

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
