package com.dcplatform.api.pdf;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class PdfService {

    private final SpringTemplateEngine templateEngine;
    private final PdfRendererComponent pdfRendererComponent;

    public PdfService(SpringTemplateEngine templateEngine, PdfRendererComponent pdfRendererComponent) {
        this.templateEngine = templateEngine;
        this.pdfRendererComponent = pdfRendererComponent;
    }

    @Async
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