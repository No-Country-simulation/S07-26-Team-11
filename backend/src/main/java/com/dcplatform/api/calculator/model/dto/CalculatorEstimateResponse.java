package com.dcplatform.api.calculator.model.dto;

import com.dcplatform.api.calculator.model.KpiCode;

import java.util.List;

public record CalculatorEstimateResponse(
		String estimateId,
		String calculationVersion,
		String createdAt,
		boolean unlocked,
		List<KpiResult> kpis,
		int lockedKpiCount
) {
	public record KpiResult(
			KpiCode code,
			String label,
			Double value,
			KpiCode.UnitType unit
	) {
	}
}