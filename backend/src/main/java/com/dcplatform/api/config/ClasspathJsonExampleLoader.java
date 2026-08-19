package com.dcplatform.api.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class ClasspathJsonExampleLoader implements JsonExampleLoader {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public JsonNode load(String path) {
		try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
			return objectMapper.readTree(inputStream);
		} catch (IOException e) {
			throw new IllegalArgumentException("No se pudo cargar o parsear el archivo de ejemplo JSON desde el classpath: " + path, e);
		}
	}
}
