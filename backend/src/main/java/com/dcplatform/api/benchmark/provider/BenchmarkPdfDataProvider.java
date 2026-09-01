package com.dcplatform.api.benchmark.provider;

import com.dcplatform.api.benchmark.repository.BenchmarkResponseRepository;
import com.dcplatform.api.pdf.provider.PdfTemplateDataProvider;
import com.dcplatform.api.shared.ApiException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class BenchmarkPdfDataProvider implements PdfTemplateDataProvider {

	private final BenchmarkResponseRepository repository;

	public BenchmarkPdfDataProvider(BenchmarkResponseRepository repository) {
		this.repository = repository;
	}

	@Override
	public Map<String, Object> provideData(UUID sourceId) {
		var response = repository.findById(sourceId)
				.orElseThrow(() -> ApiException.notFound("Benchmark response not found for ID: " + sourceId));

		// Armado del mapa para el PDF
		Map<String, Object> variables = new HashMap<>();
		variables.put("org", Map.of(
				"name", "Capacia",
				"tagline", "Benchmark de Data Centers"
		));
		variables.put("assets", Map.of(
				"logo", "classpath:/static/images/logo.png" // Ajustar a la ruta real
		));
		variables.put("footerText", "Capacia — Informe de capacidad | Confidencial");
		variables.put("title", "Informe de capacidad");
		variables.put("meetingLink", "https://capacia.vercel.app/benchmark/reunion");
		variables.put("contactEmail", "enterprise@capacia.com");

		// Variables de la IA
		variables.put("executiveSummary", response.getAiReportResult().executiveSummary());
		variables.put("recommendations", response.getAiReportResult().recommendations());

		// Variables de la Base de Datos (Benchmark / Calculadora)
		// Se formatean los valores para que coincidan con la vista
		variables.put("score", response.getGlobalScore() != null ? response.getGlobalScore() + " / 100" : "N/A");

		// Nota: Estos datos vendrían cruzados con calculator_estimates si aplica
		variables.put("annualCost", "US$ 48.200");
		variables.put("kwUnderutilized", "142 kW");
		variables.put("utilizationPercent", "38%");
		variables.put("costPerRack", "US$ 1.004");
		variables.put("industryScores", java.util.List.of()); // Mapear los scores por segmento si es necesario

		return variables;
	}
}
