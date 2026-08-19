package com.dcplatform.api.calculator.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Codigos de los indicadores devueltos por la estimacion")
public enum KpiCode {
	IDLE_CAPACITY_KW,
	IDLE_CAPACITY_RATIO,
	IDLE_CAPACITY_ANNUAL_COST,
	IDLE_COST_MONTHLY,
	IDLE_COST_3Y_PROJECTION,
	IDLE_COST_PER_RACK_ANNUAL;

	@Schema(description = "Unidad en la que se expresa el valor del KPI")
	public enum UnitType {
		KW,
		RATIO,
		CURRENCY
	}
}