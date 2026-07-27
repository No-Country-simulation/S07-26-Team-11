package com.dcplatform.api.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class PdfService {

    private final SpringTemplateEngine templateEngine;
    private final PdfRendererComponent pdfRendererComponent;
    private final PdfJobRepository pdfJobRepository;
    private final PdfDocumentRepository pdfDocumentRepository;

    public PdfService(SpringTemplateEngine templateEngine, 
                      PdfRendererComponent pdfRendererComponent, 
                      PdfJobRepository pdfJobRepository, 
                      PdfDocumentRepository pdfDocumentRepository) {
        this.templateEngine = templateEngine;
        this.pdfRendererComponent = pdfRendererComponent;
        this.pdfJobRepository = pdfJobRepository;
        this.pdfDocumentRepository = pdfDocumentRepository;
    }

    @Transactional(readOnly = true)
    public Optional<PdfJob> getJobById(Long jobId) {
        return pdfJobRepository.findById(jobId);
    }

    @Transactional(readOnly = true)
    public Optional<PdfDocument> getDocumentByUuid(UUID documentId) {
        return pdfDocumentRepository.findByDocumentId(documentId);
    }

    @Async
    @Transactional
    public CompletableFuture<byte[]> generatePdfReportAsync(String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);

            String htmlContent = templateEngine.process("templates/" + templateName, context);
            byte[] pdfBytes = pdfRendererComponent.renderHtmlToPdf(htmlContent);

            return CompletableFuture.completedFuture(pdfBytes);
        } catch (IOException e) {
            throw new RuntimeException("Error crítico en la generación asíncrona del PDF", e);
        }
    }
}