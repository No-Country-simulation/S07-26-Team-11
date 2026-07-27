package com.dcplatform.api.benchmark;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class BenchmarkRequest {
    @NotNull
    private Long operadorId;

    @NotNull
    private Map<String, Object> respuestasCuestionario;

    // Getters y Setters
    public Long getOperadorId() { return operadorId; }
    public void setOperadorId(Long operadorId) { this.operadorId = operadorId; }

    public Map<String, Object> getRespuestasCuestionario() { return respuestasCuestionario; }
    public void setRespuestasCuestionario(Map<String, Object> respuestasCuestionario) { this.respuestasCuestionario = respuestasCuestionario; }
}