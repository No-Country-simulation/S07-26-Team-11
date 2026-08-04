package com.dcplatform.api.benchmark;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BenchmarkRequest(
        @NotNull(message = "El consumo energético es obligatorio")
        @Positive(message = "El consumo debe ser mayor a cero")
        Double energyConsumptionKw,

        @NotNull(message = "La eficiencia PUE es obligatoria")
        @Positive(message = "El PUE debe ser mayor a cero")
        Double pue,

        @NotNull(message = "La capacidad instalada es obligatoria")
        @Positive(message = "La capacidad debe ser mayor a cero")
        Double installedCapacityKw
) {}