package com.dcplatform.api.benchmark.service;

import com.dcplatform.api.benchmark.model.dto.*;
import com.dcplatform.api.benchmark.repository.BenchmarkInstrumentRepository;
import com.dcplatform.api.benchmark.service.mapper.BenchmarkMapper;
import com.dcplatform.api.shared.ApiException;
import org.springframework.stereotype.Service;

@Service
public class BenchmarkServiceImpl implements BenchmarkService {

	private final BenchmarkMapper mapper;
	private final BenchmarkInstrumentRepository instrumentRepository;

	public BenchmarkServiceImpl(BenchmarkMapper mapper,
	                            BenchmarkInstrumentRepository instrumentRepository) {
		this.mapper = mapper;
		this.instrumentRepository = instrumentRepository;
	}

	@Override
	public ActiveBenchmarkResponse getActiveInstrument() {
		return instrumentRepository.findByIsActiveTrue()
				.map(mapper::toActiveBenchmarkResponse)
				.orElseThrow(() -> ApiException.notFound("No se encontró un instrumento de benchmark activo."));
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
