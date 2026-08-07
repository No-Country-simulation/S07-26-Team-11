package com.dcplatform.api.calculator;

import com.dcplatform.api.shared.ApiException;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;

public record CalculatorEstimateRequest(

		@NotNull(message = "La capacidad contratada es obligatoria")
		@Positive(message = "La capacidad contratada debe ser mayor a 0")
		Double contractedCapacityMw,

		@NotNull(message = "La capacidad utilizada es obligatoria")
		@PositiveOrZero(message = "La capacidad utilizada no puede ser negativa")
		Double utilizedCapacityMw,

		@NotNull(message = "El PUE es obligatorio")
		@DecimalMin(value = "1.0", message = "El PUE no puede ser menor al mínimo teórico de 1.0")
		Double pue,

		@NotNull(message = "El costo de energía es obligatorio")
		@PositiveOrZero(message = "El costo de energía no puede ser negativo")
		Double energyCostPerKwh,

		@NotNull(message = "La cantidad de racks es obligatoria")
		@PositiveOrZero(message = "La cantidad de racks no puede ser negativa")
		Integer rackCount,

		@NotNull(message = "La potencia de diseño por rack es obligatoria")
		@Positive(message = "La potencia de diseño debe ser mayor a 0")
		Double designPowerPerRackKw,

		@NotNull(message = "La potencia promedio por rack es obligatoria")
		@PositiveOrZero(message = "La potencia promedio no puede ser negativa")
		Double averagePowerPerRackKw,

		@NotBlank(message = "El código de moneda es obligatorio")
		@Pattern(regexp = "^[A-Z]{3}$",
				message = "La moneda debe seguir el estándar ISO 4217 (3 letras mayúsculas, ej: USD)")
		String currency,

		@NotBlank(message = "El código de país es obligatorio")
		@Pattern(regexp = "^[A-Z]{2}$",
				message = "El país debe seguir el estándar ISO 3166-1 alpha-2 (2 letras mayúsculas, ej: CO)")
		String country
) {

	// validación cruzada de negocio
	public CalculatorEstimateRequest {
		if (contractedCapacityMw != null && utilizedCapacityMw != null) {
			if (utilizedCapacityMw > contractedCapacityMw) {
				throw new ApiException(
						HttpStatus.BAD_REQUEST,
						"business-rule",
						"La capacidad utilizada (" + utilizedCapacityMw +
								" MW) no puede ser mayor a la capacidad contratada (" + contractedCapacityMw + " MW)."
				);
			}
		}

		if (averagePowerPerRackKw != null && designPowerPerRackKw != null) {
			if (averagePowerPerRackKw > designPowerPerRackKw) {
				throw new ApiException(
						HttpStatus.BAD_REQUEST,
						"business-rule",
						"La potencia promedio por rack no debería superar la de diseño."
				);
			}
		}
	}
}