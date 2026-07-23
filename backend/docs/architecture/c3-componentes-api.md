# A2 — Componentes del backend (C4 nivel 3)

**Versión:** A2 · **Fecha:** Semana 0 · **Responde:** qué módulos hay dentro de `api` y quién es responsable de cada uno.

```mermaid
graph TB
    subgraph borde["Borde / API Gateway"]
        GW["CORS · Rate limit · Validación<br/>Manejo global de errores<br/>OpenAPI · Seguridad"]
    end

    subgraph modulos["Módulos de dominio"]
        LEADS["<b>leads</b><br/>Captura de email,<br/>magic links, consentimiento"]
        CALC["<b>calculator</b><br/>Motor de fórmulas,<br/>KPIs, versionado"]
        BENCH["<b>benchmark</b><br/>Instrumento, scoring,<br/>percentiles de cohorte"]
        PDFM["<b>pdf</b><br/>Encolado, plantilla,<br/>render, storage"]
        OUT["<b>outreach</b><br/>Importación, envío,<br/>tracking, supresión"]
    end

    SHARED["<b>shared</b><br/>Errores, auditoría, cola,<br/>configuración, utilidades"]
    DB[("PostgreSQL")]

    GW --> LEADS & CALC & BENCH & PDFM & OUT
    BENCH -.->|"evento benchmark.completed"| PDFM
    CALC -.->|"KPIs del lead"| PDFM
    LEADS -.->|"lead identificado"| OUT
    LEADS & CALC & BENCH & PDFM & OUT --> SHARED --> DB
```

## Fronteras entre módulos

**Regla dura:** ningún módulo importa clases internas de otro. Se comunican por interfaces públicas o por eventos de aplicación. Esto es lo que permite que varias personas trabajen en paralelo sin pisarse, y lo que permite reemplazar un módulo entero si hace falta.

| Módulo | Responsable | Depende de |
|---|---|---|
| `leads` | [Nombre] | `shared` |
| `calculator` | [Nombre] | `shared` |
| `benchmark` | [Nombre] | `shared` |
| `pdf` | [Nombre] | `shared`, eventos de `benchmark` y `calculator` |
| `outreach` | [Nombre] | `shared`, eventos de `leads` |
