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

La base de datos del proyecto es **Supabase** (PostgreSQL 17 gestionado). Ver [ADR-0004](docs/architecture/adr/0004-supabase-postgres.md).

```bash
# 1. Variables de entorno
cp .env.example .env
# Pide los valores de DB_URL, DB_USER y DB_PASSWORD por el chat privado del equipo
# y completá el .env. Nunca los pegues en un issue, un PR ni este README.

# 2. Cargar las variables y arrancar (ver "Variables de entorno" para otros sistemas)
set -a && . ./.env && set +a
./mvnw spring-boot:run

# 3. Verificar
curl http://localhost:8080/api/v1/public/ping        # el proceso responde
curl http://localhost:8080/api/v1/public/db-status   # la conexión con la base funciona
curl http://localhost:8080/actuator/health
# Documentación: http://localhost:8080/swagger-ui.html
```

No hace falta levantar ningún contenedor: la base ya está en Supabase y el esquema ya
está migrado.

### Alternativa: PostgreSQL local

Sirve para trabajar sin conexión o para no tocar la base compartida por todo el equipo.

```bash
docker compose up -d postgres
```

Y en tu `.env`:

```bash
DB_URL=jdbc:postgresql://localhost:5432/dcplatform
DB_USER=dcplatform
DB_PASSWORD=<el mismo que tengas en DB_PASSWORD para el contenedor>
```

Sin Docker: instala PostgreSQL 16 o superior, crea la base `dcplatform` y ajusta `DB_URL`.

**¿Ya tienes algo ocupando el puerto 5432?** Define `DB_PORT` en tu `.env` (por ejemplo
`DB_PORT=5433`) y ajusta `DB_URL` para que coincida. Dentro de la red de Docker el puerto
sigue siendo el 5432; `DB_PORT` solo cambia el puerto publicado en tu máquina.

> `docker-compose.yml` es **solo para desarrollo local**: sus servicios `api` y `worker`
> apuntan siempre al contenedor `postgres`, no a Supabase.

### Comprobar el estado de la conexión

`GET /api/v1/public/db-status` pide una conexión real al pool y ejecuta una consulta. Es lo
que distingue "la API está caída" de "la API está arriba pero no llega a la base" — algo que
`/ping` no puede decirte.

```bash
curl -s http://localhost:8080/api/v1/public/db-status | jq
```

```json
{
  "status": "UP",
  "latencyMs": 166,
  "database": "PostgreSQL 17.6",
  "driver": "PostgreSQL JDBC Driver 42.7.11",
  "url": "jdbc:postgresql://aws-1-us-east-2.pooler.supabase.com:5432/postgres?sslmode=require",
  "username": "postgres.xxxxxxxxxxxx",
  "schema": "public",
  "readOnly": false,
  "pool": { "name": "dcplatform-pool", "active": 1, "idle": 1, "total": 2, "waiting": 0, "max": 5 },
  "timestamp": "2026-07-23T16:02:58.270436316Z"
}
```

- **`200`** → conexión establecida. **`503`** → no se pudo conectar.
- Si falla, la respuesta trae solo el código `sqlState` (`08001` no se pudo abrir la
  conexión, `28P01` contraseña inválida, `3D000` la base no existe). **El detalle completo
  va al log del servidor, nunca al cliente.**
- Los campos de infraestructura (`url`, `username`, `pool`, …) solo aparecen si
  `DB_STATUS_DETAILS=true`. La contraseña se enmascara siempre, aunque venga embebida en la
  cadena JDBC.

> ⚠️ Mientras Spring Security siga desactivada, `/api/v1/public/**` no pide autenticación.
> **Pon `DB_STATUS_DETAILS=false` en staging**, o estarás publicando el host y el usuario de
> la base a cualquiera que pase.

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

### Regla número uno: el `.env` no se sube

`.env` ya está en el `.gitignore` de la raíz. **Compruébalo antes de tu primer commit:**

```bash
git check-ignore -v backend/.env
# debe imprimir:  .gitignore:2:.env    backend/.env
```

Si ese comando **no imprime nada**, para: tu `.env` no está ignorado y el siguiente
`git add .` publica las credenciales del equipo. Si ya subiste uno por error, no basta con
borrarlo — queda en el historial: avisa en el chat y **rota las credenciales**
(ver la tabla de emergencias en [`CONTRIBUTING.md`](../CONTRIBUTING.md)).

