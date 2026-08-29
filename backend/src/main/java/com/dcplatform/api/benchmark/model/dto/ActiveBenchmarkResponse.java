package com.dcplatform.api.benchmark.model.dto;

import java.util.List;
import java.util.UUID;

public record ActiveBenchmarkResponse(
		UUID instrumentId,
		String version,
		List<BenchmarkStage> stages
) {

	public record BenchmarkStage(
			UUID dimensionId,
			String code,
			String label,
			List<Question> questions
	) {
	}

	public record Question(
			UUID questionId,
			String text,
			String helpText,
			List<Option> options
	) {
	}

	public record Option(
			UUID optionId,
			String label
	) {
	}
}
