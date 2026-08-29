package com.dcplatform.api.benchmark.model.dto;

import java.util.List;
import java.util.UUID;

public record SubmitBenchmarkRequest(
		List<QuestionAnswerDto> answers
) {

	public record QuestionAnswerDto(
			UUID questionId,
			UUID optionId
	) {
	}
}
