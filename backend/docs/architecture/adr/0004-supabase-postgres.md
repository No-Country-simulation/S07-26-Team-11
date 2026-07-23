# ADR-0004 — Supabase como proveedor de PostgreSQL

**Estado:** Aceptado
**Fecha:** 2026-07-23
**Autor:** Backend
**Afecta a:** `backend/src/main/resources/application.yml`, `backend/.env.example`, `database/`

## Contexto

El arquetipo arrancaba contra el PostgreSQL de `docker-compose`. Eso alcanza para trabajar en
local, pero no para que el frontend consuma la API, ni para desplegar en staging, ni para que
el equipo comparta los mismos datos: cada persona tenía su propia base vacía.

`database/README.md` dejaba el proveedor en *"[por definir]"* y exigía registrar la decisión
en un ADR. Este es ese ADR.

Restricciones de la Semana 0: presupuesto cero, cinco semanas de proyecto, y un equipo de ocho
personas que necesita levantar el proyecto sin pelearse con la infraestructura. El esquema ya
está definido en `database/migrations/V1__initial_schema.sql` y usa `pgcrypto`, `jsonb`,
`inet` y `SELECT … FOR UPDATE SKIP LOCKED` para la cola de trabajos: hace falta PostgreSQL de
verdad, no un sustituto.

## Opciones consideradas

| Opción | Pros | Contras |
|---|---|---|
| **Supabase** | Plan gratuito con PostgreSQL 17 completo; consola SQL y editor de tablas en el navegador; pooler administrado; el proyecto ya estaba creado | El plan gratuito pausa el proyecto tras una semana sin actividad; la conexión directa es solo IPv6 |
| Neon | Plan gratuito, *branching* de bases de datos | Una herramienta más que aprender; el *branching* no resuelve ningún problema que tengamos ahora |
| Railway / Render | Despliegan la API y la base juntas | El plan gratuito de Postgres caduca o es de prueba |
| Solo Postgres en Docker | Sin dependencias externas, sin límites | No hay base compartida: el frontend no puede consumir nada y no hay camino a staging |

## Decisión

Usamos **Supabase** (proyecto `db-S07-26-Team-11`), y dentro de Supabase, el
**session pooler**:

```
jdbc:postgresql://aws-1-<región>.pooler.supabase.com:5432/postgres?sslmode=require
usuario: postgres.<project-ref>
```

Dos detalles que no son preferencias, sino restricciones técnicas comprobadas:

**1. Session pooler, no conexión directa.** El host directo
`db.<project-ref>.supabase.co` publica **únicamente un registro AAAA**:

```
$ getent ahosts db.cgfnaoblcbjcbijmbeps.supabase.co
2600:1f16:111a:af00:de2c:5b6e:5456:9735   STREAM
```

Quien no tenga IPv6 en su red no puede conectarse, y falla con un genérico "connection
refused" que no explica por qué. El host del pooler resuelve a IPv4 y funciona para todos.

**2. Puerto 5432, no 6543.** El 5432 del pooler es modo *session*: cada cliente recibe una
conexión propia mientras dure. El 6543 es modo *transaction*, donde varias sesiones comparten
la misma conexión física entre transacciones — y ahí **Flyway se rompe**, porque toma un
advisory lock a nivel de sesión para serializar las migraciones, y ese lock no sobrevive.
Además obliga a poner `prepareThreshold=0` en la cadena JDBC porque los prepared statements
tampoco sobreviven.

La configuración de Hikari se ajustó a este escenario: el pooler cierra las conexiones
ociosas por su cuenta, así que `keepalive-time` (2 min) < `idle-timeout` (5 min) <
`max-lifetime` (10 min) para que Hikari las recicle antes y nadie se encuentre con un
"connection closed" en el primer request después de un rato sin tráfico.

Los defaults de `application.yml` siguen apuntando al Postgres de `docker-compose`: sin
`.env`, el proyecto arranca igual. `docker-compose.yml` no se tocó — es solo para
desarrollo local.

## Consecuencias

**Positivas:**
- Una sola base para todo el equipo: el frontend puede consumir la API con datos reales.
- Las migraciones se aplican solas al arrancar. `V1__initial_schema.sql` ya está aplicada
  (validada contra `flyway_schema_history`).
- Consola SQL en el navegador: el Data Owner puede inspeccionar sin instalar nada.
- `sslmode=require` en toda conexión, sin trabajo extra.

**Negativas:**
- El plan gratuito **pausa el proyecto tras ~7 días sin actividad**. Hay que reactivarlo a
  mano desde el panel. Si la API arranca contra un proyecto pausado, falla en el arranque.
- Todo el equipo escribe sobre la misma base. No hay aislamiento entre personas: quien
  necesite experimentar, que use `docker compose up -d postgres`.
- Cualquiera con el `.env` puede migrar la base compartida. Por eso existe
  `FLYWAY_ENABLED=false`: permite probar la conectividad sin aplicar migraciones.
- El límite de conexiones del plan gratuito obliga a mantener `DB_POOL_SIZE` bajo (5).

**Qué habría que hacer para revertirla:** cambiar tres variables de entorno
(`DB_URL`, `DB_USER`, `DB_PASSWORD`) y restaurar el volcado en otro proveedor. Nada del
código depende de Supabase: es PostgreSQL estándar, sin extensiones propietarias, y las
migraciones son la fuente de verdad del esquema. Es barata de revertir — razón de más para
no dedicarle más tiempo a la decisión ahora.

## Pendientes

- Registrar el proveedor en la tabla "Configuración" de `database/README.md`, hoy en
  *"[por definir]"*.
- Documentar en `docs/ci-cd-notas.md` cómo se reactiva el proyecto si Supabase lo pausa.
- Decidir si staging usa el mismo proyecto o uno aparte. **Usar el mismo significa que una
  migración mal probada afecta a la demo del cliente.**
