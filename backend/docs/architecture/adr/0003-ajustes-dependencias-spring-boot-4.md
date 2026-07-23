# ADR-0003 — Ajustes de dependencias y configuración para Spring Boot 4.1

**Estado:** Aceptado
**Fecha:** Semana 0
**Autor:** Backend
**Afecta a:** `backend/pom.xml`, `backend/src/main/resources/application.yml`, `backend/Dockerfile`, `backend/docker-compose.yml`

## Contexto

El arquetipo declara el parent `spring-boot-starter-parent:4.1.0`, pero sus dependencias y su
configuración estaban escritas con las convenciones de la línea **3.x**. El propio `pom.xml`
lo anticipaba: *"Spring Boot 4.x cambió varios starters respecto de la línea 3.x […] si algo
no resuelve, revisar Maven Central y dejar constancia en un ADR"*.

Al intentar levantar el entorno local por primera vez el proyecto no compilaba y, una vez
corregido, no arrancaba. Este ADR documenta los seis ajustes necesarios para que el
arquetipo compile, arranque y aplique el esquema.

## Decisiones

### 1. Testcontainers: módulos renombrados

`org.testcontainers:postgresql` → `org.testcontainers:testcontainers-postgresql`.

El BOM de Boot 4.1 gestiona Testcontainers **2.0.5**, y la línea 2.x renombró todos sus
módulos con el prefijo `testcontainers-`. Con la coordenada antigua Maven ni siquiera podía
leer el POM: la versión no resolvía y el build moría antes de compilar.

### 2. springdoc-openapi: línea 3.x

`springdoc.version` 2.8.5 → **3.0.3**.

La línea 2.x está construida contra Spring Boot 3.x. La 3.x es la compatible con Boot 4.x.
Verificado en ejecución: `/v3/api-docs` y `/swagger-ui.html` responden 200.

### 3. bucket4j: la versión declarada no existía

`bucket4j` 8.10.1 → **8.19.0**. El artefacto `bucket4j_jdk17-core` nunca publicó una 8.10.1;
la más antigua disponible es la 8.11.0. Se toma la última estable.

### 4. Flyway: hace falta el módulo de autoconfiguración (el defecto más grave)

`org.flywaydb:flyway-core` → `org.springframework.boot:spring-boot-flyway`
(mantiene `flyway-database-postgresql`; `flyway-core` entra de forma transitiva).

Spring Boot 4 sacó las autoconfiguraciones de `spring-boot-autoconfigure` a módulos propios.
Con solo `flyway-core` en el classpath **no hay autoconfiguración**: `spring.flyway.enabled:
true` no hacía nada, no se emitía ni una línea de log de Flyway y la base de datos quedaba
**vacía sin error alguno**. Es un fallo silencioso: el equipo podía dar por aplicado un
esquema inexistente.

### 5. Jackson 3: `write-dates-as-timestamps` cambió de familia

```yaml
# Antes (Jackson 2, no arranca en Boot 4)
spring.jackson.serialization.write-dates-as-timestamps: false
# Ahora (Jackson 3)
spring.jackson.datatype.datetime.write-dates-as-timestamps: false
```

Boot 4 usa **Jackson 3** (paquete `tools.jackson`), donde `WRITE_DATES_AS_TIMESTAMPS` dejó de
ser un `SerializationFeature` y pasó a `tools.jackson.databind.cfg.DateTimeFeature`. Con la
clave vieja el arranque falla con `No enum constant SerializationFeature.write-dates-as-timestamps`.

Esto sostiene una convención del proyecto: fechas en **ISO-8601 con zona**, nunca epoch.

### 6. Spring Security: desactivada temporalmente

`spring-boot-starter-security` y `spring-security-test` quedan **comentados** en el `pom.xml`,
con la explicación y la condición de reactivación escritas en el propio archivo.

Sin un `SecurityFilterChain` configurado, Spring Security bloquea **todas** las rutas con HTTP
Basic: ni `/api/v1/public/ping` ni Swagger UI responden, y el arquetipo no se puede verificar.
Se prefirió desactivarla a dejar en el repositorio una configuración `permitAll()` que alguien
pudiera olvidar endurecer antes de staging.

**Condición de reactivación:** al implementar el dashboard interno (`/api/v1/admin/**`) y los
magic links. La `SecurityConfig` debe dejar abiertas `/api/v1/public/**`, `/actuator/health` y
Swagger, y exigir autenticación en `/api/v1/admin/**`, con credenciales por variable de entorno.

## Cambios de empaquetado asociados

- **Migraciones**: el SQL vive en `database/migrations/` pero Flyway lee
  `classpath:db/migration`, así que nadie lo leía. El `pom.xml` ahora las empaqueta con un
  bloque `<resources>` desde su ubicación original: **una sola fuente de verdad**, y las
  migraciones siguen siendo propiedad del área `database/`.
- **Contexto de Docker**: como el build necesita `backend/` y `database/`, el contexto pasa a
  ser la raíz del repositorio (`context: ..`, `dockerfile: backend/Dockerfile`). Se añadió un
  `.dockerignore` en la raíz.
- **Maven wrapper**: `mvnw` y `.mvn/` no existían pese a estar documentados en CLAUDE.md y ser
  invocados por el `Dockerfile`. Generados con `mvn wrapper:wrapper`.

## Consecuencias

**Positivas:** el arquetipo compila, arranca, aplica la migración V1 y responde. El equipo
puede empezar la Semana 1 sobre una base verificada.

**Negativas:** la API queda **sin autenticación** hasta que se reactive Spring Security. Es
aceptable mientras no haya lógica de negocio ni datos reales, pero es **bloqueante antes de
exponer cualquier cosa en staging**.

**Reversión:** baja dificultad. Son cambios de coordenadas y de claves de configuración,
todos localizados en `pom.xml` y `application.yml`.
