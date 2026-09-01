package com.dcplatform.api.benchmark.agent;

import com.dcplatform.api.benchmark.agent.dto.AiReportResult;
import com.dcplatform.api.benchmark.model.BenchmarkAnswer;
import com.dcplatform.api.benchmark.model.BenchmarkOption;
import com.dcplatform.api.benchmark.model.BenchmarkResponse;
import com.dcplatform.api.benchmark.repository.BenchmarkResponseRepository;
import com.dcplatform.api.shared.ApiException;
import com.dcplatform.api.shared.event.BenchmarkCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BenchmarkAiProcessor {

	private static final Logger log = LoggerFactory.getLogger(BenchmarkAiProcessor.class);

	private final BenchmarkAiReportAgent aiAgent;
	private final BenchmarkResponseRepository responseRepository;

	public BenchmarkAiProcessor(
			BenchmarkAiReportAgent aiAgent,
			BenchmarkResponseRepository responseRepository) {
		this.aiAgent = aiAgent;
		this.responseRepository = responseRepository;
	}

	/**
	 * @Async envía la ejecución a un ThreadPool (no bloquea el request HTTP).
	 * @TransactionalEventListener garantiza que esto solo se ejecute si la
	 * transacción original de completeBenchmark() hizo commit exitosamente.
	 */
	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void processAiReportAndEnqueuePdf(BenchmarkCompletedEvent event) {
		log.info("Iniciando procesamiento de IA para sesión: {}", event.responseId());

		try {
			BenchmarkResponse response = responseRepository.findById(event.responseId())
					.orElseThrow(() -> ApiException.notFound(
							"No existe una respuesta de benchmark con el ID " + event.responseId() + "."
					));

			// extraer las opciones para el contexto de la IA
			List<BenchmarkOption> selectedOptions = response.getAnswers().stream()
					.map(BenchmarkAnswer::getOption)
					.collect(Collectors.toList());

			// se construye el contexto y se invoca al agente para generar el reporte
			// la llamada al LLM es una operación que puede demorar varios segundos
			String qaContext = PromptContextBuilder.buildQaContext(selectedOptions);
			AiReportResult aiResult = aiAgent.generateReport(qaContext);

			response.setAiReportResult(aiResult);
			response.markAsCompleted();
			responseRepository.saveAndFlush(response);

			log.info("Reporte de IA generado y guardado para Benchmark ID: {}", event.responseId());

		} catch (Exception e) {
			log.error("Fallo al generar reporte de IA para Benchmark ID: {}", event.responseId(), e);

			BenchmarkResponse response = responseRepository.findById(event.responseId())
					.orElseThrow(() -> ApiException.notFound(
							"Respuestas del benchmark no encontradas para el ID: " + event.responseId()
					));

			AiReportResult fallbackAiReport = new AiReportResult(
					"El análisis detallado mediante IA se encuentra temporalmente fuera de servicio.",
					List.of("Revisa los puntajes detallados en la tabla de dimensiones.")
			);

			response.setAiReportResult(fallbackAiReport);
			response.markAsCompleted();
			responseRepository.saveAndFlush(response);

			log.info("Reporte no disponible. Se ha aplicado el fallback para Benchmark ID: {}", event.responseId());
		}
	}
}
