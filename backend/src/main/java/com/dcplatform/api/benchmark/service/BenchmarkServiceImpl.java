package com.dcplatform.api.benchmark.service;

import com.dcplatform.api.benchmark.model.*;
import com.dcplatform.api.benchmark.model.dto.*;
import com.dcplatform.api.benchmark.repository.BenchmarkAnswerRepository;
import com.dcplatform.api.benchmark.repository.BenchmarkInstrumentRepository;
import com.dcplatform.api.benchmark.repository.BenchmarkResponseRepository;
import com.dcplatform.api.benchmark.service.mapper.BenchmarkMapper;
import com.dcplatform.api.leads.model.LeadEntity;
import com.dcplatform.api.leads.service.LeadService;
import com.dcplatform.api.pdf.PdfService;
import com.dcplatform.api.shared.ApiException;
import com.dcplatform.api.shared.UUIDValidator;
import com.dcplatform.api.shared.event.BenchmarkCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.dcplatform.api.benchmark.model.BenchmarkResponse.BenchmarkStatus.IN_PROGRESS;

@Service
public class BenchmarkServiceImpl implements BenchmarkService {

	private final Logger log = LoggerFactory.getLogger(BenchmarkServiceImpl.class);

	private final BenchmarkInstrumentRepository instrumentRepository;
	private final BenchmarkResponseRepository responseRepository;
	private final BenchmarkAnswerRepository answerRepository;

	private final BenchmarkMapper mapper;
	private final LeadService leadService;
	private final ApplicationEventPublisher eventPublisher;
	private final PdfService pdfService;

	public BenchmarkServiceImpl(BenchmarkInstrumentRepository instrumentRepository,
	                            BenchmarkResponseRepository responseRepository,
	                            BenchmarkAnswerRepository answerRepository,
	                            BenchmarkMapper mapper,
	                            LeadService leadService,
	                            ApplicationEventPublisher eventPublisher,
	                            PdfService pdfService) {
		this.instrumentRepository = instrumentRepository;
		this.responseRepository = responseRepository;
		this.answerRepository = answerRepository;
		this.mapper = mapper;
		this.leadService = leadService;
		this.eventPublisher = eventPublisher;
		this.pdfService = pdfService;
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
				.findByLeadAndInstrumentAndStatus(authenticatedLead, activeInstrument, IN_PROGRESS);

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
			log.warn("Condición de concurrencia al iniciar benchmark: {}", e.getMessage());
			// fallback: si hubo race condition y otro hilo insertó primero, la BD rechaza este insert
			BenchmarkResponse winnerResponse = responseRepository
					.findByLeadAndInstrumentAndStatus(authenticatedLead, activeInstrument, IN_PROGRESS)
					.orElseThrow(
							() -> ApiException.conflict("Error resolviendo la concurrencia al iniciar benchmark.")
					);
			return mapper.toStartBenchmarkResponse(winnerResponse);
		}

