package com.dcplatform.api.documents;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Cuerpo de la peticion de generacion del informe de capacidad (PDF institucional).
 *
 * <p>Los campos reflejan uno a uno las variables que consume la plantilla
 * {@code pdf/institutional-document.html}: no hay campos de relleno libre (el antiguo
 * {@code message[]} nunca llegaba a renderizarse). Formatear los valores para mostrar
 * ("US$ 48.200", "38%", "142 kW") es responsabilidad de quien arma la peticion: el backend
 * los imprime tal cual, sin aplicar moneda ni locale.
 *
 * <pre>
 * {
 *   "metadata": { "name": "informe-benchmark" },
 *   "date": "Thu Jul 30 09:27:13 PM -05 2026",
 *   "title": "Informe de capacidad — Northbridge Data Systems",
 *   "executiveSummary": "Su infraestructura opera hoy con una utilización promedio del 70%...",
 *   "annualCost": "US$ 48.200",
 *   "maturityLevel": "Gestionado",
 *   "score": "54 / 100",
 *   "kwUnderutilized": "142 kW",
 *   "utilizationPercent": "38%",
 *   "costPerRack": "US$ 1.004",
 *   "industryScores": [
 *     { "label": "Tu resultado", "value": 54, "own": true },
 *     { "label": "Hyperscale", "value": 71, "own": false }
 *   ],
 *   "recommendations": ["Implementá monitoreo por carga de trabajo..."]
 * }
 * </pre>
 *
 * <p><b>Diferencia con el proyecto demo:</b> alli {@code metadata.owner} viajaba en el cuerpo y
 * cualquiera podia escribir en la carpeta de cualquiera. Aca el dueno sale <em>siempre</em> del
 * token de acceso; no se acepta por parametro.
 */
@Schema(description = "Datos con los que se rellena el informe de capacidad")
public record DocumentRequest(

        @NotNull(message = "metadata es obligatorio") @Valid Metadata metadata,

        @Schema(description = """
                Fecha a imprimir. Acepta la salida de date(1) y el formato ISO-8601; si no se \
                reconoce se imprime tal cual. Vacio usa la fecha del servidor.""",
                example = "Thu Jul 30 09:27:13 PM -05 2026")
        String date,

        @Schema(description = "Titulo del documento", example = "Informe de capacidad — Northbridge Data Systems")
        @NotBlank(message = "title es obligatorio")
        @Size(max = 300, message = "title no puede superar los 300 caracteres")
        String title,

        @Schema(description = "Parrafo de resumen ejecutivo, primera pagina")
        @NotBlank(message = "executiveSummary es obligatorio")
        @Size(max = 1000, message = "executiveSummary no puede superar los 1000 caracteres")
        String executiveSummary,

        @Schema(description = "Costo anual desperdiciado, ya formateado", example = "US$ 48.200")
        @NotBlank(message = "annualCost es obligatorio")
        String annualCost,

        @Schema(description = "Nivel de madurez", example = "Gestionado")
        @NotBlank(message = "maturityLevel es obligatorio")
        String maturityLevel,

        @Schema(description = "Score de madurez, ya formateado", example = "54 / 100")
        @NotBlank(message = "score es obligatorio")
        String score,

        @Schema(description = "kW subutilizados, ya formateado", example = "142 kW")
        @NotBlank(message = "kwUnderutilized es obligatorio")
        String kwUnderutilized,

        @Schema(description = "Porcentaje de utilizacion, ya formateado", example = "38%")
        @NotBlank(message = "utilizationPercent es obligatorio")
        String utilizationPercent,

        @Schema(description = "Costo por rack al año, ya formateado", example = "US$ 1.004")
        @NotBlank(message = "costPerRack es obligatorio")
        String costPerRack,

        @Schema(description = "Comparacion contra la industria. Una fila por barra del grafico.")
        @NotEmpty(message = "industryScores debe tener al menos un elemento")
        @Size(max = 6, message = "industryScores admite como maximo 6 elementos")
        @Valid
        List<IndustryScore> industryScores,

        @Schema(description = "Recomendaciones personalizadas, en orden de prioridad")
        @NotEmpty(message = "recommendations debe tener al menos un elemento")
        @Size(max = 6, message = "recommendations admite como maximo 6 elementos")
        List<@NotBlank(message = "una recomendacion no puede estar vacia")
             @Size(max = 300, message = "una recomendacion no puede superar los 300 caracteres")
             String> recommendations) {

    /** @param name nombre del documento, sin la extension .pdf */
    @Schema(description = "Identidad del documento dentro de la cuenta del usuario")
    public record Metadata(

            @Schema(description = """
                    Nombre del documento, sin la extension .pdf. Unico por usuario: regenerar con \
                    el mismo nombre reemplaza el documento anterior. Solo letras, digitos, \
                    '.', '-' y '_'.""",
                    example = "informe-benchmark")
            @NotBlank(message = "metadata.name es obligatorio")
            // Termina siendo parte de la clave del objeto en el bucket: lista blanca estricta.
            @Pattern(regexp = "[A-Za-z0-9][A-Za-z0-9._-]{0,63}",
                    message = "metadata.name solo admite letras, digitos, '.', '-' y '_' (max 64, "
                            + "empezando por letra o digito)")
            String name) {
    }

    /**
     * Una barra del grafico "Posición frente a la industria".
     *
     * @param label nombre del segmento ("Tu resultado", "Hyperscale", ...)
     * @param value score de madurez, 0-100. Tambien fija el ancho de la barra.
     * @param own   true solo en la fila del propio operador: la plantilla la pinta en dorado,
     *              el resto en verde oscuro.
     */
    public record IndustryScore(
            @NotBlank(message = "industryScores[].label es obligatorio")
            @Size(max = 60, message = "industryScores[].label no puede superar los 60 caracteres")
            String label,

            @Min(value = 0, message = "industryScores[].value debe estar entre 0 y 100")
            @Max(value = 100, message = "industryScores[].value debe estar entre 0 y 100")
            int value,

            boolean own) {
    }
}
