package com.dcplatform.api.pdf;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Ajustes del modulo pdf, bajo app.pdf en application.yml.
 */
@ConfigurationProperties(prefix = "app.pdf")
public class PdfProperties {

    /** Version de plantilla con la que se genera. Se guarda por documento. */
    private String templateVersion = "1.0.0";

    /** Dias que el documento sigue disponible para descarga. */
    private int signedUrlTtlDays = 7;

    /** Reintentos antes de dar un job por FAILED (PdfGeneratorArchitecture.md). */
    private int maxAttempts = 3;

    /** El worker solo consume la cola en el perfil que lo habilita. */
    private boolean workerEnabled = true;

    public String getTemplateVersion() { return templateVersion; }
    public void setTemplateVersion(String templateVersion) { this.templateVersion = templateVersion; }

    public int getSignedUrlTtlDays() { return signedUrlTtlDays; }
    public void setSignedUrlTtlDays(int signedUrlTtlDays) { this.signedUrlTtlDays = signedUrlTtlDays; }

    public int getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }

    public boolean isWorkerEnabled() { return workerEnabled; }
    public void setWorkerEnabled(boolean workerEnabled) { this.workerEnabled = workerEnabled; }
}
