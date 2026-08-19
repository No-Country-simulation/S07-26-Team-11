package com.dcplatform.api.shared;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class RootController {

	@Value("${springdoc.swagger-ui.path}")
	private String swaggerUrl;

	@GetMapping(
			value = "/",
			produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_HTML_VALUE}
	)
	public ResponseEntity<?> handleRoot(
			@RequestHeader(value = HttpHeaders.ACCEPT, required = false, defaultValue = "*/*") String acceptHeader) {

		// redirección si la petición es enviada desde un navegador web (HTML)
		if (acceptHeader.contains(MediaType.TEXT_HTML_VALUE)) {
			return ResponseEntity
					.status(HttpStatus.FOUND)
					.location(URI.create(swaggerUrl))
					.build();
		}

		RootModel model = new RootModel(
				"Plataforma de Benchmark de Data Centers",
				"Calculadora, benchmark de madurez, generación de PDF y outreach. CAPACIA",
				"v1",
				"UP"
		);

		// enlaces HAL
		// 'self' referenciando dinámicamente este mismo método del controlador
		model.add(linkTo(methodOn(RootController.class).handleRoot(acceptHeader)).withSelfRel());

		// enlaces estáticos a recursos de sistema y documentación
		model.add(Link.of(swaggerUrl).withRel("documentation"));
		model.add(Link.of("/actuator/health").withRel("health"));
		model.add(linkTo(methodOn(HealthController.class).ping()).withRel("ping"));
		model.add(linkTo(methodOn(DatabaseStatusController.class).databaseStatus()).withRel("database-status"));

		return ResponseEntity.ok()
				.contentType(MediaTypes.HAL_JSON)
				.body(model);
	}

	static class RootModel extends RepresentationModel<RootModel> {

		private final String name;
		private final String description;
		private final String version;
		private final String status;

		public RootModel(String name, String description, String version, String status) {
			this.name = name;
			this.description = description;
			this.version = version;
			this.status = status;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public String getVersion() {
			return version;
		}

		public String getStatus() {
			return status;
		}
	}
}
