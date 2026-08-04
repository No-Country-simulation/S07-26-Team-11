package com.dcplatform.api.documents;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Un PDF ya generado, tal como se expone en los endpoints de consulta.
 *
 * <p>No se expone la clave del objeto en el bucket: es un detalle de infraestructura y el cliente
 * no la necesita. Para bajar el archivo esta {@code downloadUrl}, que apunta a la API y no al
 * storage, para que el enlace permanente no dependa del proveedor.
 *
 * @param id          identificador del documento
 * @param owner       email del usuario dueno
 * @param name        nombre del documento, sin extension
 * @param fileName    nombre con el que se descarga
 * @param title       titulo impreso en el documento
 * @param sizeBytes   tamano del PDF
 * @param createdAt   cuando se genero por primera vez
 * @param updatedAt   cuando se regenero por ultima vez
 * @param downloadUrl endpoint de la API que redirige a la descarga
 */
public record DocumentSummary(
        UUID id,
        String owner,
        String name,
        String fileName,
        String title,
        long sizeBytes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        String downloadUrl) {

    static DocumentSummary of(UserDocument document, String ownerEmail) {
        return new DocumentSummary(
                document.getId(),
                ownerEmail,
                document.getName(),
                document.getName() + ".pdf",
                document.getTitle(),
                document.getSizeBytes(),
                document.getCreatedAt(),
                document.getUpdatedAt(),
                "/api/v1/documents/" + document.getName() + "/download");
    }
}
