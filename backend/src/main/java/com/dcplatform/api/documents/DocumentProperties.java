package com.dcplatform.api.documents;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Ajustes del modulo de documentos institucionales, bajo {@code app.documents}.
 *
 * @param assetsLocation  ubicacion Spring de los assets. Por defecto el classpath
 *                        ({@code classpath:assets/}), que es lo que hay dentro del JAR.
 *                        Para iterar el diseno sin recompilar: {@code file:./assets/}.
 * @param logoFile        nombre del archivo de logo dentro de assetsLocation
 * @param template        template Thymeleaf, relativo a src/main/resources/templates y sin .html
 * @param templateVersion version de plantilla que se guarda con cada documento, para poder
 *                        regenerar historicos con el diseno con el que se emitieron
 * @param storagePrefix   prefijo de las claves dentro del bucket
 * @param organization    textos de marca del encabezado y del pie
 */
@ConfigurationProperties(prefix = "app.documents")
public record DocumentProperties(
        String assetsLocation,
        String logoFile,
        String template,
        String templateVersion,
        String storagePrefix,
        Organization organization) {

    /**
     * @param name       nombre de la institucion que emite el documento
     * @param tagline    linea de apoyo bajo el nombre, en el encabezado
     * @param footerNote texto legal o de contacto del pie de pagina
     */
    public record Organization(String name, String tagline, String footerNote) {
    }
}
