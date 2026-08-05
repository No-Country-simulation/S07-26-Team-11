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
            job.markProcessing();
            pdfJobRepository.save(job);

            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("responseId", job.getResponseId());

            byte[] pdfBytes = pdfService.renderReport("report-template", templateVariables);

            String storageKey = "reports/" + job.getResponseId() + "/informe-benchmark.pdf";

            pdfStorage.upload(storageKey, pdfBytes);

            PdfDocument document = new PdfDocument(
                    job.getResponseId(),
                    storageKey,
                    pdfProperties.getTemplateVersion(),
                    (long) pdfBytes.length,
                    1
            );
            pdfDocumentRepository.save(document);

            job.markDone();
            pdfJobRepository.save(job);
            log.info("Trabajo de PDF ID: {} completado exitosamente.", job.getId());

        } catch (Exception e) {
            log.error("Error al procesar el trabajo de PDF ID {}: {}", job.getId(), e.getMessage(), e);
            job.markFailed(e.getMessage(), pdfProperties.getMaxAttempts());
            pdfJobRepository.save(job);
        }
    }
}