package com.dcplatform.api.pdf;

import com.dcplatform.api.pdf.storage.PdfStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class PdfWorker {

    private static final Logger log = LoggerFactory.getLogger(PdfWorker.class);

    private final PdfJobRepository pdfJobRepository;
    private final PdfDocumentRepository pdfDocumentRepository;
    private final PdfService pdfService;
    private final PdfStorage pdfStorage;
    private final PdfProperties pdfProperties;

    public PdfWorker(PdfJobRepository pdfJobRepository,
                     PdfDocumentRepository pdfDocumentRepository,
                     PdfService pdfService,
                     PdfStorage pdfStorage,
                     PdfProperties pdfProperties) {
        this.pdfJobRepository = pdfJobRepository;
        this.pdfDocumentRepository = pdfDocumentRepository;
        this.pdfService = pdfService;
        this.pdfStorage = pdfStorage;
        this.pdfProperties = pdfProperties;
    }

    /**
     * Ejecuta el worker periódicamente (por ejemplo, cada 3 segundos) 
     * siempre y cuando esté habilitado en las propiedades (app.pdf.worker-enabled).
     */
    @Scheduled(fixedDelayString = "3000")
    @Transactional
    public void processPendingJobs() {
        if (!pdfProperties.isWorkerEnabled()) {
            return;
        }

        Optional<PdfJob> optionalJob = pdfJobRepository.findNextPendingJobForProcessing();
        if (optionalJob.isEmpty()) {
            return;
        }

        PdfJob job = optionalJob.get();
        log.info("Procesando trabajo de PDF ID: {} para responseId: {}", job.getId(), job.getResponseId());

        try {
            // 1. Marcar como en proceso
            job.markProcessing();
            pdfJobRepository.save(job);

            // 2. Preparar variables para la plantilla (acá se conectaría con los datos reales del benchmark/calculadora)
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("responseId", job.getResponseId());
            // TODO: Inyectar repositorio de benchmark para cargar métricas reales si hace falta

            // 3. Renderizar el reporte HTML a bytes de PDF
            // Asumimos una plantilla base llamada "report-template" en src/main/resources/templates/
            byte[] pdfBytes = pdfService.renderReport("report-template", templateVariables);

            // 4. Definir la clave de almacenamiento (storage key única basada en el responseId)
            String storageKey = "reports/" + job.getResponseId() + "/informe-benchmark.pdf";

            // 5. Subir al Storage (Bucket)
            pdfStorage.upload(storageKey, pdfBytes, "application/pdf");

            // 6. Registrar el documento generado en la base de datos
            PdfDocument document = new PdfDocument(
                    job.getResponseId(),
                    storageKey,
                    pdfProperties.getTemplateVersion(),
                    (long) pdfBytes.length,
                    1 // Conteo de páginas estimado o calculado
            );
            pdfDocumentRepository.save(document);

            // 7. Marcar el trabajo como finalizado con éxito
            job.markDone();
            pdfJobRepository.save(job);
            log.info("Trabajo de PDF ID: {} completado exitosamente.", job.getId());

        } catch (Exception e) {
            log.error("Error al procesar el trabajo de PDF ID {}: {}", job.getId(), e.getMessage(), e);
            // Marcar como fallido aplicando la política de reintentos máxima configurada
            job.markFailed(e.getMessage(), pdfProperties.getMaxAttempts());
            pdfJobRepository.save(job);
        }
    }
}