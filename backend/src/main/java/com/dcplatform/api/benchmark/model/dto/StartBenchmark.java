package com.dcplatform.api.benchmark.model.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StartBenchmark() {

	public record Request(
			UUID instrumentId
	) {
	}

	public record Response(
			UUID responseId,
			OffsetDateTime startedAt,
			String status
	) {
	}
}
