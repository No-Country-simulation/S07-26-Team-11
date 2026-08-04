package com.dcplatform.api.pdf;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.dcplatform.api.shared.ApiException;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/jobs/{jobId}")
    @Operation(summary = "Estado del trabajo de generación",
            description = "El frontend consulta este endpoint cada 2 segundos mientras el estado sea PENDING o PROCESSING.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actual del trabajo"),
            @ApiResponse(responseCode = "400", description = "Formato de ID de trabajo inválido o ausente"),
            @ApiResponse(responseCode = "404", description = "El trabajo no existe")
    })
    public ResponseEntity<PdfService.PdfJobStatus> getJobStatus(
            @PathVariable @NotNull(message = "El jobId es obligatorio") UUID jobId) {
        return pdfService.getJobStatus(jobId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ApiException.notFound("El trabajo solicitado no existe"));
    }

    @GetMapping("/documents/{documentId}/download")
    @Operation(summary = "Descarga del informe",
            description = "Registra la descarga y redirige con 302 a una URL firmada y temporal del object storage.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirección a la URL firmada"),
            @ApiResponse(responseCode = "400", description = "Formato de ID de documento inválido o ausente"),
            @ApiResponse(responseCode = "404", description = "El documento no existe"),
            @ApiResponse(responseCode = "422", description = "El enlace venció"),
            @ApiResponse(responseCode = "503", description = "El object storage no está configurado")
    })
    public ResponseEntity<Void> downloadDocument(
            @PathVariable @NotNull(message = "El documentId es obligatorio") UUID documentId) {
        URI signedUrl = pdfService.resolveDownload(documentId);
        return ResponseEntity.status(HttpStatus.FOUND).location(signedUrl).build();
    }
}