		return mapper.toStartBenchmarkResponse(response);
	}

	@Override
	@Transactional
	public BenchmarkProgressResponse saveProgress(String leadEmail, String responseId, SubmitBenchmarkRequest request) {
		LeadEntity authenticatedLead = leadService.getLeadEntityByEmail(leadEmail);
		UUID parsedResponseId = UUIDValidator.safeParse(responseId);

		BenchmarkResponse existingResponse = responseRepository
				.findByIdAndLeadAndStatus(parsedResponseId, authenticatedLead, IN_PROGRESS)
				.orElseThrow(() -> ApiException.notFound(
						"No existe una respuesta de benchmark con el ID " + responseId + " para el lead autenticado."
				));

		// Llave: questionId | Valor: Set de optionIds válidos para esa pregunta
		Map<UUID, Set<UUID>> validQuestionsAndOptions = existingResponse.getInstrument().getDimensions().stream()
				.flatMap(dimension -> dimension.getQuestions().stream())
				.collect(Collectors.toMap(
						BenchmarkQuestion::getId,
						question -> question.getOptions().stream()
								.map(BenchmarkOption::getId)
								.collect(Collectors.toSet())
				));

		for (SubmitBenchmarkRequest.QuestionAnswerDto answer : request.answers()) {
			Set<UUID> validOptions = validQuestionsAndOptions.get(answer.questionId());

			// si la pregunta no existe o la opción no pertenece a la pregunta, se rechaza
			if (validOptions == null || !validOptions.contains(answer.optionId())) {
				throw ApiException.badRequest(
						"La pregunta u opción provista no es válida para este instrumento. Verifique los datos enviados."
				);
			}

			// en este punto, la ejecución del UPSET es completamente seguro
			answerRepository.upsertAnswer(
					parsedResponseId,
					answer.questionId(),
					answer.optionId()
			);
		}

		int totalQuestions = validQuestionsAndOptions.size();
		int answeredCount = answerRepository.countByResponse(existingResponse);

		return new BenchmarkProgressResponse(parsedResponseId, answeredCount, totalQuestions);
	}

	@Override
	@Transactional
	public CompleteBenchmarkResponse completeBenchmark(String leadEmail, String responseId) {
		UUID parsedResponseId = UUID.fromString(responseId);
		LeadEntity authenticatedLead = leadService.getLeadEntityByEmail(leadEmail);

		BenchmarkResponse response = responseRepository
				.findByIdAndLeadAndStatus(parsedResponseId, authenticatedLead, IN_PROGRESS)
				.orElseThrow(() -> ApiException.notFound(
						"Respuesta no encontrada para el ID: " + parsedResponseId + "."
				));

		if (response.getStatus() == BenchmarkResponse.BenchmarkStatus.COMPLETED) {
			// Idempotencia: si ya está completado, se lanza error 409
			throw ApiException.conflict("Este benchmark ya fue completado.");
		}

		// Cálculos Matemáticos en Memoria (Agrupando respuestas por dimensión)
		Map<BenchmarkDimension, List<BenchmarkAnswer>> answersByDimension = response.getAnswers().stream()
				.collect(Collectors.groupingBy(answer -> answer.getQuestion().getDimension()));

		List<CompleteBenchmarkResponse.DimensionScore> dimensionScore = new ArrayList<>();
		BigDecimal globalScore = BigDecimal.ZERO;

		for (Map.Entry<BenchmarkDimension, List<BenchmarkAnswer>> entry : answersByDimension.entrySet()) {
			BenchmarkDimension dimension = entry.getKey();
			List<BenchmarkAnswer> dimAnswers = entry.getValue();

			// Promedio simple de la dimensión
			double average = dimAnswers.stream()
					.mapToDouble(a -> a.getOption().getScore().doubleValue())
					.average()
					.orElse(0.0);
			BigDecimal dimScore = BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP);

			// Acumulación ponderada al score global
			globalScore = globalScore.add(dimScore.multiply(dimension.getWeight()));

			dimensionScore.add(new CompleteBenchmarkResponse.DimensionScore(
					dimension.getCode(),
					dimension.getLabel(),
					dimScore,
					BigDecimal.valueOf(50.0), // TODO: Reemplazar con query real de la cohorte
					dimScore.subtract(BigDecimal.valueOf(50.0)) // gap
			));
		}

		// Definir Nivel de Madurez y Reglas de Negocio
		response.setGlobalScore(globalScore.setScale(2, RoundingMode.HALF_UP));
		int maturityLevel = calculateMaturityLevel(globalScore);
		response.setMaturityLevel(maturityLevel);
		response.setCohortSize(47); // TODO: Reemplazar con count real
		response.setPercentile(new BigDecimal("0.68")); // Solo si cohortSize >= 5

		response.markAsInProgress();
		responseRepository.saveAndFlush(response);

		log.info("Benchmark completado para lead autenticado. ID de respuesta: {}.", response.getId());

		UUID pdfJobId = pdfService.enqueuePdfGeneration(response.getId());

		// generar evento (luego del guardado de la respuesta y creación del trabajo de PDF en cola)
		// para que BenchmarkAiProcessor pueda generar un reporte haciendo uso del LLM
		eventPublisher.publishEvent(new BenchmarkCompletedEvent(response.getId()));

		log.info("Evento publicado para generar reporte mediante IA. ID de respuesta: {}", response.getId());

		return new CompleteBenchmarkResponse(
				response.getId(),
				response.getCompletedAt(),
				response.getGlobalScore(),
				response.getMaturityLevel(),
				getMaturityLabel(maturityLevel),
				response.getPercentile(),
				response.getCohortSize(),
				dimensionScore,
				pdfJobId
		);
	}

	@Override
	public CompleteBenchmarkResponse getResponse(String leadEmail, String responseId) {
		return null;
	}

	private int calculateMaturityLevel(BigDecimal score) {
		double s = score.doubleValue();
		if (s < 20) return 1;
		if (s < 40) return 2;
		if (s < 60) return 3;
		if (s < 80) return 4;
		return 5;
	}

	private String getMaturityLabel(int level) {
		return switch (level) {
			case 1 -> "Inicial";
			case 2 -> "Reactivo";
			case 3 -> "Gestionado";
			case 4 -> "Proactivo";
			case 5 -> "Optimizado";
			default -> "Desconocido";
		};
	}
}
