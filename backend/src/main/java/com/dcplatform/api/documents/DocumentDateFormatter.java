package com.dcplatform.api.documents;

import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;

/**
 * Convierte el campo {@code date} de la peticion en el texto que se imprime en el documento.
 *
 * <p>El cliente puede mandar la salida cruda de {@code date(1)}, un ISO-8601, o cualquier otra
 * cosa. Si se logra interpretar se normaliza a formato largo en espanol; si no, se imprime tal
 * cual llego en vez de rechazar la peticion, porque el campo es puramente de presentacion.
 */
@Component
public class DocumentDateFormatter {

    private static final Locale ES = Locale.of("es");

    private static final DateTimeFormatter OUTPUT =
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy, HH:mm", ES);

    private static final DateTimeFormatter OUTPUT_DATE_ONLY =
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", ES);

    /** Se prueban en orden; el primero que parsee gana. */
    private static final List<Parser> PARSERS = List.of(
            // date(1) con hora de 12 h y offset numerico: "Thu Jul 30 09:27:13 PM -05 2026"
            new Parser(DateTimeFormatter.ofPattern("EEE MMM d hh:mm:ss a X yyyy", Locale.ENGLISH), OUTPUT),
            // date(1) con hora de 24 h y offset numerico: "Thu Jul 30 21:27:13 -0500 2026"
            new Parser(DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss X yyyy", Locale.ENGLISH), OUTPUT),
            // date(1) con nombre de zona: "Thu Jul 30 21:27:13 COT 2026"
            new Parser(DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH), OUTPUT),
            new Parser(DateTimeFormatter.ISO_OFFSET_DATE_TIME, OUTPUT),
            new Parser(DateTimeFormatter.ISO_LOCAL_DATE_TIME, OUTPUT),
            new Parser(DateTimeFormatter.ISO_LOCAL_DATE, OUTPUT_DATE_ONLY));

    /**
     * @param raw valor recibido en {@code date}; vacio usa la fecha actual del servidor
     * @return texto listo para imprimir en el documento
     */
    public String format(String raw) {
        if (raw == null || raw.isBlank()) {
            return OUTPUT.format(ZonedDateTime.now());
        }
        // date(1) alinea el dia con espacios ("Jul  4"); un solo separador simplifica los patrones.
        String value = raw.trim().replaceAll("\\s+", " ");
        for (Parser parser : PARSERS) {
            String formatted = parser.tryFormat(value);
            if (formatted != null) {
                return formatted;
            }
        }
        return raw.trim();
    }

    private record Parser(DateTimeFormatter input, DateTimeFormatter output) {

        String tryFormat(String value) {
            try {
                TemporalAccessor parsed = input.parse(value);
                return output.format(parsed);
            } catch (RuntimeException e) {
                return null;
            }
        }
    }
}
