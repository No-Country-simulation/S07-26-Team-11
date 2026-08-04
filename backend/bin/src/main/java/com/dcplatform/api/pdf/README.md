# Módulo `pdf`

**Responsable:** Equipo de Backend  
**Estado:** Implementado / Funcional  

Ver `docs/API.md` para el contrato de los endpoints de este módulo y `docs/architecture/c3-componentes-api.md` para sus fronteras.

**Regla:** este módulo no importa clases internas de otros módulos. Se comunica a través de interfaces públicas y de eventos de aplicación.

---

## 🏗️ Componentes Principales

1. **`PdfController`**: Expone los endpoints públicos de la API para consultar el estado del trabajo (`/jobs/{jobId}`) y la descarga segura (`/documents/{documentId}/download`).
2. **`PdfJob` y `PdfJobRepository`**: Cola de trabajos basada en base de datos con soporte para concurrencia segura mediante `FOR UPDATE SKIP LOCKED`.
3. **`PdfDocument` y `PdfDocumentRepository`**: Metadatos de los informes PDF generados y almacenados en el object storage.
4. **`PdfRendererComponent`**: Adaptador para transformar plantillas HTML en formato PDF utilizando **OpenHTMLtoPDF**.
5. **`PdfWorker`**: Tarea programada (`@Scheduled`) encargada de consumir la cola de pendientes, renderizar y subir los archivos de forma asíncrona.
6. **`PdfStorage`**: Capa de abstracción para la interacción con el Object Storage y la firma de URLs temporales.

---

## ⚙️ Configuración (`application.yml`)

Las propiedades bajo el prefijo `app.pdf`:
- `template-version`: Versión de la plantilla utilizada.
- `signed-url-ttl-days`: Validez en días para la descarga de los enlaces temporales.
- `max-attempts`: Reintentos máximos ante fallos antes de marcar el job como `FAILED`.
- `worker-enabled`: Permite habilitar o deshabilitar el consumo del worker por instancia.