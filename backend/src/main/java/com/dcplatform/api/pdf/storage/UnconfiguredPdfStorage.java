package com.dcplatform.api.pdf.storage;

import com.dcplatform.api.shared.ApiException;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.UUID;

/**
 * Implementacion que se registra cuando faltan credenciales de OCI.
 *
 * Existe para que la API completa (ping, calculadora, benchmark, Swagger) siga
 * levantando sin el bucket configurado. Solo los endpoints que realmente tocan
 * el storage fallan, y lo hacen con un 503 que dice que falta configurar, en
 * lugar de tumbar el arranque o devolver una URL inventada.
 */
public class UnconfiguredPdfStorage implements PdfStorage {

    private final String missingSettings;

    public UnconfiguredPdfStorage(String missingSettings) {
        this.missingSettings = missingSettings;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public String storageKeyFor(UUID responseId) {
        // No toca el bucket: la clave es deterministica y sirve para no romper
        // el codigo que la calcula antes de subir.
        return "benchmark-reports/" + responseId + ".pdf";
    }

    @Override
    public String upload(String storageKey, byte[] content) {
        throw unavailable();
    }

    @Override
    public URI createSignedDownloadUrl(String storageKey, String fileName) {
        throw unavailable();
    }

    private ApiException unavailable() {
        return new ApiException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "storage-unavailable",
                "El almacenamiento de documentos no esta configurado. Faltan variables de entorno: " + missingSettings);
    }
}
