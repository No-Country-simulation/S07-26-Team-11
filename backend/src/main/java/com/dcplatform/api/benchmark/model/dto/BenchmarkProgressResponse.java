package com.dcplatform.api.benchmark.model.dto;

import java.util.UUID;

public record BenchmarkProgressResponse(
		UUID responseId,
		int answeredCount,
		int totalQuestions
) {
}
