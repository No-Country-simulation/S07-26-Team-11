package com.dcplatform.api.benchmark.agent.dto;

import java.util.List;

public record AiReportResult(
		String executiveSummary,
		List<String> recommendations
) {
}
