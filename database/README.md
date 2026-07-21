# database/ — PostgreSQL: esquema, migraciones y datos semilla

**Responsable principal (Data Owner):** - — Backend Developer
**Revisor técnico:** - — Backend Developer (índices y rendimiento)
**Aprueba cambios de modelo:** Andrés Segura — Solution Architect
**Objetivo:** ser la fuente de verdad del modelo de datos y el único lugar por donde cambia el esquema.

> Si estas personas cambian, **actualiza esta línea en el mismo PR de la reasignación.**

---

## Qué le corresponde al Data Owner

1. Mantener el esquema y **todas** las migraciones.
2. Mantener el diccionario de datos y el diagrama ER en [`docs/`](docs/).
3. Cargar y versionar los datos semilla del instrumento del benchmark.
4. Ser el punto único de contacto para "necesito un campo nuevo".
5. Revisar que ninguna consulta del dashboard haga *full scan* sobre tablas que crecen.

---

## Reglas duras

1. **Nadie modifica el esquema a mano.** Ni en local, ni en staging, ni "solo para probar". Todo cambio es una migración versionada, revisada en un PR.
2. **Las migraciones son inmutables una vez mergeadas.** Para corregir algo se crea una migración nueva, nunca se edita una existente. Editar una migración ya aplicada rompe la base de datos de todo el equipo y de staging.
3. **`ddl-auto: validate`.** Hibernate valida el esquema; no lo crea ni lo modifica.
4. **Ninguna credencial en este directorio.** La cadena de conexión va en variables de entorno.
5. **Ningún dato real de personas en `seeds/`.** Solo datos sintéticos.

---

## Cómo poner la información de PostgreSQL

**Nunca escribas aquí una cadena de conexión con credenciales.** Lo que sí se documenta:

| Qué | Dónde va |
|---|---|
| Versión de PostgreSQL, extensiones necesarias | Este README, sección "Configuración" |
| Nombre de la base, del usuario y de los esquemas | Este README |
| Migraciones | `migrations/` |
| Datos semilla | `seeds/` |
| Diccionario de datos y diagrama ER | `docs/` |
| Host, usuario y contraseña reales | Variables de entorno + chat privado del equipo |
| Proveedor elegido y su configuración | Un ADR en `docs/architecture/adr/` |

### Configuración

| | |
|---|---|
| Versión | PostgreSQL 16 |
| Base de datos | `dcplatform` |
| Usuario de la aplicación | `dcplatform` |
| Esquema | `public` |
| Zona horaria | UTC (siempre) |
| Codificación | UTF8 |
| Extensiones | `pgcrypto` (generación de UUID) |
| Proveedor | [por definir — ver `docs/ci-cd-notes.md`] |

### Levantar la base en local

```bash
cd ../backend
docker compose up -d postgres
# las migraciones se aplican solas al arrancar la aplicación
```

---

## Cómo entrar a la base de datos desde la terminal

El contenedor se llama `dcplatform-postgres`, la base y el usuario son ambos `dcplatform`.
**No hace falta tener PostgreSQL instalado en tu máquina:** `psql` ya viene dentro del
contenedor.

### Sesión interactiva (lo más habitual)

```bash
docker exec -it dcplatform-postgres psql -U dcplatform -d dcplatform
```

Se abre el prompt `dcplatform=#`. Para salir: `\q`.

### Una sola consulta, sin abrir sesión

Útil en scripts y para verificar cosas rápido:

```bash
docker exec dcplatform-postgres psql -U dcplatform -d dcplatform -c "SELECT count(*) FROM leads;"
```

Sin `-it` porque no hay interacción. Con `-t -A` la salida sale limpia, sin cabecera ni
bordes, ideal para encadenar con otros comandos.

### Comandos de psql que más vas a usar

| Comando | Qué hace |
|---|---|
| `\dt` | Listar las tablas |
| `\d leads` | Ver las columnas, índices y restricciones de una tabla |
| `\d+ leads` | Igual, con tamaños y comentarios |
| `\di` | Listar los índices |
| `\l` | Listar las bases de datos |
| `\dn` | Listar los esquemas |
| `\x` | Alternar la salida vertical (se agradece en tablas anchas) |
| `\timing` | Mostrar cuánto tarda cada consulta |
| `\?` | Ayuda de los comandos `\` |
| `\h SELECT` | Ayuda de la sintaxis SQL de un comando |
| `\q` | Salir |

### Verificar que el esquema está aplicado

```bash
# Las tablas creadas por las migraciones
docker exec dcplatform-postgres psql -U dcplatform -d dcplatform -c "\dt"

