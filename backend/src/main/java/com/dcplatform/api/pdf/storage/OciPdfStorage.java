package com.dcplatform.api.pdf.storage;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Implementacion sobre Oracle Cloud Object Storage.
 */
public class OciPdfStorage implements PdfStorage {

    private static final Logger log = LoggerFactory.getLogger(OciPdfStorage.class);
    private static final String CONTENT_TYPE = "application/pdf";

    private final ObjectStorage client;
    private final PdfStorageProperties properties;

    /**
     * Namespace ya resuelto: viene de configuracion o del GetNamespace que hace
     * PdfStorageConfig al arrancar. No se vuelve a leer de properties para que
     * no queden dos fuentes de verdad.
     */
    private final String namespace;

    public OciPdfStorage(ObjectStorage client, PdfStorageProperties properties, String namespace) {
        this.client = client;
        this.properties = properties;
        this.namespace = namespace;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String storageKeyFor(UUID responseId) {
        return properties.getKeyPrefix() + responseId + ".pdf";
    }

    @Override
    public String upload(String storageKey, byte[] content) {
        PutObjectRequest request = PutObjectRequest.builder()
                .namespaceName(namespace)
                .bucketName(properties.getBucketName())
                .objectName(storageKey)
                .contentType(CONTENT_TYPE)
                .contentLength((long) content.length)
                .putObjectBody(new ByteArrayInputStream(content))
                .build();

        client.putObject(request);
        log.info("PDF subido al bucket: key={} bytes={}", storageKey, content.length);
        return storageKey;
    }

    @Override
    public URI createSignedDownloadUrl(String storageKey, String fileName) {
        Instant expiry = Instant.now().plus(properties.getSignedUrlTtl());

        // El PAR se crea por descarga y vive pocos minutos. Alternativa descartada:
        // crear uno de 7 dias al generar el PDF, que obligaria a guardarlo en la
        // base (no hay columna para eso) y dejaria un enlace valido circulando.
        CreatePreauthenticatedRequestDetails details = CreatePreauthenticatedRequestDetails.builder()
                .name("pdf-download-" + UUID.randomUUID())
                .objectName(storageKey)
                .accessType(CreatePreauthenticatedRequestDetails.AccessType.ObjectRead)
                .timeExpires(Date.from(expiry))
                .build();

        CreatePreauthenticatedRequestRequest request = CreatePreauthenticatedRequestRequest.builder()
                .namespaceName(namespace)
                .bucketName(properties.getBucketName())
                .createPreauthenticatedRequestDetails(details)
                .build();

        CreatePreauthenticatedRequestResponse response = client.createPreauthenticatedRequest(request);
        String fullPath = response.getPreauthenticatedRequest().getFullPath();

        log.debug("PAR creado para key={} con vigencia {}", storageKey, properties.getSignedUrlTtl());
        return URI.create(fullPath);
    }
}
