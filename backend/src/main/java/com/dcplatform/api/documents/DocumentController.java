package com.dcplatform.api.documents;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.dcplatform.api.shared.annotations.ApiJsonExample;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Documentos PDF institucionales del usuario autenticado.
 *
 * <p>Todos los endpoints exigen un token de acceso valido y operan <em>solo</em> sobre los
 * documentos de quien lo presenta: el dueno se toma del token, nunca de la ruta ni del cuerpo.
 * Un ADMIN que necesite ver los de todos usa {@code GET /api/v1/admin/documents}.
 *
 * <p>Los PDF viven en el bucket privado de object storage. La descarga no pasa por la API: se
 * responde {@code 302} hacia una URL firmada y de vida corta del storage.
 */
@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Documentos", description = "Genera, lista y descarga los PDF institucionales del usuario")
@SecurityRequirement(name = "bearer-jwt")
public class DocumentController {

    private final DocumentService documents;

    public DocumentController(DocumentService documents) {
        this.documents = documents;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Genera un PDF institucional y lo guarda en el bucket",
            description = """
                    Renderiza la plantilla con los datos recibidos, sube el PDF al object storage
                    privado y registra sus metadatos. El dueno es el usuario del token. Regenerar
                    con el mismo `metadata.name` **reemplaza** el documento anterior.""")
    @ApiJsonExample(
            status = "201",
            description = "PDF generado y almacenado",
            path = "/static/swagger/examples/documents/document-create-201-success.json")
    @ApiJsonExample(
            status = "400",
            description = "Error de validación de campos",
            path = "/static/swagger/examples/documents/document-create-400-validation-error.json")
    @ApiJsonExample(
            status = "401",
            description = "No autenticado",
            path = "/static/swagger/examples/documents/documents-401-unauthorized.json")
    @ApiJsonExample(
            status = "503",
            description = "Storage no disponible",
            path = "/static/swagger/examples/documents/document-create-503-storage-unavailable.json")
    public ResponseEntity<DocumentSummary> create(@AuthenticationPrincipal String email,
                                                  @Valid @RequestBody DocumentRequest request) {
        DocumentSummary created = documents.create(email, request);
        return ResponseEntity.created(URI.create(created.downloadUrl())).body(created);
    }

    @PostMapping(value = "/preview", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Devuelve el HTML intermedio, sin generar el PDF",
            description = """
                    Iteracion rapida del diseno: mismo modelo que el PDF, salida HTML para abrir en
                    el navegador. No sube nada al bucket. Recordar que el navegador es mas permisivo
                    que el motor de PDF: la paginacion y los limites de CSS solo se ven en el PDF.""")
    @ApiJsonExample(
            description = "Vista previa HTML renderizada",
            path = "/static/swagger/examples/documents/document-preview-200-success.json")
    @ApiJsonExample(
            status = "401",
            description = "No autenticado",
            path = "/static/swagger/examples/documents/documents-401-unauthorized.json")
    @ApiJsonExample(
            status = "400",
            description = "Error de validación de campos",
            path = "/static/swagger/examples/documents/document-create-400-validation-error.json")
    public String preview(@AuthenticationPrincipal String email,
                          @Valid @RequestBody DocumentRequest request) {
        return documents.previewHtml(email, request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lista los documentos del usuario autenticado",
            description = "Del mas reciente al mas antiguo. Sin documentos devuelve `count: 0`, no 404.")
    @ApiJsonExample(
            description = "Listado con documentos",
            path = "/static/swagger/examples/documents/documents-list-mine-200-success.json")
    @ApiJsonExample(
            description = "Listado vacío",
            path = "/static/swagger/examples/documents/documents-list-mine-200-empty.json")
    @ApiJsonExample(
            status = "401",
            description = "No autenticado",
            path = "/static/swagger/examples/documents/documents-401-unauthorized.json")
    public OwnerDocuments listMine(@AuthenticationPrincipal String email) {
        return documents.listMine(email);
    }

    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Metadatos de un documento propio")
    @ApiJsonExample(
            description = "Metadatos del documento",
            path = "/static/swagger/examples/documents/document-get-one-200-success.json")
    @ApiJsonExample(
            status = "401",
            description = "No autenticado",
            path = "/static/swagger/examples/documents/documents-401-unauthorized.json")
    @ApiJsonExample(
            status = "404",
            description = "Documento no encontrado",
            path = "/static/swagger/examples/documents/document-get-one-404-not-found.json")
    public DocumentSummary getOne(@AuthenticationPrincipal String email,
                                  @Parameter(description = "Nombre del documento, sin .pdf", example = "informe-julio")
                                  @PathVariable String name) {
        return documents.getMine(email, name);
    }

    @GetMapping(value = "/{name}/download", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Descarga el PDF",
            description = """
                    Responde `302` hacia una URL firmada y temporal del object storage. El binario
                    no pasa por la API. Con curl hay que seguir el redirect: `curl -L`.""")
    @ApiJsonExample(
            status = "302",
            description = "Redirección a URL firmada",
            path = "/static/swagger/examples/documents/document-download-302-redirect.json")
    @ApiJsonExample(
            status = "401",
            description = "No autenticado",
            path = "/static/swagger/examples/documents/documents-401-unauthorized.json")
    @ApiJsonExample(
            status = "404",
            description = "Documento no encontrado",
            path = "/static/swagger/examples/documents/document-get-one-404-not-found.json")
    @ApiJsonExample(
            status = "503",
            description = "Storage no disponible",
            path = "/static/swagger/examples/documents/document-download-503-storage-unavailable.json")
    public ResponseEntity<Void> download(@AuthenticationPrincipal String email,
                                         @Parameter(description = "Nombre del documento, sin .pdf",
                                                 example = "informe-julio")
                                         @PathVariable String name) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(documents.downloadUrl(email, name))
                .build();
    }
}
