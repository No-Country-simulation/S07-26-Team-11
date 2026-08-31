package com.dcplatform.api.benchmark.service;

import com.dcplatform.api.benchmark.model.BenchmarkInstrument;
import com.dcplatform.api.benchmark.model.BenchmarkResponse;
import com.dcplatform.api.benchmark.model.dto.*;
import com.dcplatform.api.benchmark.repository.BenchmarkAnswerRepository;
import com.dcplatform.api.benchmark.repository.BenchmarkInstrumentRepository;
import com.dcplatform.api.benchmark.repository.BenchmarkResponseRepository;
import com.dcplatform.api.benchmark.service.mapper.BenchmarkMapper;
import com.dcplatform.api.leads.model.LeadEntity;
import com.dcplatform.api.leads.service.LeadService;
import com.dcplatform.api.shared.ApiException;
import com.dcplatform.api.shared.UUIDValidator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class BenchmarkServiceImpl implements BenchmarkService {

	private final BenchmarkMapper mapper;
	private final BenchmarkInstrumentRepository instrumentRepository;
	private final BenchmarkResponseRepository responseRepository;
	private final BenchmarkAnswerRepository answerRepository;
	private final LeadService leadService;

	public BenchmarkServiceImpl(BenchmarkMapper mapper,
	                            BenchmarkInstrumentRepository instrumentRepository,
	                            BenchmarkResponseRepository responseRepository,
	                            BenchmarkAnswerRepository answerRepository,
	                            LeadService leadService) {
		this.mapper = mapper;
		this.instrumentRepository = instrumentRepository;
		this.responseRepository = responseRepository;
		this.answerRepository = answerRepository;
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
				.findByLeadAndInstrument(authenticatedLead, activeInstrument);

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
					.findByLeadAndInstrument(authenticatedLead, activeInstrument)
					.orElseThrow(
							() -> ApiException.conflict("Error resolviendo la concurrencia al iniciar benchmark.")
					);
			return mapper.toStartBenchmarkResponse(winnerResponse);
		}

		return mapper.toStartBenchmarkResponse(response);
	}

	@Override
	public BenchmarkProgressResponse saveProgress(String leadEmail, String responseId, SubmitBenchmarkRequest request) {
		LeadEntity authenticatedLead = leadService.getLeadEntityByEmail(leadEmail);
		UUID parsedResponseId = UUIDValidator.safeParse(responseId);

		BenchmarkResponse existingResponse = responseRepository.findByIdAndLead(parsedResponseId, authenticatedLead)
				.orElseThrow(() -> ApiException.notFound(
						"No existe una respuesta de benchmark con el ID " + responseId + " para el lead autenticado."
				));

		for (SubmitBenchmarkRequest.QuestionAnswerDto answer : request.answers()) {
			answerRepository.upsertAnswer(
					parsedResponseId,
					answer.questionId(),
					answer.optionId()
			);
		}

		int totalQuestions = existingResponse.getInstrument().getDimensions().stream()
				.mapToInt(dimension -> dimension.getQuestions().size())
				.sum();

		int answeredCount = answerRepository.countByResponse(existingResponse);

		return new BenchmarkProgressResponse(parsedResponseId, answeredCount, totalQuestions);
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
