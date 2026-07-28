package com.dcplatform.api.pdf.storage;

import java.net.URI;
import java.util.UUID;

/**
 * Almacenamiento de los PDF generados.
 *
 * Se define como interfaz para que el resto del modulo no dependa del SDK de
 * Oracle: si en algun momento se cambia de proveedor, el cambio queda contenido
 * en este paquete. Ver ADR pendiente sobre object storage.
 */
public interface PdfStorage {

    /** true si hay credenciales cargadas y el bucket es utilizable. */
    boolean isAvailable();

    /**
     * Clave del objeto para una respuesta de benchmark. Derivada del responseId
     * para que reprocesar un job pise el mismo objeto en vez de acumular copias
     * (idempotencia por responseId, ver PdfGeneratorArchitecture.md).
     */
    String storageKeyFor(UUID responseId);

    /** Sube el PDF y devuelve la clave con la que quedo guardado. */
    String upload(String storageKey, byte[] content);

    /**
     * URL temporal de descarga directa desde el storage, para redirigir con 302.
     * La vigencia la fija app.storage.oci.signed-url-ttl.
     */
    URI createSignedDownloadUrl(String storageKey, String fileName);
}
