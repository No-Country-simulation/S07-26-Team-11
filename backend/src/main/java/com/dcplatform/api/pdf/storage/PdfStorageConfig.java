package com.dcplatform.api.pdf.storage;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.function.Supplier;

/**
 * Arma el cliente de Object Storage a partir de las variables de entorno.
 *
 * Regla que ordena todo este archivo: la falta de credenciales nunca puede
 * impedir que la aplicacion arranque. Si algo falta o esta mal, se registra
 * UnconfiguredPdfStorage y queda un WARN en el log.
 */
@Configuration
@EnableConfigurationProperties(PdfStorageProperties.class)
public class PdfStorageConfig {

    private static final Logger log = LoggerFactory.getLogger(PdfStorageConfig.class);

    @Bean
    public PdfStorage pdfStorage(PdfStorageProperties properties) {
        if (!properties.isConfigured()) {
            String missing = properties.missingSettings();
            log.warn("Object Storage sin configurar; la generacion y descarga de PDF quedan deshabilitadas. "
                    + "Variables faltantes: {}", missing);
            return new UnconfiguredPdfStorage(missing);
        }

        try {
            ObjectStorage client = buildClient(properties);
            String namespace = resolveNamespace(client, properties);
            log.info("Object Storage conectado: bucket={} namespace={} region={}",
                    properties.getBucketName(), namespace, properties.getRegion());
            return new OciPdfStorage(client, properties, namespace);
        } catch (Exception ex) {
            // Credenciales presentes pero invalidas (PEM ilegible, region inexistente...).
            // Se degrada igual que si faltaran, para no romper el arranque en Render.
            log.error("No se pudo inicializar el cliente de Object Storage: {}", ex.getMessage());
            return new UnconfiguredPdfStorage("revisar credenciales de OCI: " + ex.getMessage());
        }
    }

    /**
     * El namespace es el unico dato que se puede deducir de las credenciales:
     * GetNamespace devuelve el de la tenancy con la que uno se autentico. Si
     * OCI_NAMESPACE viene configurado se respeta y no se hace la llamada.
     *
     * Cuesta un request al arrancar. Vale la pena por ser una variable menos
     * que mantener sincronizada entre .env y Render, pero conviene fijarla en
     * produccion para que el arranque no dependa de la red.
     */
    private String resolveNamespace(ObjectStorage client, PdfStorageProperties properties) {
        String configured = properties.getNamespace();
        if (configured != null && !configured.isBlank()) {
            return configured;
        }

        String discovered = client.getNamespace(GetNamespaceRequest.builder().build()).getValue();
        log.info("OCI_NAMESPACE no configurado; descubierto desde la tenancy: {}", discovered);
        return discovered;
    }

    private ObjectStorage buildClient(PdfStorageProperties properties) {
        Region region = Region.fromRegionId(properties.getRegion());

        SimpleAuthenticationDetailsProvider.SimpleAuthenticationDetailsProviderBuilder authBuilder =
                SimpleAuthenticationDetailsProvider.builder()
                        .tenantId(properties.getTenancyOcid())
                        .userId(properties.getUserOcid())
                        .fingerprint(properties.getFingerprint())
                        .region(region)
                        .privateKeySupplier(privateKeySupplier(properties));

        if (properties.getPrivateKeyPassphrase() != null && !properties.getPrivateKeyPassphrase().isBlank()) {
            authBuilder.passPhrase(properties.getPrivateKeyPassphrase());
        }

        return ObjectStorageClient.builder()
                .region(region)
                .build(authBuilder.build());
    }

    /**
     * La clave privada de API puede venir como archivo (comodo en local) o como
     * PEM en base64 en una sola linea (unica forma practica en Render, que no
     * tiene disco persistente). Si estan las dos, gana el archivo.
     */
    private Supplier<InputStream> privateKeySupplier(PdfStorageProperties properties) {
        String path = properties.getPrivateKeyPath();
        if (path != null && !path.isBlank()) {
            Path pemPath = Path.of(path);
            if (!Files.isReadable(pemPath)) {
                throw new IllegalStateException("No se puede leer la clave privada en " + path);
            }
            return () -> {
                try {
                    return Files.newInputStream(pemPath);
                } catch (IOException ex) {
                    throw new IllegalStateException("Fallo al leer la clave privada en " + path, ex);
                }
            };
        }

        byte[] pem;
        try {
            pem = Base64.getDecoder().decode(properties.getPrivateKeyBase64().replaceAll("\\s", ""));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("OCI_PRIVATE_KEY_BASE64 no es base64 valido", ex);
        }
        return () -> new ByteArrayInputStream(pem);
    }
}
