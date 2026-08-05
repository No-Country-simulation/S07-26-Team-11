package com.dcplatform.api.documents;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
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

    @PostMapping
    @Operation(summary = "Genera un PDF institucional y lo guarda en el bucket",
            description = """
                    Renderiza la plantilla con los datos recibidos, sube el PDF al object storage
                    privado y registra sus metadatos. El dueno es el usuario del token. Regenerar
                    con el mismo `metadata.name` **reemplaza** el documento anterior.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "PDF generado y almacenado"),
            @ApiResponse(responseCode = "400", description = "Faltan campos o el nombre tiene caracteres no permitidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente, invalido o revocado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "503", description = "El object storage no esta configurado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
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
    @ApiResponse(responseCode = "200", description = "HTML de la plantilla ya renderizado")
    public String preview(@AuthenticationPrincipal String email,
                          @Valid @RequestBody DocumentRequest request) {
        return documents.previewHtml(email, request);
    }

    @GetMapping
    @Operation(summary = "Lista los documentos del usuario autenticado",
            description = "Del mas reciente al mas antiguo. Sin documentos devuelve `count: 0`, no 404.")
    @ApiResponse(responseCode = "200", description = "Listado del usuario")
    public OwnerDocuments listMine(@AuthenticationPrincipal String email) {
        return documents.listMine(email);
    }

    @GetMapping("/{name}")
    @Operation(summary = "Metadatos de un documento propio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Metadatos del documento"),
            @ApiResponse(responseCode = "404", description = "El documento no existe en la cuenta",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public DocumentSummary getOne(@AuthenticationPrincipal String email,
                                  @Parameter(description = "Nombre del documento, sin .pdf", example = "informe-julio")
                                  @PathVariable String name) {
        return documents.getMine(email, name);
    }

    @GetMapping("/{name}/download")
    @Operation(summary = "Descarga el PDF",
            description = """
                    Responde `302` hacia una URL firmada y temporal del object storage. El binario
                    no pasa por la API. Con curl hay que seguir el redirect: `curl -L`.""")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redireccion a la URL firmada"),
            @ApiResponse(responseCode = "404", description = "El documento no existe en la cuenta",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "503", description = "El object storage no esta configurado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ResponseEntity<Void> download(@AuthenticationPrincipal String email,
                                         @Parameter(description = "Nombre del documento, sin .pdf",
                                                 example = "informe-julio")
                                         @PathVariable String name) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(documents.downloadUrl(email, name))
                .build();
    }
}
