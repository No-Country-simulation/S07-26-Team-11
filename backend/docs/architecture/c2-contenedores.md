# A1 — Diagrama de contenedores (C4 nivel 2)

**Versión:** A1 · **Fecha:** Semana 0 · **Responde:** qué unidades desplegables hay y cómo se comunican.

```mermaid
graph TB
    subgraph cliente["Navegador"]
        WEB["<b>web</b><br/>Next.js<br/>Reporte, calculadora, benchmark<br/>y dashboard interno"]
    end

    subgraph plataforma["Plataforma"]
        API["<b>api</b><br/>Spring Boot / Java 21<br/>REST + OpenAPI<br/>Módulos: leads, calculator,<br/>benchmark, pdf, outreach"]
        WORKER["<b>worker</b><br/>Mismo artefacto, perfil distinto<br/>Consume la cola de trabajos:<br/>PDF y envío de correos"]
        DB[("<b>postgres</b><br/>PostgreSQL 17 · Supabase<br/>Datos + cola de trabajos")]
    end

    STORE[("<b>object storage</b><br/>PDFs generados")]
    MAIL["<b>proveedor de email</b><br/>Transaccional + campañas"]

    WEB -->|"HTTPS / JSON<br/>/api/v1"| API
    API -->|"JDBC"| DB
    WORKER -->|"JDBC · FOR UPDATE SKIP LOCKED"| DB
    WORKER -->|"HTTPS"| STORE
    WORKER -->|"HTTPS / API"| MAIL
    MAIL -->|"Webhooks firmados"| API
    API -->|"URL firmada"| STORE
```

## Decisiones incorporadas

- **`api` y `worker` son el mismo artefacto JAR** desplegado dos veces con perfiles de Spring distintos. Comparten el modelo de dominio y se separan de verdad el día que haga falta.
- **La cola vive en PostgreSQL**, con `SELECT ... FOR UPDATE SKIP LOCKED`. Sin Redis: menos infraestructura, comportamiento correcto con varios workers.
- **PostgreSQL es gestionado por Supabase**, no un contenedor propio. `api` y `worker` se conectan por el *session pooler* — ver [ADR-0004](adr/0004-supabase-postgres.md). El `postgres` de `docker-compose.yml` es solo para desarrollo local.
- **Nada bloqueante en el request HTTP.** Generar el PDF y enviar correos son trabajos asíncronos.
- **El dashboard interno vive dentro de `web`**, en rutas protegidas. No es un despliegue aparte.
