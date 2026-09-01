package com.dcplatform.api.benchmark.model.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CompleteBenchmarkResponse(
		UUID responseId,
		OffsetDateTime completedAt,
		BigDecimal globalScore,
		Integer maturityLevel,
		String maturityLabel,
		BigDecimal percentile,
		Integer cohortSize,
		List<DimensionScore> dimensions,
		UUID pdfJobId
) {
	public record DimensionScore(
			String code,
			String label,
			BigDecimal score,
			BigDecimal cohortMedian,
			BigDecimal gap
	) {
	}
}
