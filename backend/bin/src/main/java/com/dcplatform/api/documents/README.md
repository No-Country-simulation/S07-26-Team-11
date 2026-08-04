# Módulo `documents`

**Estado:** implementado y funcional.

Documentos PDF institucionales que genera **el usuario autenticado** bajo demanda desde
`/api/v1/documents`. Adaptado del proyecto `backend/api-demo-pdf`.

## Qué NO es

No es el informe del benchmark. Ese vive en el módulo [`pdf`](../pdf/README.md): lo genera un
worker asíncrono a partir de la cola `pdf_jobs`, cuelga de `benchmark_responses` y se entrega
por polling. Son dos flujos distintos que comparten el mismo bucket, cada uno con su prefijo.

| | `documents` (este módulo) | `pdf` |
|---|---|---|
| Disparo | El usuario, síncrono | Worker, asíncrono |
| Dueño | `users` | `benchmark_responses` |
| Tabla | `user_documents` | `pdf_documents` / `pdf_jobs` |
| Prefijo en el bucket | `documents/` | `benchmark-reports/` |

## Cadena de render

```
DocumentRequest -> modelo -> Thymeleaf -> HTML -> jsoup (XHTML) -> Open HTML to PDF -> bytes
                                                                          |
                                          bucket privado (OCI) + fila en user_documents
```

El paso por jsoup no es opcional: el motor consume XML bien formado y Thymeleaf produce HTML5.

## Piezas

| Clase | Responsabilidad |
|---|---|
| `DocumentController` | Endpoints del usuario (crear, listar, consultar, descargar, preview) |
| `AdminDocumentController` | `GET /api/v1/admin/documents`: inventario de todos, solo ADMIN |
| `DocumentService` | Orquesta la cadena. **Acota todo al usuario del token** |
| `InstitutionalPdfRenderer` | Thymeleaf → HTML → PDF, con fuentes y SVG |
| `DocumentAssets` | Logo (data URI) y fuentes, desde `classpath:assets/` |
| `DocumentDateFormatter` | Normaliza `date` a texto largo en español |
| `UserDocument` / `UserDocumentRepository` | Metadatos en `user_documents` |
| `DocumentProperties` | Configuración bajo `app.documents` |

## Reglas de seguridad

1. **El dueño sale del token, nunca del cuerpo ni de la ruta.** En el demo del que se adaptó,
   `metadata.owner` viajaba en el request y cualquiera podía escribir o leer en la carpeta de
   cualquiera.
2. Un documento de otro usuario responde **404**, no 403: no se filtra si existe.
3. `metadata.name` termina siendo parte de la clave del objeto, así que pasa por una lista
   blanca (`[A-Za-z0-9][A-Za-z0-9._-]{0,63}`) antes de tocar el storage.
4. La clave del objeto usa el **id** del usuario, no su email: sin datos personales en el
   bucket, estable si el email cambia.
5. La descarga responde `302` a una URL firmada de vida corta. El binario no pasa por la API y
   el enlace permanente es el endpoint, no el del storage.

## Dependencia entre módulos

Este módulo importa `pdf.storage.PdfStorage` (interfaz pública) y `auth.User`/`UserRepository`.
`PdfStorage` es infraestructura compartida por dos módulos: cuando haya un tercero, conviene
moverla a un paquete propio de infraestructura en vez de dejarla colgando de `pdf`.

## Plantilla

`src/main/resources/templates/pdf/institutional-document.html`.
Guía para diseñarla: `backend/document_design/INSTRUCCIONES.md` (fuera del repo del equipo).
