package com.dcplatform.api.calculator;

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
			String code,
			String label,
			Double value,
			String unit,
			Double benchmarkMedian
	) {
	}
}