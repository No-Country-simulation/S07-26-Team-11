# No Country - S07-26-Team 11

## Infraestructura y PDF Generator

Plataforma Integrada de Benchmark de Data Centers


## Objetivo

**Infraestructura y PDF Generator** es la infraestructura que conecta tres componentes públicos de una startup de infraestructura de IA — el **reporte de industria**, la **calculadora de estimación** y el **benchmark de madurez** — y los convierte en un sistema integrado.

Un operador de data center entra por la calculadora, cuantifica cuánta capacidad está pagando sin producir nada, completa el benchmark de madurez, y recibe automáticamente un **PDF institucional** con su posición en la industria y sus KPIs. El equipo comercial ve las respuestas acumuladas en un dashboard y puede disparar campañas de invitación al benchmark desde una lista de contactos.

**Entregable de este proyecto:** todo lo anterior funcionando en un ambiente de **staging accesible públicamente**, como demo MVP.

**Duración:** 5 semanas (Semana 0 a Semana 4).

---

## Integrantes del equipo

> Completar: la primera prueba de que el flujo de Git funciona.

| Nombre | Rol | LinkedIn | GitHub / Sitio personal |
|---|---|---|---|
| Sebastian Di Giuseppe | Project Manager | [link](https://www.linkedin.com/in/sebadigiuseppe/) | [link](https://seba-dg-portfolio.ai.studio) |
| Andrés Segura | Software / Solution Architect | [link](https://www.linkedin.com/in/andresseguradev/?lipi=urn%3Ali%3Apage%3Ad_flagship3_detail_base%3Bjf4i0OnwQYeGAAi6HP32xA%3D%3D) | [link](https://andres-segura.dev) |
| Rider Renato Manrique | Backend Developer | [link](https://www.linkedin.com/in/rider-manrique/?lipi=urn%3Ali%3Apage%3Ad_flagship3_detail_base%3Bjf4i0OnwQYeGAAi6HP32xA%3D%3D) | - |
| Héctor Armando Cortez | Backend Developer | [link](https://www.linkedin.com/in/hector-cortez-cy/?lipi=urn%3Ali%3Apage%3Ad_flagship3_detail_base%3Bjf4i0OnwQYeGAAi6HP32xA%3D%3D) | - |
| Matias Almaraz | Backend Developer | [link](https://www.linkedin.com/in/matias-almaraz-197005275/?lipi=urn%3Ali%3Apage%3Ad_flagship3_detail_base%3Bjf4i0OnwQYeGAAi6HP32xA%3D%3D) | - |
| Augusto Zanetta | Backend Developer | [link](https://www.linkedin.com/in/augusto-zanetta-8745012b8/?lipi=urn%3Ali%3Apage%3Ad_flagship3_detail_base%3BqQ1Kj%2BpOQv%2BheNLtnVrzhw%3D%3D) | - |
| Oskar Morales | Full Stack Developer | [link](https://www.linkedin.com/in/oskarmorales/?lipi=urn%3Ali%3Apage%3Ad_flagship3_detail_base%3Bjf4i0OnwQYeGAAi6HP32xA%3D%3D) | - |
| Maria Daniela Monti Julien | Frontend Developer | [link](https://www.linkedin.com/in/mariamonti/?lipi=urn%3Ali%3Apage%3Ad_flagship3_detail_base%3Bjf4i0OnwQYeGAAi6HP32xA%3D%3D) | - |
| Vanina Restelli | UX/UI Designer | [link](https://www.linkedin.com/in/vaninarestelli/?lipi=urn%3Ali%3Apage%3Ad_flagship3_detail_base%3Bjf4i0OnwQYeGAAi6HP32xA%3D%3D) | - |
| Pamela Calafate | QA Tester | [link](https://www.linkedin.com/in/pamelacalafate/?lipi=urn%3Ali%3Apage%3Ad_flagship3_detail_base%3Bjf4i0OnwQYeGAAi6HP32xA%3D%3D) | - |

---

## Estructura del repositorio

Este es un **monorepo**: un solo repositorio de Git, una sola rama `main`, y cada entregable en su propio directorio con su propio README.

| Directorio | Entregable | Responsable principal | README |
|---|---|---|---|
| [`backend/`](backend/) | API, captura de email, PDF Generator, outreach | - | [ver](backend/README.md) |
| [`frontend/`](frontend/) | Sitio público (reporte, calculadora, benchmark) y dashboard interno | - | [ver](frontend/README.md) |
| [`database/`](database/) | Esquema PostgreSQL, migraciones, datos semilla | - | [ver](database/README.md) |
| [`testing/`](testing/) | Plan de pruebas, colecciones, E2E y reportes | - | [ver](testing/README.md) |
| [`design/`](design/) | Sistema de diseño, template del PDF, assets de marca | - | [ver](design/README.md) |
| [`docs/`](docs/) | Arquitectura, ADRs, contratos de datos, catálogo de endpoints | Arquitecto | [ver](docs/README.md) |

**Documentos raíz:**

- [`PROJECT.md`](PROJECT.md) — Guía del Project Manager: calendario, reuniones, seguimiento de entregables y relación con el cliente.
- [`CONTRIBUTING.md`](CONTRIBUTING.md) — Flujo de Git completo, convenciones de commits y reglas de PR.
- [`CLAUDE.md`](CLAUDE.md) — Contexto del proyecto para asistentes de IA.
- [`docs/API.md`](docs/API.md) — **Catálogo de todos los endpoints.** Contrato entre backend y frontend.

---

## Sobre el stack técnico de este arquetipo

El arquetipo trae Java 21 + Spring Boot + PostgreSQL en el backend y Next.js en el frontend. **Estas tecnologías son un punto de partida, no una imposición.**

Se eligieron por familiaridad con ellas y porque permiten arrancar el día 1 sin discutir. **La decisión final de cada equipo sobre su propia tecnología es suya.** Si el equipo de backend prefiere otro lenguaje o el de frontend otro framework, pueden cambiarlo — con dos condiciones innegociables:

1. **Se respeta el contrato de API** documentado en [`docs/API.md`](docs/API.md). El contrato es el límite entre equipos; lo que pase de ese límite hacia adentro es decisión de quien lo implementa.
2. **Se cumple el objetivo final** y el sistema queda desplegado en staging.

Todo cambio de tecnología se documenta con un ADR en [`docs/architecture/adr/`](docs/architecture/adr/), explicando por qué. No hace falta pedir permiso: hace falta dejar constancia.

---

## Secretos y variables de entorno

> **Regla número uno del proyecto: ningún secreto entra al repositorio. Nunca. Ni en una rama temporal, ni "solo para probar", ni comentado.**

Un secreto es cualquier cadena que dé acceso a algo: contraseñas de base de datos, API keys del proveedor de email, tokens de despliegue, claves de firma de JWT, URLs de conexión con credenciales embebidas.

### Cómo trabajamos con ellos

1. Cada directorio de proyecto tiene un archivo **`.env.example`** con **los nombres** de las variables y valores falsos de ejemplo. Ese archivo **sí** se versiona.
2. Cada persona copia `.env.example` a `.env` en su máquina y pone ahí los valores reales. El archivo `.env` **está en `.gitignore` y nunca se sube**.
3. Los valores reales se comparten por el **chat privado del equipo o un gestor de contraseñas**, nunca por el repositorio, nunca en un issue, nunca en un PR.
4. Cuando alguien agrega una variable nueva, **la agrega también a `.env.example`** en el mismo PR. Si no, el resto del equipo no puede levantar el proyecto y lo van a descubrir a las 11 de la noche.

---

## Cómo ejecutar el proyecto localmente

**Requisitos:** Git, Docker y Docker Compose (recomendado), o bien JDK 21 + Node 20+ + PostgreSQL 16 instalados directamente.

Antes que nada configurar ssh en tu perffil de Github, consultar con Solution Architect en caso de duda.

```bash
# 1. Clonar y posicionarse en la rama de trabajo
git clone git@github.com:No-Country-simulation/S07-26-Team-11.git
cd S07-26-Team-11/
git checkout dev

# 2. Configurar variables de entorno
cp backend/.env.example backend/.env
cp frontend/.env.example frontend/.env
# editar ambos .env con los valores reales

# 3. Levantar la base de datos (y opcionalmente todo el stack)
cd backend
docker compose up -d postgres      # solo la base de datos
# docker compose up --build        # todo el stack

# 4. Backend
./mvnw spring-boot:run             # http://localhost:8080
# Documentación de la API: http://localhost:8080/swagger-ui.html

# 5. Frontend (en otra terminal)
cd frontend
npm install
npm run dev                        # http://localhost:3000
```

Instrucciones detalladas y solución de problemas en el README de cada directorio.

---

## Dónde desplegar (opciones evaluadas por Architect Solution)

Ninguna está decidida. El equipo elige y lo registra en un ADR.

| Capa | Opciones | Nota |
|---|---|---|
| **Frontend** | Vercel (Hobby), Cloudflare Pages, Netlify | Vercel es lo natural para Next.js, pero **su plan Hobby prohíbe el uso comercial**: confirmar con el Project Manager o Cliente. Cloudflare Pages no tiene esa restricción |
| **Backend** | Render (free web service), Google Cloud Run, Azure/AWS free tier (12 meses) | Los servicios gratuitos de Render se apagan por inactividad y tardan cerca de un minuto en despertar. Mitigación: un ping programado antes de cada demo |
| **Base de datos** | Neon, Supabase, Render Postgres | Neon no pausa el proyecto y permite una rama de BD por rama de Git. Supabase pausa proyectos tras una semana de inactividad |
| **Storage de PDFs** | Cloudflare R2, Supabase Storage | R2 no cobra egreso |
| **Email** | Brevo (campañas, 300/día gratis), Resend (transaccional, ~3.000/mes) | Separarlos evita que una campaña bloquee los correos críticos del sistema |

> Verificar las capas gratuitas antes de comprometerse: cambiaron mucho entre 2024 y 2026. Railway, Fly.io y Koyeb ya no son opciones gratuitas sostenibles.

### CI/CD

**Todavía no hay CI/CD.** Es una decisión consciente de la Semana 0. Lo que hay que saber para decidirlo después está en [`docs/ci-cd-notes.md`](docs/ci-cd-notes.md) — **incluida una recomendación importante sobre repositorio público vs. privado que conviene leer.**

---

## Cómo contribuir

El flujo completo está en [`CONTRIBUTING.md`](CONTRIBUTING.md). El resumen:

```
main  ← solo recibe merges desde dev, una vez por semana, y solo el encargado de la semana
 └── dev  ← rama de integración. Todo el trabajo llega aquí por Pull Request
      ├── backend/nombre-de-la-tarea
      ├── frontend/nombre-de-la-tarea
      ├── database/nombre-de-la-tarea
      ├── testing/nombre-de-la-tarea
      ├── design/nombre-de-la-tarea
      └── docs/nombre-de-la-tarea
```

**Reglas:**

1. **Nadie hace push directo a `main` ni a `dev`.** Todo entra por Pull Request.
2. Tu rama sale de `dev` y vuelve a `dev`.
3. **Modificas solo el directorio de tu área.** Si necesitas tocar el directorio de otra persona, se lo pides a esa persona o la etiquetas en el PR. Excepción: `docs/API.md` y `.env.example`, que son de todos y se actualizan cuando cambia un contrato.
4. Un PR necesita **al menos una aprobación** antes del merge. Los PR que tocan `docs/API.md`, `database/migrations/` o la arquitectura necesitan **la aprobación del arquitecto**.
5. Cada semana, **un encargado rotativo** hace el merge de `dev` a `main`. El calendario de rotación está en [`PROJECT.md`](PROJECT.md).

### Directorio que puede modificar cada rol

| Rol | Directorio propio | Lectura obligatoria |
|---|---|---|
| Arquitecto | `docs/`, arquetipos de todos | — |
| Backend Developers | `backend/`, `database/` | `docs/API.md` |
| Frontend Developer | `frontend/` | `docs/API.md`, `design/` |
| Full Stack Developer | `backend/` (módulo PDF), `frontend/` | `docs/API.md`, `design/` |
| UX/UI Designer | `design/` | `design/README.md` (restricciones del PDF) |
| QA Tester | `testing/` | `docs/API.md` |
| Project Manager | `PROJECT.md`, `README.md` (tabla de integrantes) | Todo |

---

## Qué hacer en cada caso

La regla general: **la comunicación técnica ocurre en el chat privado del equipo, y las decisiones quedan escritas en el repositorio.** Un acuerdo que solo existe en el chat no existe.

| Situación | Qué hacer |
|---|---|
| **No sé cómo hacer algo de mi tarea** | Pregunta en el chat del equipo. Nadie va a juzgarte por preguntar; sí vamos a tener un problema si te bloqueas tres días en silencio. Regla práctica: **si llevas más de 45 minutos atascado en lo mismo, pregunta.** |
| **Tengo una recomendación para otra persona** | Dísela primero de forma directa y en privado, o como comentario en su PR. Comenta el código y la decisión, nunca a la persona: "este endpoint podría devolver 404 en vez de 200 con lista vacía", no "esto está mal hecho". Si es una mejora opcional, dilo explícitamente para que no se lea como un bloqueo. |
| **Encontré un error en el trabajo de otro** | Abre un issue con: qué esperabas, qué pasó, cómo reproducirlo, y captura o log. Etiqueta a la persona responsable. Si es urgente y bloquea a alguien más, además avisa en el chat. |
| **Necesito un cambio en la API** | No lo implementes por tu cuenta. Abre un PR sobre [`docs/API.md`](docs/API.md) proponiendo el cambio y etiqueta al arquitecto y a quien consume ese endpoint. El contrato se cambia antes que el código. |
| **Necesito un campo nuevo en la base de datos** | Pídelo al responsable de `database/`. Nadie modifica el esquema a mano en staging: se hace por migración versionada. |
| **Mi tarea depende de algo que no está listo** | Avísalo el mismo día en el chat y en el daily. Mientras tanto, trabaja contra un mock. Una dependencia bloqueada que se reporta el viernes ya costó una semana. |
| **No voy a alcanzar a entregar a tiempo** | Dilo apenas lo sepas, no el día de la entrega. Se puede recortar alcance, pedir ayuda o reasignar — pero solo si hay tiempo de reaccionar. |
| **No voy a poder seguir en el proyecto** | Avísale al PM y al arquitecto lo antes posible. Se reasigna sin drama. Lo que sí hace daño es desaparecer sin avisar: el README de cada directorio existe justamente para que otra persona pueda retomar el trabajo. |
| **Se filtró un secreto** | Avisa de inmediato en el chat y **rota la credencial**. Ver la sección de secretos arriba. |
| **Staging se cayó** | Avisa en el chat con la hora y qué estabas haciendo. Si es antes de una demo, es prioridad sobre cualquier otra tarea. |
| **Tengo una duda para el cliente** | No contactes al cliente directamente. Pásasela al PM, que las agrupa y las lleva a la reunión semanal. |

### Sobre reasignaciones

Es normal que la disponibilidad de las personas cambie durante un proyecto. Por eso:

- Cada directorio tiene un **responsable principal nombrado en su README**, y ese README se actualiza cuando hay una reasignación.
- Cada README de directorio incluye una sección de **"Estado actual"** que debe permitir a alguien nuevo entender en 10 minutos qué está hecho, qué falta y dónde está lo pendiente.
- Ninguna tarea crítica debería tener una sola persona que sepa cómo funciona. Si tienes conocimiento que nadie más tiene, escríbelo.

---

## Licencia y confidencialidad

[Definir con el equipo y el PM antes de hacer público el repositorio: si el contenido del reporte de industria, las fórmulas de la calculadora o los datos de marca son confidenciales, el repositorio debe ser privado o esos activos deben quedar fuera del repositorio.]
