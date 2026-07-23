# Convenciones de datos y formatos de intercambio

Aplican a todo el proyecto, sin importar el lenguaje que use cada equipo.

## 1. Base de datos (PostgreSQL)

| Aspecto | Convención |
|---|---|
| Nombres | `snake_case`. Tablas en plural (`benchmark_responses`), columnas en singular |
| Claves primarias | `UUID`. Nunca enteros autoincrementales expuestos en URLs públicas |
| Claves foráneas | `{tabla_singular}_id` |
| Fechas | `timestamptz`, siempre en UTC. La conversión a hora local es del frontend |
| Dinero | `numeric(19,4)`. Nunca `float` ni `double` |
| Porcentajes | `numeric(7,4)` como fracción: `0.1523`, no `15.23` |
| Enums | `varchar` + restricción `CHECK`. No tipos enum nativos: migrarlos es doloroso |
| Booleanos | Prefijo `is_` o `has_` |
| Auditoría | `created_at` y `updated_at` en toda tabla |
| Borrado | Lógico (`deleted_at`) para `leads` y `outreach_contacts`, por trazabilidad legal |
| JSON | `jsonb`, solo para `inputs_json` y `outputs_json` de la calculadora. Todo lo que se consulte va en columnas |
| Índices | Sobre toda clave foránea y sobre las columnas de filtro del dashboard |
| Migraciones | `V{n}__descripcion.sql`. **Inmutables una vez mergeadas**: para corregir algo se crea una migración nueva |

## 2. JSON (API)

| Aspecto | Convención |
|---|---|
| Nombres | `camelCase`. La traducción desde `snake_case` la hace el backend |
| Fechas | ISO-8601 con zona: `2026-07-20T14:30:00Z` |
| Identificadores | UUID como string |
| Decimales | Números JSON, no strings. El redondeo de presentación es del frontend |
| Nulos | `null` explícito. Nunca omitir el campo ni enviar `""` para "sin valor" |
| Colecciones vacías | `[]`, nunca `null` |
| Errores | RFC 9457 Problem Details. Ver `docs/API.md` |
| Paginación | `?page=0&size=20&sort=campo,asc` → `{ content, page, size, totalElements, totalPages }` |
| Versionado | Prefijo `/api/v1` |

## 3. Archivos

| Flujo | Entrada | Salida |
|---|---|---|
| Outreach | CSV UTF-8 (delimitador `,`, cabecera `email,nombre,empresa,cargo`) o XLSX. Máx. 5.000 filas / 5 MB | Reporte de importación en JSON |
| Exportes del dashboard | — | CSV UTF-8 **con BOM**, para que Excel no rompa las tildes |
| PDF | HTML renderizado desde plantilla | PDF/A-2b, fuentes incrustadas |

## 4. Cómo se coordina un cambio

1. Cambio de **contrato de API** → PR sobre `docs/API.md`, dos aprobaciones, luego se implementa.
2. Cambio de **esquema de base de datos** → se le pide al responsable de `database/`. Nadie modifica el esquema a mano en staging.
3. Cambio de **variable de entorno** → se agrega a `.env.example` **en el mismo PR** y se avisa en el chat.

El frontend genera sus tipos TypeScript desde el OpenAPI (`npx openapi-typescript`). El QA importa el mismo OpenAPI a Postman. Ninguno de los dos escribe tipos a mano: así la desincronización se vuelve imposible.
