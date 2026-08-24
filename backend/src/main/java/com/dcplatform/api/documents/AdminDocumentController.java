package com.dcplatform.api.documents;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.dcplatform.api.shared.annotations.ApiJsonExample;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Inventario completo de documentos, agrupado por usuario.
 *
 * <p>Equivale al {@code GET /api/v1/documents} global del proyecto demo, con la diferencia de que
 * alli era publico. La proteccion de rol la aplica SecurityConfig sobre {@code /api/v1/admin/**}.
 */
@RestController
@RequestMapping("/api/v1/admin/documents")
@Tag(name = "Documentos (admin)", description = "Inventario de documentos de todos los usuarios")
@SecurityRequirement(name = "bearer-jwt")
public class AdminDocumentController {

    private final DocumentService documents;

    public AdminDocumentController(DocumentService documents) {
        this.documents = documents;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lista los documentos de todos los usuarios",
            description = "Un elemento por usuario con documentos, en orden alfabetico de email.")
    @ApiJsonExample(
            description = "Inventario completo de documentos",
            path = "/static/swagger/examples/documents/admin-documents-list-all-200-success.json")
    @ApiJsonExample(
            status = "403",
            description = "Acceso denegado por rol",
            path = "/static/swagger/examples/documents/admin-documents-403-forbidden.json")
    public List<OwnerDocuments> listAll() {
        return documents.listAll();
    }
}
