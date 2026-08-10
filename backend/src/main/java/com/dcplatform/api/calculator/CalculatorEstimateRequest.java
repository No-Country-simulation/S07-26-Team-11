package com.dcplatform.api.calculator;

import com.dcplatform.api.shared.ApiException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;

public record CalculatorEstimateRequest(

		@NotNull(message = "La capacidad instalada es obligatoria")
		@Positive(message = "La capacidad instalada debe ser mayor a 0")
		Double installedCapacityKw,

		@NotNull(message = "La capacidad utilizada es obligatoria")
		@PositiveOrZero(message = "La capacidad utilizada no puede ser negativa")
		Double usedCapacityKw,

		@NotNull(message = "La tarifa de electricidad es obligatoria")
		@PositiveOrZero(message = "La tarifa de electricidad no puede ser negativa")
		Double electricityRatePerKwh,

		@NotNull(message = "La cantidad de racks es obligatoria")
		@PositiveOrZero(message = "La cantidad de racks no puede ser negativa")
		Integer rackCount
) {

	// validación cruzada de negocio
	public CalculatorEstimateRequest {
		if (installedCapacityKw != null && usedCapacityKw != null) {
			if (usedCapacityKw > installedCapacityKw) {
				throw new ApiException(
						HttpStatus.BAD_REQUEST,
						"business-rule",
						"La capacidad utilizada (" + usedCapacityKw +
								" kW) no puede ser mayor a la capacidad instalada (" + installedCapacityKw + " kW)."
				);
			}
		}
	}
}