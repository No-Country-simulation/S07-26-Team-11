# Stack técnico

Estado del stack al cierre de esta fase de desarrollo. Este documento describe **lo que
realmente está implementado** (leído del código, no del plan original).

---

## Backend

| | |
|---|---|
| Lenguaje / runtime | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Build | Maven |

**Módulos principales:**

- **Spring Web** — API REST (`spring-boot-starter-web`).
- **Spring Validation** — validación de DTOs con Bean Validation (`@Valid`, `@NotBlank`, etc.).
- **Spring Data JPA + Hibernate** — acceso a datos. `ddl-auto: validate`: Hibernate nunca
  modifica el esquema, solo lo valida contra las entidades. El esquema lo administra Flyway.
- **Spring Security** — stateless (`SessionCreationPolicy.STATELESS`), sin sesiones de
  servidor. Autenticación por JWT vía filtro propio (`JwtAuthenticationFilter`).
- **JJWT** (`jjwt-api` / `jjwt-impl` / `jjwt-jackson`) — emisión y verificación de JWT. Clave
  HMAC (`Keys.hmacShaKeyFor`), algoritmo inferido de la longitud del secreto configurado.
- **Thymeleaf** (`spring-boot-starter-thymeleaf`) — motor de plantillas HTML para el informe
  PDF (`institutional-document.html`).
- **openhtmltopdf** (`pdfbox` + `svg-support` + `slf4j`) + **jsoup** — renderiza el HTML de
  Thymeleaf a PDF.
- **OCI Java SDK** (`oci-java-sdk-objectstorage`) — cliente nativo de Oracle Cloud Object
  Storage (ver sección Bucket).
- **springdoc-openapi** (`springdoc-openapi-starter-webmvc-ui`, v3.0.3 — línea compatible con
  Spring Boot 4.x) — genera la spec OpenAPI y sirve Swagger UI.
- **Spring HATEOAS** — usado en el endpoint raíz (`GET /`) para exponer los enlaces de
  documentación disponibles.
- **Bucket4j** — rate limiting (límites por email/IP en captura de leads).
- **Brevo SDK** (`sib-api-v3-sdk`) — envío de emails transaccionales (magic link).
- **Apache POI** + **Commons CSV** — exportables (Excel/CSV) para el panel admin.
- **Spring Boot Actuator** — `/actuator/health`, `/actuator/info`.
- **Flyway** (`flyway-database-postgresql`) — migraciones versionadas.

**Testing:** `spring-boot-starter-test` (JUnit 5 + Mockito), `spring-security-test`,
`testcontainers-postgresql` (tests de integración contra Postgres real, no mocks).

**Seguridad de contraseñas:** BCrypt (`BCryptPasswordEncoder`).

---

## Base de datos

- **Motor:** PostgreSQL.
- **Proveedor:** [Supabase](https://supabase.com) (Postgres gestionado) en staging/producción.
  En desarrollo local, un contenedor `postgres:16-alpine` vía `backend/docker-compose.yml`
  (mismo motor, sin depender de infraestructura externa para levantar el proyecto).
- **Pooling:** HikariCP, con tuning específico para el pooler de Supabase — el pooler cierra
  conexiones ociosas por su cuenta, así que `keepalive-time` (2 min) < `idle-timeout` (5 min)
  < `max-lifetime` (10 min), para que Hikari siempre recicle primero.
- **Migraciones:** Flyway, fuente única de verdad en [`database/migrations/`](../database/migrations/)
  (fuera de `backend/`, propiedad del área de datos). El `pom.xml` del backend las empaqueta
  dentro del JAR en build time, en la ruta que espera Flyway (`classpath:db/migration`) — no se
  duplican a mano en `src/main/resources`.
- **Esquema actual** (3 migraciones):
  - `V1__initial_schema.sql` — `leads`, `lead_access_tokens`, `calculator_estimates`,
    `benchmark_instruments`, `benchmark_dimensions`, `benchmark_questions`,
    `benchmark_options`, `benchmark_responses`, `benchmark_answers`, `pdf_jobs`,
    `pdf_documents`, `outreach_campaigns`, `outreach_contacts`, `email_suppressions`,
    `admin_users`.
  - `V2__auth_users.sql` — `users`, `revoked_tokens` (login por contraseña; ver
    [`api-architecture.md`](api-architecture.md) para cómo conviven con `leads`).
  - `V3__user_documents.sql` — `user_documents` (informes PDF generados desde `/documents`).
- **Datos semilla:** [`database/seeds/`](../database/seeds/) (instrumento de benchmark, usuario
  admin de desarrollo, respuestas de ejemplo) — no se aplican en producción.

---

## Almacenamiento de archivos (bucket)

- **Proveedor:** Oracle Cloud Infrastructure (OCI) — **Object Storage**, bucket privado.
- **Acceso:** SDK nativo de OCI, autenticado con par de claves de API (no usuario/contraseña):
  `tenancy OCID`, `user OCID`, `fingerprint` y clave privada (ruta a `.pem` en local, o el PEM
  en base64 en Render/staging).
- **Descarga:** URLs firmadas y temporales, nunca acceso público directo al bucket.
  - TTL general de URL firmada: configurable (`OCI_SIGNED_URL_TTL`, default `15m`).
  - Los jobs de PDF del pipeline asíncrono usan su propio TTL de 7 días
    (`app.pdf.signed-url-ttl-days`), pensado para que el usuario pueda volver a descargar un
    informe ya generado sin regenerarlo.
- **Organización:** por prefijo de key — `benchmark-reports/` (informes del benchmark),
  `documents/` (documentos institucionales de `/api/v1/documents`).
- **Degradación controlada:** si falta cualquier variable de configuración de OCI, la
  aplicación **arranca igual** — solo se deshabilitan subida/descarga de PDF, con un `WARN` en
  el log y `503` en los endpoints que dependen del bucket. No es un requisito duro para
  levantar el backend en local.

---

## Frontend

| | |
|---|---|
| Framework | Next.js 15 (App Router) |
| UI library | React 19 |
| Lenguaje | TypeScript 5.7 |
| Estilos | Tailwind CSS 3.4, con tokens de diseño propios (`forest`, `gold`, `base-natural`, etc.) |

- **Cliente HTTP tipado:** los tipos de las respuestas de la API **no se escriben a mano** —
  se generan desde el OpenAPI del backend con `npm run types:api`
  (`openapi-typescript http://localhost:8080/v3/api-docs -o lib/api-types.ts`). Mantiene
  imposible la desincronización silenciosa entre frontend y backend.
- **Autenticación:** JWT Bearer guardado en `localStorage`, adjuntado automáticamente por el
  cliente HTTP (`lib/api.ts`). Estado de sesión en contexto React (`AuthProvider`) +
  componente guard (`RequireAuth`) que redirige a `/login` si no hay sesión válida.
- **Estructura de rutas (App Router):** sitio público (`/`, `/calculadora`, `/reporte`,
  `/benchmark`, `/login`, `/cuenta`, `/auth/verify`) y panel interno (`/admin/overview`,
  `/admin/leads`, `/admin/campaigns`, `/admin/outreach`, `/admin/analytics`,
  `/admin/settings`) — ver [`api-architecture.md`](api-architecture.md) sobre qué de esto
  tiene backend real hoy.
- **Diagnóstico:** `/api-status`, sondea `GET /public/ping` y `GET /public/db-status` del
  backend directamente desde el navegador.

