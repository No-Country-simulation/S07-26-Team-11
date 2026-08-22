# Arquitectura de la API

Este documento describe **cómo está organizada** la API — convenciones, autenticación,
niveles de acceso y catálogo de rutas. Para el contrato detallado de cada endpoint
(request/response body) hoy la referencia es Swagger UI en vivo (ver abajo); no se duplica
aquí para no quedar desactualizado.

---

## Base

- **Base path:** `/api/v1` (todas las rutas de negocio cuelgan de ahí).
- **Formato:** JSON. Errores en formato uniforme **RFC 9457 Problem Details**
  (`type`, `title`, `status`, `detail`, `instance`, `traceId`, y `errors[]` para validación de
  campos) — mismo shape para cualquier error de cualquier endpoint.
- **Documentación interactiva:** `GET /swagger-ui.html` (o `/swagger-ui/index.html`), spec
  cruda en `GET /v3/api-docs`. Esa misma spec es la que el frontend usa para generar sus tipos
  TypeScript (`npm run types:api`) — contract-first en la práctica, aunque no exista un
  `docs/API.md` escrito a mano.

  > `GET /v3/api-docs.yaml` (la variante YAML que sirve springdoc por defecto) devuelve
  > **401**, no 200: el matcher público en `SecurityConfig` es `"/v3/api-docs/**"`, que no
  > cubre `/v3/api-docs.yaml` (no hay `/` antes de `.yaml`). Para generar un `openapi.yaml` hay
  > que pedir la variante JSON pública y convertirla, o agregar el matcher que falta.
- **Descubrimiento:** `GET /` devuelve un JSON con enlaces HATEOAS a la documentación
  disponible (útil como *health check* legible para humanos).

---

## Autenticación y autorización

- **Modelo:** JWT Bearer, stateless — no hay sesiones de servidor
  (`SessionCreationPolicy.STATELESS`). El token viaja en `Authorization: Bearer <token>`.
- **Revocación:** logout no borra el token del lado del cliente nada más — lo invalida en
  servidor insertándolo en la tabla `revoked_tokens`, contra la que se chequea en cada
  request autenticado.
- **Dos sistemas de alta de cuenta que convergen en el mismo JWT:**
  - **Password** (`users` table) — reservado a cuentas de manejo interno; `POST /auth/register`
    exige rol `ADMIN` (ya no es autoservicio).
  - **Magic link** (`leads` table) — alta pública real de cuentas nuevas
    (`POST /public/leads` → email → `POST /public/leads/verify`).
  - Al canjear un magic link, el backend "puentea" hacia el sistema de password: crea (o
    reusa) una fila en `users` con una contraseña inutilizable y emite el mismo tipo de JWT
    que emitiría `/auth/login`. El resto de la API no necesita saber por cuál de los dos
    caminos entró el usuario.
- **Niveles de acceso** (definidos centralmente en `SecurityConfig`, no hay `@PreAuthorize`
  disperso en los controllers):

  | Nivel | Regla | Ejemplos |
  |---|---|---|
  | Público | `permitAll()` | login, captura/verificación de leads, calculadora, health |
  | Autenticado | cualquier JWT válido no revocado | `/documents/**`, `/auth/me`, `/auth/logout` |
  | Admin/Viewer | `hasAnyRole("ADMIN","VIEWER")` | `/admin/**` |
  | Solo admin | `hasRole("ADMIN")` | `/admin/outreach/**`, `POST /auth/register` |

---

## Catálogo de endpoints implementados

`✅` = controller real. `⛔` = referenciado en `SecurityConfig` (o en la ruta REST del
frontend) pero **sin controller implementado todavía** — se lista para que quede claro que la
ruta "existe" en la configuración de seguridad pero no responde.

### Auth (`/api/v1/auth`)

