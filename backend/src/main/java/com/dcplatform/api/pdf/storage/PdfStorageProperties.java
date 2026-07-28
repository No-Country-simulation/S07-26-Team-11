package com.dcplatform.api.pdf.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Credenciales y ubicacion del bucket de Oracle Cloud Object Storage.
 *
 * Todos los valores llegan por variables de entorno desde backend/.env
 * (ver .env.example). Ninguno tiene default con datos reales: si falta
 * cualquiera de los obligatorios, el modulo arranca deshabilitado y falla
 * con un mensaje claro recien cuando alguien intenta usarlo. Esto es a
 * proposito: el resto de la API tiene que poder levantar sin el bucket.
 */
@ConfigurationProperties(prefix = "app.storage.oci")
public class PdfStorageProperties {

    /** Nombre del bucket. Ej: bucket-mng-pdf-S07-26-11-data */
    private String bucketName;

    /**
     * Namespace de Object Storage de la tenancy (no es el nombre del bucket).
     * Opcional: si queda vacio se descubre al arrancar con GetNamespace, que
     * devuelve el namespace de la tenancy con la que uno se autentico.
     * Conviene fijarlo igual en produccion para ahorrarse esa llamada.
     */
    private String namespace;

    /** Region del bucket. Ej: us-ashburn-1, sa-saopaulo-1 */
    private String region;

    private String tenancyOcid;

    private String userOcid;

    /** Huella de la clave publica de API cargada en el usuario de OCI. */
    private String fingerprint;

    /**
     * Ruta al PEM de la clave privada de API. Comodo en local.
     * En Render conviene privateKeyBase64, que no necesita archivo en disco.
     */
    private String privateKeyPath;

    /** PEM de la clave privada codificado en base64, en una sola linea. */
    private String privateKeyBase64;

    /** Solo si la clave privada esta protegida con passphrase. */
    private String privateKeyPassphrase;

    /**
     * Vigencia de la URL firmada que se entrega en el redirect de descarga.
     * Corta a proposito: la URL viaja al navegador y el enlace permanente
     * es el endpoint de la API, no el del storage.
     */
    private Duration signedUrlTtl = Duration.ofMinutes(15);

    /** Prefijo de las claves de objeto dentro del bucket. */
    private String keyPrefix = "benchmark-reports/";

    /**
     * true solo si estan todos los datos necesarios para firmar contra OCI.
     * Lo consulta PdfStorageConfig para decidir que implementacion registrar.
     */
    public boolean isConfigured() {
        // namespace queda fuera a proposito: es el unico dato derivable de las
        // credenciales, asi que se descubre solo si no viene configurado.
        return hasText(bucketName)
                && hasText(region)
                && hasText(tenancyOcid)
                && hasText(userOcid)
                && hasText(fingerprint)
                && (hasText(privateKeyPath) || hasText(privateKeyBase64));
    }

    /** Nombres de las propiedades obligatorias que quedaron vacias. */
    public String missingSettings() {
        StringBuilder missing = new StringBuilder();
        appendIfBlank(missing, bucketName, "OCI_BUCKET_NAME");
        appendIfBlank(missing, region, "OCI_REGION");
        appendIfBlank(missing, tenancyOcid, "OCI_TENANCY_OCID");
        appendIfBlank(missing, userOcid, "OCI_USER_OCID");
        appendIfBlank(missing, fingerprint, "OCI_FINGERPRINT");
        if (!hasText(privateKeyPath) && !hasText(privateKeyBase64)) {
            append(missing, "OCI_PRIVATE_KEY_PATH u OCI_PRIVATE_KEY_BASE64");
        }
        return missing.toString();
    }

    private static void appendIfBlank(StringBuilder target, String value, String name) {
        if (!hasText(value)) {
            append(target, name);
        }
    }

    private static void append(StringBuilder target, String name) {
        if (!target.isEmpty()) {
            target.append(", ");
        }
        target.append(name);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }

    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getTenancyOcid() { return tenancyOcid; }
    public void setTenancyOcid(String tenancyOcid) { this.tenancyOcid = tenancyOcid; }

    public String getUserOcid() { return userOcid; }
    public void setUserOcid(String userOcid) { this.userOcid = userOcid; }

    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }

    public String getPrivateKeyPath() { return privateKeyPath; }
    public void setPrivateKeyPath(String privateKeyPath) { this.privateKeyPath = privateKeyPath; }

    public String getPrivateKeyBase64() { return privateKeyBase64; }
    public void setPrivateKeyBase64(String privateKeyBase64) { this.privateKeyBase64 = privateKeyBase64; }

    public String getPrivateKeyPassphrase() { return privateKeyPassphrase; }
    public void setPrivateKeyPassphrase(String privateKeyPassphrase) { this.privateKeyPassphrase = privateKeyPassphrase; }

    public Duration getSignedUrlTtl() { return signedUrlTtl; }
    public void setSignedUrlTtl(Duration signedUrlTtl) { this.signedUrlTtl = signedUrlTtl; }

    public String getKeyPrefix() { return keyPrefix; }
    public void setKeyPrefix(String keyPrefix) { this.keyPrefix = keyPrefix; }
}
