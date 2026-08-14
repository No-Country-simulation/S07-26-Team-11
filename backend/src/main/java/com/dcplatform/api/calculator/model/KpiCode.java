package com.dcplatform.api.calculator.model;

public enum KpiCode {
	IDLE_CAPACITY_KW,
	IDLE_CAPACITY_RATIO,
	IDLE_CAPACITY_ANNUAL_COST,
	IDLE_COST_MONTHLY,
	IDLE_COST_3Y_PROJECTION,
	IDLE_COST_PER_RACK_ANNUAL;

	public enum UnitType {
		KW,
		RATIO,
		CURRENCY
	}
}