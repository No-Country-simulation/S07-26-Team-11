package com.dcplatform.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * La especificacion OpenAPI se genera desde el codigo y es el contrato con el frontend.
 * El frontend genera sus tipos TypeScript desde aqui; el QA importa esto a Postman.
 * Documento de referencia acordado: docs/API.md
 *
 * Swagger UI queda en /swagger-ui.html y la especificacion en /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    private final String localUrl;
    private final String stagingUrl;

    public OpenApiConfig(@Value("${server.port:8080}") String port,
                         @Value("${app.openapi.staging-url:}") String stagingUrl) {
        this.localUrl = "http://localhost:" + port;
        this.stagingUrl = stagingUrl;
    }

    @Bean
    public OpenAPI apiDefinition() {
        List<Server> servers = new ArrayList<>();
        servers.add(new Server().url(localUrl).description("Local"));

        // Solo se publica staging cuando hay una URL real configurada:
        // un servidor "https://[por-definir]" rompe el cliente generado
        // por el frontend y confunde en Postman.
        if (stagingUrl != null && !stagingUrl.isBlank()) {
            servers.add(new Server().url(stagingUrl).description("Staging"));
        }

        return new OpenAPI()
                .info(new Info()
                        .title("Plataforma de Benchmark de Data Centers")
                        .version("v1")
                        .description("Calculadora, benchmark de madurez, generacion de PDF y outreach. CAPACIA"))
                .servers(servers);
    }
}
