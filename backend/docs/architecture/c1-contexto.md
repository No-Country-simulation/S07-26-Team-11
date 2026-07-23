# A0 — Diagrama de contexto (C4 nivel 1)

**Versión:** A0 · **Fecha:** Semana 0 · **Responde:** quiénes usan el sistema y con qué habla.

```mermaid
graph TB
    OP["Operador de data center<br/><i>Usuario público</i>"]
    EQ["Equipo comercial de la startup<br/><i>Usuario interno</i>"]

    SYS["<b>Plataforma de Benchmark</b><br/>Reporte + calculadora + benchmark<br/>+ PDF automático + outreach"]

    MAIL["Proveedor de email<br/><i>Sistema externo</i>"]
    STORE["Object storage<br/><i>Sistema externo</i>"]
    SERIE["Otros proyectos de la serie<br/><i>Sistema externo</i>"]

    OP -->|"Calcula, responde el benchmark,<br/>recibe su PDF"| SYS
    EQ -->|"Consulta respuestas,<br/>dispara campañas"| SYS
    SYS -->|"Envía magic links,<br/>PDFs e invitaciones"| MAIL
    SYS -->|"Guarda los PDFs generados"| STORE
    SYS <-->|"Comparte datos agregados"| SERIE
    MAIL -->|"Eventos de entrega y apertura"| SYS
```

## Actores

| Actor | Qué hace | Cómo se autentica |
|---|---|---|
| Operador de data center | Usa la calculadora, deja su email, completa el benchmark, descarga su PDF | Magic link por correo. Sin contraseña |
| Equipo comercial | Consulta leads y respuestas, sube listas y lanza campañas | Usuario y contraseña, roles `ADMIN` y `VIEWER` |
| Fundador | Recibe los leads. No usa el sistema directamente | — |
