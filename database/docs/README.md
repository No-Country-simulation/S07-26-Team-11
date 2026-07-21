# database/docs/ — Diccionario de datos y diagrama ER

**Responsable:** el Data Owner.

## Que va aqui

| Archivo | Contenido |
|---|---|
| `er-diagram.md` | Diagrama entidad-relacion en Mermaid (`erDiagram`) |
| `diccionario.md` | Tabla por tabla: que significa cada columna, en lenguaje de negocio |
| `consultas-frecuentes.md` | Consultas del dashboard y de los agregados del reporte, con su plan de ejecucion |

## Por que importa

Si el Data Owner deja el proyecto, esto es lo unico que permite que otra persona retome
el modelo sin reconstruirlo leyendo las migraciones una por una. **Manten esto al dia
en el mismo PR que cambia el esquema**, no "despues".

## Plantilla del diagrama

```mermaid
erDiagram
    LEADS ||--o{ CALCULATOR_ESTIMATES : "realiza"
    LEADS ||--o{ BENCHMARK_RESPONSES : "responde"
    LEADS ||--o{ LEAD_ACCESS_TOKENS : "recibe"
    BENCHMARK_INSTRUMENTS ||--o{ BENCHMARK_DIMENSIONS : "contiene"
    BENCHMARK_DIMENSIONS ||--o{ BENCHMARK_QUESTIONS : "agrupa"
    BENCHMARK_QUESTIONS ||--o{ BENCHMARK_OPTIONS : "ofrece"
    BENCHMARK_RESPONSES ||--o{ BENCHMARK_ANSWERS : "registra"
    BENCHMARK_RESPONSES ||--|| PDF_JOBS : "dispara"
    BENCHMARK_RESPONSES ||--o{ PDF_DOCUMENTS : "produce"
    OUTREACH_CAMPAIGNS ||--o{ OUTREACH_CONTACTS : "incluye"
```
