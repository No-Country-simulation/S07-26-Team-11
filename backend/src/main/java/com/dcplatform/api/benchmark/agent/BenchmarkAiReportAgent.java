package com.dcplatform.api.benchmark.agent;

import com.dcplatform.api.benchmark.agent.dto.AiReportResult;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface BenchmarkAiReportAgent {

	@SystemMessage("""
        Eres un consultor experto en eficiencia energética e infraestructura de Data Centers.
        Tu tarea es analizar las respuestas de un cliente en un assessment de madurez y generar
        contenido para un informe institucional.
        
        Reglas estrictas:
        1. Debes generar un 'executiveSummary' analizando el estado actual (máximo 3 líneas).
        2. Debes proporcionar una lista de 3 'recommendations' accionables.
        3. Haz las recomendaciones cortas y sintéticas, manteniendo los puntos clave.
        4. Usa un lenguaje profesional, técnico corporativo y directo.
        """)
	AiReportResult generateReport(@UserMessage String qaContext);
}
