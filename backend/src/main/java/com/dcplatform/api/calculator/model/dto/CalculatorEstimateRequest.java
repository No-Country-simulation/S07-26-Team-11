package com.dcplatform.api.calculator.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Entradas para estimar capacidad y costos de energia")
public record CalculatorEstimateRequest(

		@NotNull(message = "La capacidad instalada es obligatoria")
		@Positive(message = "La capacidad instalada debe ser mayor a 0")
		@Schema(description = "Capacidad electrica total instalada", example = "2000.0", minimum = "0", exclusiveMinimum = true)
		Double installedCapacityKw,

		@NotNull(message = "La capacidad utilizada es obligatoria")
		@PositiveOrZero(message = "La capacidad utilizada no puede ser negativa")
		@Schema(description = "Capacidad actualmente utilizada. No puede superar la capacidad instalada", example = "760.0", minimum = "0")
		Double usedCapacityKw,

		@NotNull(message = "La tarifa de electricidad es obligatoria")
		@PositiveOrZero(message = "La tarifa de electricidad no puede ser negativa")
		@Schema(description = "Tarifa de electricidad por kWh", example = "0.12", minimum = "0")
		Double electricityRatePerKwh,

		@NotNull(message = "La cantidad de racks es obligatoria")
		@PositiveOrZero(message = "La cantidad de racks no puede ser negativa")
		@Schema(description = "Cantidad de racks del data center", example = "48", minimum = "0")
		Integer rackCount
) {
}