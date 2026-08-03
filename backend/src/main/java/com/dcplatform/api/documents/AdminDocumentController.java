package com.dcplatform.api.documents;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @GetMapping
    @Operation(summary = "Lista los documentos de todos los usuarios",
            description = "Un elemento por usuario con documentos, en orden alfabetico de email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventario completo"),
            @ApiResponse(responseCode = "403", description = "El token no tiene rol ADMIN")})
    public List<OwnerDocuments> listAll() {
        return documents.listAll();
    }
}
