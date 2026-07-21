# backend/ — API, captura de email, PDF Generator y outreach

**Responsables principales:** Héctor Armando Cortez, Rider Renato Manrique Cueto — Backend Developers
**Tareas especializadas:**  Rider Renato Manrique Cueto (módulo PDF), Héctor Armando Cortez (módulo outreach)
**Objetivo:** exponer la API que conecta el frontend con los datos, generar el PDF institucional automáticamente y sostener el pipeline de outreach.

> Si estas personas cambian, **actualiza esta línea en el mismo PR de la reasignación.**

---

## Stack (propuesta del arquetipo, no obligatoria)

| | |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Build | Maven, empaquetado JAR |
| Configuración | YAML (`application.yml`) |
| Base de datos | PostgreSQL 16, migraciones con Flyway |
| Documentación | OpenAPI generado con springdoc → Swagger UI |
| PDF | Thymeleaf → HTML → Open HTML to PDF |

**El equipo de backend puede cambiar este stack, si quieren usar Python/Django, Javascript/Nodejs Express, Hono. O dejarlo así están en su libre decición.** La única condición es respetar el contrato de [`docs/API.md`](../docs/API.md) y dejar constancia en un ADR. Si cambian de lenguaje, este directorio se reemplaza entero — el contrato sigue igual.

> **Nota sobre versiones:** el `pom.xml` era un arquetipo escrito con convenciones de Spring Boot 3.x sobre un parent 4.1.0. Los ajustes necesarios para que compile y arranque (springdoc 3.x, renombrado de módulos de Testcontainers 2.x, versión de Bucket4j, módulo `spring-boot-flyway`, clave de Jackson 3 y desactivación temporal de Spring Security) están hechos y documentados en [ADR-0003](../docs/architecture/adr/0003-ajustes-dependencias-spring-boot-4.md). **Léelo antes de tocar dependencias.**

> ⚠️ **Spring Security está desactivada** (comentada en el `pom.xml`) para poder verificar el arranque del arquetipo. La API no tiene autenticación. Reactivarla junto con una `SecurityConfig` es **bloqueante antes de exponer nada en staging**. Ver ADR-0003.

---

## Estructura

```
src/main/java/com/dcplatform/api/
├── ApiApplication.java          punto de entrada
├── config/                      OpenAPI, CORS, seguridad
├── shared/                      errores, cola, auditoría, utilidades
├── leads/                       captura de email, magic links, consentimiento
├── calculator/                  motor de fórmulas y KPIs
├── benchmark/                   instrumento, scoring, percentiles
├── pdf/                         encolado, plantilla, render, storage
└── outreach/                    importación, envío, tracking
src/main/resources/
├── application.yml
└── templates/                   plantillas Thymeleaf del PDF y de los correos
```

**Las migraciones no viven aquí.** Están en [`database/migrations/`](../database/), que es su
única fuente de verdad; el `pom.xml` las empaqueta dentro del JAR en `db/migration`, que es
donde Flyway las busca. No copies archivos `.sql` a `src/main/resources`.

**Regla de fronteras:** ningún módulo importa clases internas de otro. Se comunican por interfaces públicas y eventos de aplicación. Es lo que permite que varias personas trabajen en paralelo sin conflictos permanentes.

---

## Cómo ejecutarlo localmente

```bash
# 1. Variables de entorno
cp .env.example .env      # y completar

# 2. Base de datos
docker compose up -d postgres

# 3. Aplicación (lee las variables de .env)
set -a && . ./.env && set +a
./mvnw spring-boot:run

# 4. Verificar
curl http://localhost:8080/api/v1/public/ping
curl http://localhost:8080/actuator/health
# Documentación: http://localhost:8080/swagger-ui.html
```

Sin Docker: instala PostgreSQL 16 (es más recomendable actualmente que la v18), crea la base `dcplatform` y ajusta `DB_URL` en tu `.env`.

**¿Ya tienes algo ocupando el puerto 5432?** Define `DB_PORT` en tu `.env` (por ejemplo
`DB_PORT=5433`) y ajusta `DB_URL` para que coincida. Dentro de la red de Docker el puerto
sigue siendo el 5432; `DB_PORT` solo cambia el puerto publicado en tu máquina.

**Con todo en contenedores:**
```bash
docker compose up --build     # api en :8080, worker aparte, postgres en :${DB_PORT:-5432}
```

El contexto de build de la imagen es la **raíz del repositorio**, no `backend/`: el JAR
empaqueta las migraciones desde `database/migrations/`, así que la imagen necesita ver los
dos directorios.

Docker es opcional. Si el equipo prefiere ejecutar el JAR directamente, la configuración del `Dockerfile` puede ignorarse — pero mantenerla facilita que todos tengan el mismo entorno.

---

## Comandos

```bash
./mvnw spring-boot:run                          # ejecutar
./mvnw test                                     # pruebas
./mvnw clean package                            # construir el JAR
./mvnw spring-boot:run -Dspring-boot.run.profiles=worker   # ejecutar como worker
java -jar target/api-0.0.1-SNAPSHOT.jar         # ejecutar el JAR construido
```

---

## Variables de entorno

Todas están declaradas en [`.env.example`](.env.example). **Si agregas una nueva, agrégala ahí en el mismo PR** o el resto del equipo no podrá levantar el proyecto.

Ninguna variable con valor real entra al repositorio. En staging se configuran en el panel del proveedor; en GitHub, en `Settings → Secrets and variables → Actions`.

---

## Cosas que no son negociables

1. **El esquema lo maneja Flyway**, no Hibernate (`ddl-auto: validate`). Nunca un `ALTER TABLE` a mano en staging.
2. **Ningún endpoint devuelve trazas ni mensajes internos.** El formato de error es RFC 9457, uniforme.
3. **La generación de PDF y el envío de correos son asíncronos.** Nunca dentro del request HTTP.
4. **Rate limit en los endpoints públicos de captura de email.** Sin esto, una tarde de spam contamina la base de leads.
5. **La respuesta de `POST /public/leads` es idéntica exista o no el email.** No revelamos si un correo está registrado.
6. **El scoring del benchmark se calcula en el servidor.** Los pesos nunca se exponen al cliente.

---

## Estado actual

- ☑ Arquetipo: aplicación arranca, `/ping` responde, OpenAPI publicado
- ☑ Configuración YAML con variables de entorno
- ☑ Manejo global de errores en formato Problem Details
- ☑ Docker y Compose
- ☐ Módulo `leads` (Semana 1)
- ☐ Módulo `calculator` (Semana 1)
- ☐ Módulo `benchmark` (Semana 2)
- ☐ Módulo `pdf` (Semana 3)
- ☐ Módulo `outreach` (Semana 4)

## Documentos relacionados

- [Contrato de API](../docs/API.md)
- [Arquitectura del PDF Generator](src/main/java/com/dcplatform/api/pdf/PdfGeneratorArchitecture.md)
- [Componentes del backend](../docs/architecture/c3-componentes-api.md)
- [Convenciones de datos](../docs/conventions/datos.md)
