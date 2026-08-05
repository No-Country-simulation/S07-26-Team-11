package com.dcplatform.api.documents;

import java.util.List;

/**
 * Listado de documentos de un usuario.
 *
 * <p>Un usuario sin documentos devuelve {@code count: 0} y lista vacia, nunca 404: no tener
 * documentos no es un error.
 *
 * @param owner     email del usuario dueno
 * @param count     cantidad de documentos
 * @param documents documentos, del mas reciente al mas antiguo
 */
public record OwnerDocuments(String owner, int count, List<DocumentSummary> documents) {

    static OwnerDocuments of(String owner, List<DocumentSummary> documents) {
        return new OwnerDocuments(owner, documents.size(), documents);
    }
}
