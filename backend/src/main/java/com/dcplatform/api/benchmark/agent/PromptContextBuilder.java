package com.dcplatform.api.benchmark.agent;

import com.dcplatform.api.benchmark.model.BenchmarkOption;
import com.dcplatform.api.benchmark.model.BenchmarkQuestion;

import java.util.List;

public class PromptContextBuilder {

	/**
	 * Convierte las opciones elegidas en un resumen de texto plano para el Agente.
	 */
	public static String buildQaContext(List<BenchmarkOption> selectedOptions) {
		StringBuilder contextBuilder = new StringBuilder();
		contextBuilder.append("A continuación se presentan las prácticas actuales del Data Center:\n\n");

		for (BenchmarkOption option : selectedOptions) {
			BenchmarkQuestion question = option.getQuestion();
			contextBuilder.append("- Pregunta: ").append(question.getText()).append("\n");
			contextBuilder.append("  Respuesta del cliente: ").append(option.getLabel()).append("\n\n");
		}

		return contextBuilder.toString();
	}
}
