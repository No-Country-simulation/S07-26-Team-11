package com.dcplatform.api.benchmark;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BenchmarkRequest(
        @NotNull(value = "El consumo energético es obligatorio")
        @Positive(value = "El consumo debe ser mayor a cero")
        Double energyConsumptionKw,

        @NotNull(value = "La eficiencia PUE es obligatoria")
        @Positive(value = "El PUE debe ser mayor a cero")
        Double pue,

        @NotNull(value = "La capacidad instalada es obligatoria")
        @Positive(value = "La capacidad debe ser mayor a cero")
        Double installedCapacityKw
) {}