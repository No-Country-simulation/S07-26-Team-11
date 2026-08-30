package com.dcplatform.api.benchmark.service.mapper;

import com.dcplatform.api.benchmark.model.BenchmarkInstrument;
import com.dcplatform.api.benchmark.model.BenchmarkResponse;
import com.dcplatform.api.benchmark.model.dto.ActiveBenchmarkResponse;
import com.dcplatform.api.benchmark.model.dto.StartBenchmark;

public interface BenchmarkMapper {

	ActiveBenchmarkResponse toActiveBenchmarkResponse(BenchmarkInstrument entity);

	StartBenchmark.Response toStartBenchmarkResponse(BenchmarkResponse entity);
}
