package com.dcplatform.api.pdf;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/pdf")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<PdfJobResponse> getJobStatus(@PathVariable Long jobId) {
        return pdfService.getJobById(jobId)
                .map(job -> {
                    UUID docUuid = null;
                    String downloadUrl = null;
                    
                    if (job.getStatus() == PdfJob.JobStatus.DONE) {
                        var docOpt = pdfService.getDocumentByResponseId(job.getResponseId());
                        if (docOpt.isPresent()) {
                            PdfDocument doc = docOpt.get();
                            docUuid = doc.getDocumentId();
                            downloadUrl = "/api/v1/public/pdf/documents/" + docUuid + "/download";
                        }
                    }

                    OffsetDateTime expiresAt = job.getUpdatedAt() != null 
                            ? job.getUpdatedAt().atOffset(ZoneOffset.UTC).plusDays(7) 
                            : OffsetDateTime.now(ZoneOffset.UTC).plusDays(7);

                    var response = new PdfJobResponse(
                            job.getId(),
                            job.getStatus().name(),
                            job.getAttempts(),
                            docUuid,
                            downloadUrl,
                            expiresAt,
                            job.getErrorMessage()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<Void> downloadDocument(@PathVariable UUID documentId) {
        return pdfService.getDocumentByUuid(documentId)
                .map(doc -> {
                    String signedUrl = "https://storage.example.com/" + doc.getStorageKey() + "?sig=mock";
                    return ResponseEntity.status(302)
                            .location(URI.create(signedUrl))
                            .<Void>build();
                })
                .orElse(ResponseEntity.notFound().<Void>build());
    }

    public record PdfJobResponse(
            Long jobId,
            String status,
            int attempts,
            UUID documentId,
            String downloadUrl,
            OffsetDateTime expiresAt,
            String failureReason
    ) {}
}