| Método | Ruta | Acceso | |
|---|---|---|---|
| POST | `/auth/login` | Público | ✅ |
| POST | `/auth/logout` | Autenticado | ✅ |
| GET | `/auth/me` | Autenticado | ✅ |
| POST | `/auth/register` | ADMIN | ✅ — alta de cuentas internas, no autoservicio |
| POST | `/auth/request-access` | Público | ⛔ — sin controller, regla huérfana en `SecurityConfig` |

### Leads / magic link (`/api/v1/public/leads`)

| Método | Ruta | Acceso | |
|---|---|---|---|
| POST | `/public/leads` | Público | ✅ — captura de email, dispara el magic link |
| POST | `/public/leads/verify` | Público | ✅ — canjea el token del email por un JWT |

### Calculadora (`/api/v1/public/calculator`)

| Método | Ruta | Acceso | |
|---|---|---|---|
| GET | `/public/calculator/defaults` | Público | ✅ |
| POST | `/public/calculator/estimate` | Público | ✅ |
| POST | `/public/calculator/estimate/unlock-results` | Autenticado | ✅ |
| GET | `/public/calculator/estimates/{id}` | Autenticado | ✅ |

### Benchmark (`/api/v1/public/benchmark`)

| Método | Ruta | Acceso | |
|---|---|---|---|
| GET | `/public/benchmark/instrument` | Público | ⛔ — sin controller; el frontend calcula el benchmark en el cliente en su lugar |
| POST | `/public/benchmark/submit` | Autenticado | ✅ |

### Documentos institucionales (`/api/v1/documents`)

| Método | Ruta | Acceso | |
|---|---|---|---|
| POST | `/documents` | Autenticado | ✅ — genera el PDF y lo guarda (reemplaza si `metadata.name` ya existe para ese usuario) |
| POST | `/documents/preview` | Autenticado | ✅ — devuelve el HTML del template sin generar PDF, para iterar el diseño |
| GET | `/documents` | Autenticado | ✅ — documentos del usuario autenticado |
| GET | `/documents/{name}` | Autenticado | ✅ |
| GET | `/documents/{name}/download` | Autenticado | ✅ — redirige a la URL firmada del bucket |

### PDF (pipeline asíncrono, `/api/v1/public/pdf`)

| Método | Ruta | Acceso real | |
|---|---|---|---|
| GET | `/public/pdf/jobs/{jobId}` | **Autenticado** (no público pese al prefijo) | ✅ |
| GET | `/public/pdf/documents/{documentId}/download` | **Autenticado** (no público pese al prefijo) | ✅ |

> Nota: viven bajo `/public/` por convención de paquete, pero no están en la lista
> `permitAll()` de `SecurityConfig` — caen en `anyRequest().authenticated()`. El nombre no
> refleja el acceso real; vale la pena renombrar o agregar la excepción explícita en algún
> momento.

### Admin (`/api/v1/admin`)

| Método | Ruta | Acceso | |
|---|---|---|---|
| GET | `/admin/documents` | ADMIN o VIEWER | ✅ — inventario de documentos de todos los usuarios |
| POST | `/admin/auth/login` | Público | ⛔ — sin controller; el panel admin hoy reusa `/auth/login` |
| `/admin/outreach/**` | — | ADMIN | ⛔ — módulo `outreach` vacío, "pendiente de implementar" (ver su README); el frontend (`/admin/outreach`, `/admin/campaigns`) ya tiene pantallas armadas, sobre datos mock |

### Diagnóstico (público, sin `/documents` ni auth)

| Método | Ruta | |
|---|---|---|
| GET | `/public/ping` | ✅ |
| GET | `/public/db-status` | ✅ — detalle de host/pool solo si `DB_STATUS_DETAILS=true` |
| GET | `/actuator/health`, `/actuator/info` | ✅ |

### Otras reglas de seguridad sin controller (pendientes)

Referenciadas en `SecurityConfig` como públicas pero sin implementación aún:
`GET /public/industry/stats`, `POST /public/webhooks/email/*`, `GET /public/unsubscribe`.

---

Los ítems `⛔` no son errores de este documento — son el estado real al que se ha llegado en este proyecto demo.

