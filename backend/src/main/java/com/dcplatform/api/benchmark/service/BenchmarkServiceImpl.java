package com.dcplatform.api.benchmark.service;

import com.dcplatform.api.benchmark.model.BenchmarkInstrument;
import com.dcplatform.api.benchmark.model.BenchmarkResponse;
import com.dcplatform.api.benchmark.model.dto.*;
import com.dcplatform.api.benchmark.repository.BenchmarkInstrumentRepository;
import com.dcplatform.api.benchmark.repository.BenchmarkResponseRepository;
import com.dcplatform.api.benchmark.service.mapper.BenchmarkMapper;
import com.dcplatform.api.leads.model.LeadEntity;
import com.dcplatform.api.leads.service.LeadService;
import com.dcplatform.api.shared.ApiException;
import org.springframework.stereotype.Service;

@Service
public class BenchmarkServiceImpl implements BenchmarkService {

	private final BenchmarkMapper mapper;
	private final BenchmarkInstrumentRepository instrumentRepository;
	private final BenchmarkResponseRepository responseRepository;
	private final LeadService leadService;

	public BenchmarkServiceImpl(BenchmarkMapper mapper,
	                            BenchmarkInstrumentRepository instrumentRepository,
	                            BenchmarkResponseRepository responseRepository,
	                            LeadService leadService) {
		this.mapper = mapper;
		this.instrumentRepository = instrumentRepository;
		this.responseRepository = responseRepository;
		this.leadService = leadService;
	}

	@Override
	public ActiveBenchmarkResponse getActiveInstrument() {
		return instrumentRepository.findByIsActiveTrue()
				.map(mapper::toActiveBenchmarkResponse)
				.orElseThrow(() -> ApiException.notFound("No se encontró un instrumento de benchmark activo."));
	}

	@Override
	public StartBenchmark.Response startBenchmark(String leadEmail, StartBenchmark.Request request) {
		BenchmarkInstrument activeInstrument = instrumentRepository.findByIdAndIsActiveTrue(request.instrumentId())
				.orElseThrow(() -> ApiException.notFound("Instrumento de benchmark no encontrado, inactivo o ambos."));

		LeadEntity authenticatedLead = leadService.getLeadEntityByEmail(leadEmail);

		BenchmarkResponse response = new BenchmarkResponse();
		response.setLeadId(authenticatedLead.getId());
		response.setInstrumentId(activeInstrument.getId());
		response.markAsInProgress();

		response = responseRepository.save(response);

		return mapper.toStartBenchmarkResponse(response);
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
