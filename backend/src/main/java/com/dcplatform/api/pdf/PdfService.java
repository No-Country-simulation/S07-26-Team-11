package com.dcplatform.api.pdf;

import com.dcplatform.api.pdf.storage.PdfStorage;
import com.dcplatform.api.shared.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Operaciones de consulta y descarga del modulo pdf.
 *
 * La generacion en si la dispara el worker; aca solo vive lo que necesita la
 * API publica mas el render, que el worker reutiliza.
 */
@Service
public class PdfService {

    private static final String DOWNLOAD_PATH = "/api/v1/public/pdf/documents/%s/download";

    private final SpringTemplateEngine templateEngine;
    private final PdfRendererComponent pdfRendererComponent;
    private final PdfJobRepository pdfJobRepository;
    private final PdfDocumentRepository pdfDocumentRepository;
    private final PdfStorage pdfStorage;
    private final PdfProperties properties;

    public PdfService(SpringTemplateEngine templateEngine,
                      PdfRendererComponent pdfRendererComponent,
                      PdfJobRepository pdfJobRepository,
                      PdfDocumentRepository pdfDocumentRepository,
                      PdfStorage pdfStorage,
                      PdfProperties properties) {
        this.templateEngine = templateEngine;
        this.pdfRendererComponent = pdfRendererComponent;
        this.pdfJobRepository = pdfJobRepository;
        this.pdfDocumentRepository = pdfDocumentRepository;
        this.pdfStorage = pdfStorage;
        this.properties = properties;
    }

    /**
     * Estado del trabajo para el polling del frontend. Cuando ya termino, agrega
     * los datos del documento asociado, que se resuelve por responseId.
     */
    @Transactional(readOnly = true)
    public Optional<PdfJobStatus> getJobStatus(UUID jobId) {
        return pdfJobRepository.findById(jobId).map(job -> {
            PdfDocument document = job.getStatus() == PdfJob.JobStatus.DONE
                    ? pdfDocumentRepository.findByResponseId(job.getResponseId()).orElse(null)
                    : null;

            UUID documentId = document != null ? document.getId() : null;
            String downloadUrl = documentId != null ? DOWNLOAD_PATH.formatted(documentId) : null;
            OffsetDateTime expiresAt = document != null
                    ? document.getGeneratedAt().plusDays(properties.getSignedUrlTtlDays())
                    : null;

            return new PdfJobStatus(
                    job.getId(),
                    job.getStatus().name(),
                    job.getAttempts(),
                    documentId,
                    downloadUrl,
                    expiresAt,
                    job.getFailureReason());
        });
    }

    /**
     * Resuelve la descarga: valida que el documento exista y no haya vencido,
     * registra el acceso y devuelve la URL firmada a la que redirigir.
     */
    @Transactional
    public URI resolveDownload(UUID documentId) {
        PdfDocument document = pdfDocumentRepository.findById(documentId)
                .orElseThrow(() -> ApiException.notFound("El documento solicitado no existe"));

        OffsetDateTime expiresAt = document.getGeneratedAt().plusDays(properties.getSignedUrlTtlDays());
        if (expiresAt.isBefore(OffsetDateTime.now())) {
            throw ApiException.businessRule("El enlace de descarga vencio. Solicita que se regenere el informe.");
        }

        URI signedUrl = pdfStorage.createSignedDownloadUrl(
                document.getStorageKey(), "informe-benchmark.pdf");

        document.registerDownload();
        return signedUrl;
    }

    /**
     * HTML de la plantilla -> PDF. Lo usa el worker dentro de su propia
     * transaccion; no se expone por HTTP porque renderizar toma segundos.
     */
    public byte[] renderReport(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        // Sin prefijo "templates/": el resolver de Spring Boot ya apunta a
        // classpath:/templates/, agregarlo de nuevo busca templates/templates/.
        String html = templateEngine.process(templateName, context);

        try {
            return pdfRendererComponent.renderHtmlToPdf(html);
        } catch (IOException ex) {
            throw new IllegalStateException("Fallo al renderizar el PDF del informe", ex);
        }
    }

    /** Proyeccion de lectura que consume el controller. */
    public record PdfJobStatus(
            UUID jobId,
            String status,
            int attempts,
            UUID documentId,
            String downloadUrl,
            OffsetDateTime expiresAt,
            String failureReason) {
    }
}