package com.dcplatform.api.documents;

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Map;

/**
 * Cadena de render del documento institucional: modelo -> HTML (Thymeleaf) -> PDF
 * (Open HTML to PDF), tal como la describe {@code pdf/PdfGeneratorArchitecture.md}.
 *
 * <p>El paso por jsoup no es decorativo: el motor consume XML bien formado y Thymeleaf produce
 * HTML5 (etiquetas vacias sin cerrar, entidades, atributos sin comillas). jsoup parsea con las
 * reglas indulgentes del navegador y entrega un DOM W3C valido.
 *
 * <p>Limites del motor: CSS 2.1 + paged media. <b>Sin flexbox, sin grid, sin JavaScript.</b>
 * El layout se arma con tablas y bloques. Las graficas van como SVG generado en el servidor.
 */
@Component
public class InstitutionalPdfRenderer {

    private static final Locale ES = Locale.of("es");

    private final ITemplateEngine templateEngine;
    private final DocumentAssets assets;
    private final String templateName;

    public InstitutionalPdfRenderer(ITemplateEngine templateEngine,
                                    DocumentAssets assets,
                                    DocumentProperties properties) {
        this.templateEngine = templateEngine;
        this.assets = assets;
        this.templateName = properties.template();
    }

    /**
     * Renderiza solo el HTML intermedio. Lo usa {@code POST /documents/preview} para iterar el
     * diseno en el navegador, que da recarga inmediata.
     *
     * @param model variables que consume el template
     */
    public String renderHtml(Map<String, Object> model) {
        return templateEngine.process(templateName, new Context(ES, model));
    }

    /** @return los bytes del PDF ya paginado */
    public byte[] renderPdf(Map<String, Object> model) {
        return toPdf(renderHtml(model));
    }

    /** @param html documento HTML completo, ya renderizado por Thymeleaf */
    public byte[] toPdf(String html) {
        org.w3c.dom.Document dom = new W3CDom().fromJsoup(Jsoup.parse(html));

        ByteArrayOutputStream out = new ByteArrayOutputStream(64 * 1024);
        PdfRendererBuilder builder = new PdfRendererBuilder();
        // El baseUri queda vacio a proposito: los assets viajan como data URI, no como rutas.
        builder.withW3cDocument(dom, "");
        // Habilita <svg> incrustado, que es como se pintan las graficas (el motor no ejecuta JS).
        builder.useSVGDrawer(new BatikSVGDrawer());
        registerFonts(builder);
        builder.toStream(out);
        try {
            // El titulo y el autor del PDF salen de <title> y <meta name="author"> del template.
            builder.run();
        } catch (IOException e) {
            throw new UncheckedIOException("Fallo el render del PDF institucional", e);
        }
        return out.toByteArray();
    }

    private void registerFonts(PdfRendererBuilder builder) {
        for (DocumentAssets.FontFile font : assets.fonts()) {
            builder.useFont(font.stream(), font.family(), font.weight(),
                    font.italic() ? FontStyle.ITALIC : FontStyle.NORMAL, true);
        }
    }
}
