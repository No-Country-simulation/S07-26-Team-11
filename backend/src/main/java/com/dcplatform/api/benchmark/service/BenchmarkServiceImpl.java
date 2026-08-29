package com.dcplatform.api.benchmark.service;

import com.dcplatform.api.benchmark.model.dto.*;
import org.springframework.stereotype.Service;

@Service
public class BenchmarkServiceImpl implements BenchmarkService {

	@Override
	public ActiveBenchmarkResponse getActiveInstrument() {
		return null;
	}

	@Override
	public StartBenchmark.Response startBenchmark(String leadEmail, StartBenchmark.Request request) {
		return null;
	}

	@Override
	public BenchmarkProgressResponse saveProgress(String leadEmail, String responseId, SubmitBenchmarkRequest request) {
		return null;
	}

	@Override
	public CompleteBenchmarkResponse completeBenchmark(String leadEmail, String responseId) {
		return null;
	}

	@Override
	public CompleteBenchmarkResponse getResponse(String leadEmail, String responseId) {
		return null;
	}
}