### Cómo cargarlas

Spring lee la configuración desde el entorno del proceso; **no lee el archivo `.env` por sí
solo**. Hay que cargarlo antes de arrancar, y la forma depende de con qué lo ejecutes:

**Linux / macOS — bash o zsh**

```bash
set -a && . ./.env && set +a     # exporta todo lo que declara el .env
./mvnw spring-boot:run
```

`set -a` hace que cada asignación se exporte sola; `set +a` vuelve a la normalidad. Vale
solo para la terminal actual: si abres otra, hay que repetirlo.

**Windows — PowerShell**

```powershell
Get-Content .env | Where-Object { $_ -match '^\s*[^#].*=' } | ForEach-Object {
    $name, $value = $_ -split '=', 2
    [Environment]::SetEnvironmentVariable($name.Trim(), $value.Trim(), 'Process')
}
.\mvnw.cmd spring-boot:run
```

**IntelliJ IDEA** — la opción más cómoda si depuras desde el IDE:

- `Run → Edit Configurations… → ApiApplication → Environment variables`, y pegar las
  variables separadas por `;`, o
- instalar el plugin **EnvFile** (`Settings → Plugins`), y en la pestaña *EnvFile* de la
  configuración marcar *Enable EnvFile* y añadir `backend/.env`. Esto es preferible: no
  duplica los valores dentro de la configuración del IDE, que sí se puede subir por error.

**VS Code** — en `.vscode/launch.json`:

```json
{ "type": "java", "name": "ApiApplication", "request": "launch",
  "mainClass": "com.dcplatform.api.ApiApplication",
  "envFile": "${workspaceFolder}/backend/.env" }
```

**Docker Compose** — no hay que hacer nada: Compose carga solo el `.env` que esté junto al
`docker-compose.yml`, o sea `backend/.env`.

**Staging** — en el panel del proveedor, nunca en un archivo dentro de la imagen.

**GitHub Actions** — `Settings → Secrets and variables → Actions`, y en el workflow:

```yaml
env:
  DB_URL: ${{ secrets.DB_URL }}
  DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
```

### Variables de la base de datos

| Variable | Por defecto | Para qué |
|---|---|---|
| `DB_URL` | Postgres local de Compose | Cadena JDBC. Con Supabase, la del **session pooler** |
| `DB_USER` | `dcplatform` | Con Supabase es `postgres.<project-ref>`, no `postgres` |
| `DB_PASSWORD` | — | Se pide por el chat privado del equipo |
| `DB_SCHEMA` | `public` | Esquema donde Flyway lleva su historial |
| `DB_POOL_SIZE` | `5` | Máximo de conexiones. Los planes gratuitos las limitan |
| `DB_POOL_MIN_IDLE` | `1` | Conexiones ociosas que se mantienen abiertas |
| `DB_CONNECTION_TIMEOUT_MS` | `15000` | Espera máxima por una conexión del pool |
| `DB_KEEPALIVE_MS` / `DB_IDLE_TIMEOUT_MS` / `DB_MAX_LIFETIME_MS` | `120000` / `300000` / `600000` | Reciclado de conexiones. **Mantener el orden `keepalive < idle < max-lifetime`**: el pooler de Supabase corta las conexiones ociosas por su cuenta, y esto hace que Hikari las renueve antes de que eso pase |
| `FLYWAY_ENABLED` | `true` | `false` arranca sin aplicar migraciones. Útil para probar solo la conectividad contra la base compartida |
| `DB_STATUS_DETAILS` | `true` | Detalle de `/api/v1/public/db-status`. **`false` en staging** |

**Sobre Supabase:** usa siempre el *session pooler*
(`aws-1-<región>.pooler.supabase.com:5432`), no la conexión directa
(`db.<project-ref>.supabase.co`), que resuelve **solo a IPv6** y deja fuera a quien no tenga
IPv6 en su red. Y **no uses el puerto 6543** (*transaction pooler*): rompe Flyway. El
razonamiento completo está en [ADR-0004](docs/architecture/adr/0004-supabase-postgres.md).

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
- ☑ Conexión a Supabase (session pooler) y migraciones aplicadas
- ☑ Endpoint de estado de la conexión: `/api/v1/public/db-status`
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
