package com.dcplatform.api.pdf;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.dcplatform.api.shared.ApiException;
import com.dcplatform.api.shared.annotations.ApiJsonExample;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

/**
 * Endpoints públicos del módulo pdf. Contrato en docs/API.md sección 4.
 */
@RestController
@RequestMapping("/api/v1/public/pdf")
@Tag(name = "PDF", description = "Estado de la generación y descarga del informe")
@Validated
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping(value = "/jobs/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Estado del trabajo de generación",
            description = "El frontend consulta este endpoint cada 2 segundos mientras el estado sea PENDING o PROCESSING.")
    @ApiJsonExample(
            description = "Estado actual del trabajo",
            path = "/static/swagger/examples/pdf/job-status-200-success.json")
    @ApiJsonExample(
            status = "400",
            description = "ID de trabajo inválido o ausente",
            path = "/static/swagger/examples/pdf/job-status-400-invalid-id-format.json")
    @ApiJsonExample(
            status = "400",
            description = "ID de trabajo nulo",
            path = "/static/swagger/examples/pdf/job-status-400-null-id.json")
    @ApiJsonExample(
            status = "401",
            description = "No autenticado",
            path = "/static/swagger/examples/pdf/auth-401-unauthorized.json")
    @ApiJsonExample(
            status = "403",
            description = "Sin permisos",
            path = "/static/swagger/examples/pdf/auth-403-forbidden.json")
    @ApiJsonExample(
            status = "404",
            description = "Trabajo no encontrado",
            path = "/static/swagger/examples/pdf/job-status-404-not-found.json")
    public ResponseEntity<PdfService.PdfJobStatus> getJobStatus(
            @PathVariable @NotNull(message = "El jobId es obligatorio") UUID jobId) {
        return pdfService.getJobStatus(jobId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ApiException.notFound("El trabajo solicitado no existe"));
    }

    @GetMapping(value = "/documents/{documentId}/download", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Descarga del informe",
            description = "Registra la descarga y redirige con 302 a una URL firmada y temporal del object storage.")
    @ApiJsonExample(
            status = "302",
            description = "Redirección a URL firmada",
            path = "/static/swagger/examples/pdf/document-download-302-redirect.json")
    @ApiJsonExample(
            status = "400",
            description = "ID de documento inválido",
            path = "/static/swagger/examples/pdf/document-download-400-invalid-id-format.json")
    @ApiJsonExample(
            status = "401",
            description = "No autenticado",
            path = "/static/swagger/examples/pdf/auth-401-unauthorized.json")
    @ApiJsonExample(
            status = "403",
            description = "Sin permisos",
            path = "/static/swagger/examples/pdf/auth-403-forbidden.json")
    @ApiJsonExample(
            status = "404",
            description = "Documento no encontrado",
            path = "/static/swagger/examples/pdf/document-download-404-not-found.json")
    @ApiJsonExample(
            status = "422",
            description = "Enlace de descarga vencido",
            path = "/static/swagger/examples/pdf/document-download-422-expired-link.json")
    @ApiJsonExample(
            status = "503",
            description = "Object storage no disponible",
            path = "/static/swagger/examples/pdf/document-download-503-storage-unavailable.json")
    public ResponseEntity<Void> downloadDocument(
            @PathVariable @NotNull(message = "El documentId es obligatorio") UUID documentId) {
        URI signedUrl = pdfService.resolveDownload(documentId);
        return ResponseEntity.status(HttpStatus.FOUND).location(signedUrl).build();
    }
}