package com.dcplatform.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * La especificación OpenAPI se genera desde el código y es el contrato con el frontend.
 * El frontend genera sus tipos TypeScript desde aquí; el QA importa esto a Postman.
 * Documento de referencia acordado: docs/API.md
 * <p>
 * Swagger UI queda en /swagger-ui.html y la especificación en /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI apiDefinition() {
		return new OpenAPI()
				.info(new Info()
						.title("Plataforma de Benchmark de Data Centers")
						.version("v1")
						.description("Calculadora, benchmark de madurez, generación de PDF y outreach. CAPACIA"))
				.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
				.components(new Components()
						.addSecuritySchemes("BearerAuth", new SecurityScheme()
								.name("BearerAuth")
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")));
	}
}
