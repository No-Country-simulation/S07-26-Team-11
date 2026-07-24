# Arquitectura

**Responsable:** Andrés Segura — Solution Architect

Los diagramas están en Mermaid, en texto plano, versionados junto al código. Se renderizan solos en GitHub.

**Regla:** cada cambio de arquitectura se hace en el mismo PR que cambia el diagrama, y viene acompañado de un ADR.

## Versiones planeadas

| Versión | Nivel C4 | Qué responde | Semana | Estado |
|---|---|---|---|---|
| A0 | C1 Contexto | Quiénes usan el sistema y con qué sistemas externos habla | 0 | ☑ |
| A1 | C2 Contenedores | Qué unidades desplegables hay y cómo se comunican | 0 | ☑ |
| A2 | C3 Componentes de la API | Qué módulos internos tiene el backend | 0 | ☑ |
| A3 | Despliegue | Qué corre en qué proveedor | 0 | ☐ |
| A4 | Secuencia | Flujo calculadora → benchmark → PDF → correo | 1 | ☐ |
| A5 | C3 del PDF Generator | Cola, worker, template, storage | 3 | ☐ |
