package com.dcplatform.api.benchmark.service.mapper;

import com.dcplatform.api.benchmark.model.BenchmarkInstrument;
import com.dcplatform.api.benchmark.model.dto.ActiveBenchmarkResponse;

public interface BenchmarkMapper {

	ActiveBenchmarkResponse toActiveBenchmarkResponse(BenchmarkInstrument entity);
}
