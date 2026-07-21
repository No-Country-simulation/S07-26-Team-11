# templates/

Plantillas Thymeleaf del PDF y de los correos.

- `pdf/` — plantilla del documento institucional. La mantienen el Frontend Dev (maquetación) y la Diseñadora (diseño).
- `email/` — plantillas de los correos: magic link, entrega del PDF, invitacion al benchmark.

**Restricciones del PDF:** sin flexbox, sin grid, sin JavaScript. Layout con tablas y bloques.
Ver `../java/com/dcplatform/api/pdf/PdfGeneratorArchitecture.md`.
