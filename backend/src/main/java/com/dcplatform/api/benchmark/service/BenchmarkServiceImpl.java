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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
	@Transactional(readOnly = true)
	public ActiveBenchmarkResponse getActiveInstrument() {
		return instrumentRepository.findByIsActiveTrue()
				.map(mapper::toActiveBenchmarkResponse)
				.orElseThrow(() -> ApiException.notFound("No existe un instrumento de benchmark activo."));
	}

	@Override
	@Transactional
	public StartBenchmark.Response startBenchmark(String leadEmail, StartBenchmark.Request request) {
		BenchmarkInstrument activeInstrument = instrumentRepository.findByIdAndIsActiveTrue(request.instrumentId())
				.orElseThrow(() -> ApiException.notFound("Instrumento de benchmark inexistente o inactivo."));

		LeadEntity authenticatedLead = leadService.getLeadEntityByEmail(leadEmail);

		Optional<BenchmarkResponse> existingResponse = responseRepository
				.findByLeadIdAndInstrumentId(authenticatedLead.getId(), activeInstrument.getId());

		// si ya existe una respuesta iniciada por el lead autenticado, retornarla
		if (existingResponse.isPresent()) {
			return mapper.toStartBenchmarkResponse(existingResponse.get());
		}

		// si no existe una respuesta iniciada por el lead autenticado, crear una nueva
		BenchmarkResponse response = new BenchmarkResponse();
		response.setLead(authenticatedLead);
		response.setInstrument(activeInstrument);
		response.markAsInProgress();

		try {
			response = responseRepository.saveAndFlush(response);
		} catch (DataIntegrityViolationException e) {
			// fallback: si hubo race condition y otro hilo insertó primero, la BD rechaza este insert
			BenchmarkResponse winnerResponse = responseRepository
					.findByLeadIdAndInstrumentId(authenticatedLead.getId(), activeInstrument.getId())
					.orElseThrow(
							() -> ApiException.conflict("Error resolviendo la concurrencia al iniciar benchmark.")
					);
			return mapper.toStartBenchmarkResponse(winnerResponse);
		}

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
