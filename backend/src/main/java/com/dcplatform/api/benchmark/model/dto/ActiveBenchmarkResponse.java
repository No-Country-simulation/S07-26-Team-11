package com.dcplatform.api.benchmark.model.dto;

import java.util.List;
import java.util.UUID;

public record ActiveBenchmarkResponse(
		UUID instrumentId,
		String version,
		List<BenchmarkStageDto> stages
) {

	public record BenchmarkStageDto(
			UUID dimensionId,
			String code,
			String label,
			List<QuestionDto> questions
	) {
	}

	public record QuestionDto(
			UUID questionId,
			String text,
			String helpText,
			List<OptionDto> options
	) {
	}

	public record OptionDto(
			UUID optionId,
			String label
	) {
	}
}
