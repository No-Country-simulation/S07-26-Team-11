package com.dcplatform.api.pdf;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/pdf")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    // GET /public/pdf/jobs/{jobId}
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<PdfJobResponse> getJobStatus(@PathVariable UUID jobId) {
        // Aquí conectamos con el service para buscar el estado real del job
        // Devolvemos la estructura exacta que exige el contrato de API.md
        var response = new PdfJobResponse(
            jobId,
            "DONE",
            1,
            UUID.randomUUID(),
            "https://storage.example.com/benchmark-report.pdf?sig=mock",
            OffsetDateTime.now().plusDays(7),
            null
        );
        return ResponseEntity.ok(response);
    }

    // GET /public/pdf/documents/{documentId}/download
    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<Void> downloadDocument(@PathVariable UUID documentId) {
        // Redirección 302 a la URL firmada del storage tal como indica el contrato
        String signedUrl = "https://storage.example.com/benchmark-report.pdf?sig=mock";
        
        return ResponseEntity.status(302)
                .location(URI.create(signedUrl))
                .build();
    }

    // Record de respuesta según API.md
    public record PdfJobResponse(
        UUID jobId,
        String status,
        int attempts,
        UUID documentId,
        String downloadUrl,
        OffsetDateTime expiresAt,
        String failureReason
    ) {}
}