package com.dcplatform.api.documents;

import com.openhtmltopdf.extend.FSSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Publica al template y al motor de render los recursos de {@code backend/assets}.
 *
 * <p>Las imagenes se entregan como data URI y no como ruta de archivo: asi el template no depende
 * del directorio de trabajo del proceso, y el HTML de {@code /preview} se ve en el navegador igual
 * que en el PDF.
 *
 * <p>Se leen una sola vez al arrancar: reemplazar el logo exige reiniciar. La falta de assets nunca
 * impide arrancar, misma regla que el resto de la infraestructura del proyecto: si el logo no esta
 * queda un WARN y el documento se emite sin el.
 */
@Component
public class DocumentAssets {

    private static final Logger log = LoggerFactory.getLogger(DocumentAssets.class);

    private static final Map<String, String> MIME_TYPES = Map.of(
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "gif", "image/gif",
            "svg", "image/svg+xml");

    /** Sufijo del nombre de archivo de fuente -> peso CSS. Convencion de Google Fonts. */
    private static final Map<String, Integer> FONT_WEIGHTS = Map.of(
            "thin", 100,
            "extralight", 200,
            "light", 300,
            "regular", 400,
            "medium", 500,
            "semibold", 600,
            "bold", 700,
            "extrabold", 800,
            "black", 900);

    private final Map<String, String> images;
    private final List<FontFile> fonts;

    public DocumentAssets(DocumentProperties properties) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String base = normalizeBase(properties.assetsLocation());

        this.images = loadLogo(resolver, base + properties.logoFile());
        this.fonts = loadFonts(resolver, base + "fonts/*.ttf");
    }

    /** Imagenes disponibles en el template por clave, por ejemplo {@code ${assets.logo}}. */
    public Map<String, String> images() {
        return images;
    }

    /** Fuentes a registrar en el motor de render. Vacio si no hay ninguna. */
    public List<FontFile> fonts() {
        return fonts;
    }

    private static Map<String, String> loadLogo(ResourcePatternResolver resolver, String location) {
        Resource resource = resolver.getResource(location);
        if (!resource.exists() || !resource.isReadable()) {
            log.warn("Logo no encontrado en '{}'. El PDF se generara sin logo. "
                    + "Ver backend/assets/README.md", location);
            return Map.of();
        }
        try (InputStream in = resource.getInputStream()) {
            String mime = MIME_TYPES.getOrDefault(extension(location), "application/octet-stream");
            String base64 = Base64.getEncoder().encodeToString(in.readAllBytes());
            log.info("Logo del PDF cargado desde {}", location);
            return Map.of("logo", "data:" + mime + ";base64," + base64);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el logo " + location, e);
        }
    }

    private static List<FontFile> loadFonts(ResourcePatternResolver resolver, String pattern) {
        Resource[] found;
        try {
            found = resolver.getResources(pattern);
        } catch (IOException e) {
            // Que no haya directorio de fuentes es normal: se usan las 14 fuentes base del PDF.
            log.info("Sin fuentes propias en '{}'; se usaran las fuentes base del PDF", pattern);
            return List.of();
        }

        List<FontFile> fonts = java.util.Arrays.stream(found)
                .filter(Resource::isReadable)
                .sorted(Comparator.comparing(r -> String.valueOf(r.getFilename())))
                .map(DocumentAssets::toFontFile)
                .toList();

        fonts.forEach(f -> log.info("Fuente registrada para el PDF: {} (family={} weight={} italic={})",
                f.fileName(), f.family(), f.weight(), f.italic()));
        return fonts;
    }

    /** Deriva familia, peso y estilo del nombre del archivo: {@code Inter-SemiBoldItalic.ttf}. */
    private static FontFile toFontFile(Resource resource) {
        String fileName = String.valueOf(resource.getFilename());
        String stem = stripExtension(fileName);
        int dash = stem.indexOf('-');
        String family = dash < 0 ? stem : stem.substring(0, dash);
        String variant = dash < 0 ? "regular" : stem.substring(dash + 1).toLowerCase(Locale.ROOT);

        boolean italic = variant.contains("italic");
        String weightKey = variant.replace("italic", "").trim();
        int weight = FONT_WEIGHTS.getOrDefault(weightKey.isEmpty() ? "regular" : weightKey, 400);

        // FSSupplier y no File: dentro del JAR la fuente no es un archivo del sistema.
        FSSupplier<InputStream> supplier = () -> {
            try {
                return resource.getInputStream();
            } catch (IOException e) {
                throw new UncheckedIOException("No se pudo leer la fuente " + fileName, e);
            }
        };
        return new FontFile(fileName, family, weight, italic, supplier);
    }

    /** Garantiza que la ubicacion termine en '/' para poder concatenarle nombres. */
    private static String normalizeBase(String location) {
        String value = (location == null || location.isBlank()) ? "classpath:assets/" : location.trim();
        return value.endsWith("/") ? value : value + "/";
    }

    private static String extension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? "" : fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }

    /** Una fuente lista para registrar en {@code PdfRendererBuilder.useFont(...)}. */
    public record FontFile(String fileName, String family, int weight, boolean italic,
                           FSSupplier<InputStream> stream) {
    }
}
