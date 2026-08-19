package com.dcplatform.api.config;

import com.dcplatform.api.shared.annotations.ApiJsonExample;
import com.dcplatform.api.shared.annotations.ApiJsonExamples;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

	@Bean
	public OperationCustomizer customJsonExampleCustomizer() {
		return (operation, handlerMethod) -> {
			List<ApiJsonExample> annotations = new ArrayList<>();

			// 1. Extraer anotaciones individuales o repetidas
			ApiJsonExample single = handlerMethod.getMethodAnnotation(ApiJsonExample.class);
			if (single != null) {
				annotations.add(single);
			}

			ApiJsonExamples multiple = handlerMethod.getMethodAnnotation(ApiJsonExamples.class);
			if (multiple != null) {
				annotations.addAll(List.of(multiple.value()));
			}

			if (annotations.isEmpty()) {
				return operation;
			}

			ApiResponses responses = operation.getResponses();
			if (responses == null) {
				responses = new ApiResponses();
				operation.setResponses(responses);
			}

			// 2. Procesar cada anotación e inyectar el ejemplo JSON
			for (ApiJsonExample ann : annotations) {
				String jsonContent = loadClasspathFile(ann.path());

				ApiResponse apiResponse = responses.computeIfAbsent(
						ann.status().toString(),
						k -> new ApiResponse().description(ann.description())
				);

				if (apiResponse.getDescription() == null || apiResponse.getDescription().isBlank()) {
					apiResponse.setDescription(ann.description());
				}

				Content content = apiResponse.getContent();
				if (content == null) {
					content = new Content();
					apiResponse.setContent(content);
				}

				MediaType mediaType = content.computeIfAbsent(ann.mediaType(), k -> new MediaType());

				Example exampleObj = new Example();
				if (!ann.summary().isBlank()) {
					exampleObj.setSummary(ann.summary());
				}
				exampleObj.setValue(jsonContent);

				String exampleKey = ann.summary().isBlank() ? "Ejemplo " + ann.status() : ann.summary();
				mediaType.addExamples(exampleKey, exampleObj);
			}

			return operation;
		};
	}

	private String loadClasspathFile(String path) {
		try {
			ClassPathResource resource = new ClassPathResource(path);
			return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalArgumentException("No se pudo cargar el archivo de ejemplo JSON desde el classpath: " + path, e);
		}
	}
}
