package com.dcplatform.api.calculator.model.dto;

import com.dcplatform.api.calculator.model.KpiCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resultado de una estimacion del calculator")
public record CalculatorEstimateResponse(
		@Schema(description = "Identificador de la estimacion guardada. Es null antes de desbloquearla", example = "8f7e6d5c-4b3a-2918-1706-5e4d3c2b1a09", nullable = true)
		String estimateId,
		@Schema(description = "Version del algoritmo utilizado", example = "1.0.0")
		String calculationVersion,
		@Schema(description = "Fecha de calculo en UTC", example = "2026-08-18T12:00:00Z")
		String createdAt,
		@Schema(description = "Indica si los KPI de costo estan desbloqueados", example = "false")
		boolean unlocked,
		@Schema(description = "Lista de KPI visibles para esta estimacion")
		List<KpiResult> kpis,
		@Schema(description = "Cantidad de KPI ocultos hasta desbloquear los resultados", example = "2")
		int lockedKpiCount
) {
	@Schema(description = "KPI calculado y su unidad")
	public record KpiResult(
			@Schema(description = "Codigo estable del KPI", example = "IDLE_CAPACITY_KW")
			KpiCode code,
			@Schema(description = "Nombre legible del KPI", example = "Capacidad subutilizada estimada")
			String label,
			@Schema(description = "Valor numerico del KPI", example = "1240.0")
			Double value,
			@Schema(description = "Unidad del valor")
			KpiCode.UnitType unit
	) {
	}
}