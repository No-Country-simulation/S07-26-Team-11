package com.dcplatform.api.shared.event;

import java.util.UUID;

public record BenchmarkCompletedEvent(
		UUID responseId
) {
}