# Qué migraciones aplicó Flyway y si alguna falló
docker exec dcplatform-postgres psql -U dcplatform -d dcplatform \
  -c "SELECT version, description, success, installed_on FROM flyway_schema_history ORDER BY installed_at;"
```

Si `success` es `false` en alguna fila, la base quedó a medias: hay que arreglar la
migración y recrear el volumen (ver más abajo).

### Cargar un archivo .sql (datos semilla)

```bash
# Desde database/seeds/
docker exec -i dcplatform-postgres psql -U dcplatform -d dcplatform < seeds/benchmark_instrument_v1.sql
```

`-i` (sin `t`) pasa el archivo por la entrada estándar. El orden importa: primero el
instrumento, después las respuestas de ejemplo.

### Conectarte con un cliente gráfico (DBeaver, DataGrip, pgAdmin)

| Campo | Valor |
|---|---|
| Host | `localhost` |
| Puerto | el de `DB_PORT` en `backend/.env` (por defecto `5432`) |
| Base de datos | `dcplatform` |
| Usuario | `dcplatform` |
| Contraseña | la de `DB_PASSWORD` en `backend/.env` |

> El puerto que ves aquí es el publicado en **tu máquina**. Dentro de la red de Docker los
> contenedores siempre se hablan por el `5432`, por eso `DB_URL` de los servicios apunta a
> `postgres:5432` y no a `localhost`.

### Empezar de cero

Cuidado: **borra todos los datos locales**. Es la forma correcta de recuperarse de una
migración que quedó a medias.

```bash
cd ../backend
docker compose down -v      # -v elimina también el volumen pgdata
docker compose up -d postgres
```

Al volver a arrancar la aplicación, Flyway aplica las migraciones desde cero.

### Copia de seguridad y restauración

```bash
# Volcar
docker exec dcplatform-postgres pg_dump -U dcplatform -d dcplatform > backup.sql

# Restaurar
docker exec -i dcplatform-postgres psql -U dcplatform -d dcplatform < backup.sql
```

Los `.sql.bak` y los `.dump` están en el `.gitignore`: **los volcados no entran al repositorio.**

---

## Migraciones

Las migraciones viven en `migrations/` y **esa es su única copia**. No se duplican en `backend/src/main/resources/db/migration/`: el `pom.xml` las empaqueta desde aquí hacia `db/migration` dentro del JAR, que es donde Flyway las busca. **Decisión tomada — no copies archivos `.sql` al backend**, se desincronizarían.

**Convención de nombres:** `V{n}__descripcion_en_snake_case.sql`

```
V1__initial_schema.sql
V2__benchmark_instrument.sql
V3__outreach_tables.sql
```

**Antes de abrir un PR con una migración:**

- ☐ ¿El nombre sigue la convención y el número es el siguiente disponible?
- ☐ ¿Corre desde cero sobre una base vacía?
- ☐ ¿Corre sobre una base que ya tiene las migraciones anteriores?
- ☐ ¿Toda clave foránea tiene índice?
- ☐ ¿Las columnas de fecha son `timestamptz`?
- ☐ ¿Las de dinero son `numeric(19,4)`?
- ☐ ¿Está actualizado el diccionario de datos?

---

## Convenciones

Las completas están en [`docs/conventions/datos.md`](../docs/conventions/datos.md). El resumen:

`snake_case` · tablas en plural · claves primarias UUID · `timestamptz` en UTC · dinero `numeric(19,4)` · porcentajes como fracción · enums con `varchar` + `CHECK` · auditoría `created_at`/`updated_at` en toda tabla · borrado lógico en `leads` y `outreach_contacts`.

---

## Estado actual

- ☑ Convenciones publicadas
- ☑ Migración inicial de referencia (`V1__initial_schema.sql`)
- ☐ Instrumento del benchmark cargado (depende de que el cliente entregue las preguntas)
- ☐ Tablas de outreach (Semana 4)
- ☐ Diagrama ER en `docs/`
- ☐ Proveedor elegido y base de staging creada
