package com.dcplatform.api.documents;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Cuerpo de la peticion de generacion de un PDF institucional.
 *
 * <pre>
 * {
 *   "metadata": { "name": "informe-julio" },
 *   "date": "Thu Jul 30 09:27:13 PM -05 2026",
 *   "title": "Informe de julio",
 *   "message": ["primer parrafo", "segundo parrafo"]
 * }
 * </pre>
 *
 * <p><b>Diferencia con el proyecto demo:</b> alli {@code metadata.owner} viajaba en el cuerpo y
 * cualquiera podia escribir en la carpeta de cualquiera. Aca el dueno sale <em>siempre</em> del
 * token de acceso; no se acepta por parametro.
 */
@Schema(description = "Datos con los que se rellena la plantilla del documento")
public record DocumentRequest(

        @NotNull(message = "metadata es obligatorio") @Valid Metadata metadata,

        @Schema(description = """
                Fecha a imprimir. Acepta la salida de date(1) y el formato ISO-8601; si no se \
                reconoce se imprime tal cual. Vacio usa la fecha del servidor.""",
                example = "Thu Jul 30 09:27:13 PM -05 2026")
        String date,

        @Schema(description = "Titulo del documento", example = "Informe de julio")
        @NotBlank(message = "title es obligatorio")
        @Size(max = 300, message = "title no puede superar los 300 caracteres")
        String title,

        @Schema(description = "Parrafos del cuerpo, en orden",
                example = "[\"Primer párrafo del documento.\", \"Segundo párrafo.\"]")
        @NotEmpty(message = "message debe tener al menos un parrafo")
        List<String> message) {

    /** @param name nombre del documento, sin la extension .pdf */
    @Schema(description = "Identidad del documento dentro de la cuenta del usuario")
    public record Metadata(

            @Schema(description = """
                    Nombre del documento, sin la extension .pdf. Unico por usuario: regenerar con \
                    el mismo nombre reemplaza el documento anterior. Solo letras, digitos, \
                    '.', '-' y '_'.""",
                    example = "informe-julio")
            @NotBlank(message = "metadata.name es obligatorio")
            // Termina siendo parte de la clave del objeto en el bucket: lista blanca estricta.
            @Pattern(regexp = "[A-Za-z0-9][A-Za-z0-9._-]{0,63}",
                    message = "metadata.name solo admite letras, digitos, '.', '-' y '_' (max 64, "
                            + "empezando por letra o digito)")
            String name) {
    }
}
