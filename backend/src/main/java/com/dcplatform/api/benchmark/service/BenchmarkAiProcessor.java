package com.dcplatform.api.benchmark.service;

import com.dcplatform.api.benchmark.agent.BenchmarkAiReportAgent;
import com.dcplatform.api.benchmark.agent.PromptContextBuilder;
import com.dcplatform.api.benchmark.agent.dto.AiReportResult;
import com.dcplatform.api.benchmark.model.BenchmarkAnswer;
import com.dcplatform.api.benchmark.model.BenchmarkOption;
import com.dcplatform.api.benchmark.model.BenchmarkResponse;
import com.dcplatform.api.benchmark.repository.BenchmarkAnswerRepository;
import com.dcplatform.api.benchmark.repository.BenchmarkOptionRepository;
import com.dcplatform.api.benchmark.repository.BenchmarkResponseRepository;
import com.dcplatform.api.shared.ApiException;
import com.dcplatform.api.shared.event.BenchmarkAnswersSavedEvent;
import com.dcplatform.api.shared.event.BenchmarkCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Component
public class BenchmarkAiProcessor {

	private static final Logger log = LoggerFactory.getLogger(BenchmarkAiProcessor.class);

	private final BenchmarkResponseRepository responseRepository;
	private final BenchmarkOptionRepository optionRepository;
	private final BenchmarkAnswerRepository answerRepository;
	private final BenchmarkAiReportAgent aiReportAgent;
	private final ApplicationEventPublisher eventPublisher;

	public BenchmarkAiProcessor(BenchmarkResponseRepository responseRepository,
	                            BenchmarkOptionRepository optionRepository,
	                            BenchmarkAnswerRepository answerRepository,
	                            BenchmarkAiReportAgent aiReportAgent,
	                            ApplicationEventPublisher eventPublisher) {
		this.responseRepository = responseRepository;
		this.optionRepository = optionRepository;
		this.answerRepository = answerRepository;
		this.aiReportAgent = aiReportAgent;
		this.eventPublisher = eventPublisher;
	}

	// esto se ejecuta en un hilo separado del pool de Spring
	// TransactionPhase.AFTER_COMMIT asegura que esto solo se ejecute si el insert principal fue exitoso
	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = BenchmarkAnswersSavedEvent.class)
	public void processAiReport(BenchmarkAnswersSavedEvent event) {
		UUID responseId = event.sourceId();
		log.info("Iniciando análisis asíncrono de IA para Benchmark ID: {}", responseId);

		try {
			BenchmarkResponse response = responseRepository.findById(responseId)
					.orElseThrow(() -> ApiException.notFound(
							"Respuestas del benchmark no encontradas para el ID: " + responseId));

			List<UUID> selectedOptions = answerRepository.findByResponseId(responseId)
					.stream()
					.map(BenchmarkAnswer::getOptionId)
					.toList();

			List<BenchmarkOption> options = optionRepository.findAllById(selectedOptions);

			// llamada a Gemini (si hay error 503, LangChain4J reintentará aquí sin bloquear transacciones de BD)
			String qaContext = PromptContextBuilder.buildQaContext(options);
			AiReportResult aiReport = aiReportAgent.generateReport(qaContext);
			response.setAiReportResult(aiReport);

			response.markAsCompleted();

			responseRepository.save(response);

			// publicar un evento para notificar que el análisis de IA ha finalizado
			// PdfJobEventListener escucha este evento y encola la generación de PDF
			eventPublisher.publishEvent(new BenchmarkCompletedEvent(responseId));

			log.info("Análisis de IA finalizado y guardado para Benchmark ID: {}", responseId);

		} catch (Exception e) {
			log.error("Fallo al generar reporte de IA para Benchmark ID: {}", responseId, e);
			log.info("Marcando el benchmark como fallido para futuro reintento. Benchmark ID: {}", responseId);

			BenchmarkResponse response = responseRepository.findById(responseId)
					.orElseThrow(() -> ApiException.notFound(
							"Respuestas del benchmark no encontradas para el ID: " + responseId));
			response.markAsAbandoned();
			responseRepository.save(response);
		}
	}
}
