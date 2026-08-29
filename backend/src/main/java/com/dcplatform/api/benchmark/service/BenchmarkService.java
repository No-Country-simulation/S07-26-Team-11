package com.dcplatform.api.benchmark.service;

import com.dcplatform.api.benchmark.model.dto.*;

public interface BenchmarkService {

	ActiveBenchmarkResponse getActiveInstrument();

	StartBenchmark.Response startBenchmark(String leadEmail, StartBenchmark.Request request);

	BenchmarkProgressResponse saveProgress(String leadEmail, String responseId, SubmitBenchmarkRequest request);

	CompleteBenchmarkResponse completeBenchmark(String leadEmail, String responseId);

	CompleteBenchmarkResponse getResponse(String leadEmail, String responseId);

}
