# ADR-0002 — Generación de PDF con HTML + Open HTML to PDF

**Estado:** Aceptado
**Fecha:** Semana 0
**Autor:** Solution Architect
**Afecta a:** módulo `pdf`, diseño, frontend

## Contexto

El PDF debe leerse como documento institucional, con branding completo. La diseñadora trabaja en Figma y debe poder iterar sin depender de un desarrollador en cada ajuste. El backend es Java. El hosting gratuito tiene alrededor de 512 MB de RAM.

## Opciones consideradas

| Opción | Licencia | Pros | Contras |
|---|---|---|---|
| **Open HTML to PDF** (sobre PDFBox) | LGPL / Apache 2.0 | Java puro, liviano, hecho para reportes desde plantillas de servidor, soporta SVG y PDF accesible | Sin flexbox, sin grid, sin JavaScript. Requiere XHTML bien formado |
| iText 8 + pdfHTML | **AGPL** | El mejor soporte de CSS Paged Media en Java puro | AGPL: un producto comercial cerrado necesita licencia paga, del orden de miles de dólares al año |
| Playwright para Java (Chromium) | Apache 2.0 | Fidelidad CSS3 total, ejecuta JavaScript | Consume varias veces más CPU y RAM. No cabe en el tier gratuito junto con la JVM |
| Composición programática (PDFBox directo) | Apache 2.0 | Control total, muy liviano | El diseño se vuelve código: cada ajuste visual es una tarea de desarrollo |
| wkhtmltopdf | — | — | Archivado desde 2023 con vulnerabilidades sin parchar. Descartado por seguridad |

## Decisión

**Thymeleaf → HTML/CSS → Open HTML to PDF.**

## Consecuencias

**Positivas:** la diseñadora y el Frontend Dev iteran el template con las herramientas que ya dominan; licencia limpia; consume poca memoria; el mismo sistema de tokens de diseño sirve para la web y para el PDF.

**Negativas y su mitigación:** el motor **no soporta flexbox ni grid**. El layout se maqueta con tablas y bloques. Esta restricción hay que comunicársela a la diseñadora **antes** de que diseñe, y validarla con una prueba de concepto del template en la Semana 0. Es el riesgo principal de este entregable.

**Plan de contingencia:** si el diseño aprobado resulta imposible con este motor, se pasa a Playwright para Java y se mueve el worker de PDF a un servicio con más memoria. Documentar en un ADR nuevo.

**Reversión:** media. El template HTML se reutiliza casi entero; cambia el motor de render y el despliegue del worker.
