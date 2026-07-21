package com.dcplatform.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * La especificacion OpenAPI se genera desde el codigo y es el contrato con el frontend.
 * El frontend genera sus tipos TypeScript desde aqui; el QA importa esto a Postman.
 * Documento de referencia acordado: docs/API.md
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiDefinition() {
        return new OpenAPI()
                .info(new Info()
                        .title("Plataforma de Benchmark de Data Centers")
                        .version("v1")
                        .description("Calculadora, benchmark de madurez, generacion de PDF y outreach."))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local"),
                        new Server().url("https://[por-definir]").description("Staging")));
    }
}
