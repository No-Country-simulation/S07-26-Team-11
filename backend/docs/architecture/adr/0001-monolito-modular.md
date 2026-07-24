# ADR-0001 — Monolito modular en lugar de microservicios

**Estado:** Aceptado
**Fecha:** Semana 0
**Autor:** Solution Architect
**Afecta a:** todo el backend

## Contexto

Cinco entregables, cinco semanas, ocho personas con stacks heterogéneos, presupuesto cero y una sola persona con Java/Spring como fortaleza declarada. El sistema debe quedar desplegado en un ambiente de staging gratuito, donde cada servicio adicional consume cuota.

## Opciones consideradas

| Opción | Pros | Contras |
|---|---|---|
| Microservicios | Fronteras físicas, despliegue independiente | Multiplica la infraestructura, el costo de operación y el tiempo de coordinación. Inviable en capa gratuita |
| Monolito modular | Un despliegue, una base de datos, fronteras lógicas estrictas | Requiere disciplina para que los módulos no se acoplen |
| Monolito sin módulos | Lo más rápido de arrancar | Ocho personas sobre el mismo código sin fronteras se convierte en conflictos permanentes |

## Decisión

**Monolito modular.** Un solo artefacto desplegable con paquetes independientes por dominio (`leads`, `calculator`, `benchmark`, `pdf`, `outreach`, `shared`). Ningún módulo importa clases internas de otro; se comunican por interfaces públicas y eventos de aplicación.

## Consecuencias

**Positivas:** un solo despliegue y una sola base de datos, cabe en la capa gratuita; onboarding simple; el `worker` es el mismo JAR con otro perfil.

**Negativas:** las fronteras dependen de la disciplina del equipo y de la revisión de PRs, no del compilador. Un módulo pesado afecta el arranque de todo.

**Reversión:** barata si las fronteras se respetan. Extraer un módulo a su propio servicio es un trabajo de días, no de semanas — justamente por eso las fronteras importan.
