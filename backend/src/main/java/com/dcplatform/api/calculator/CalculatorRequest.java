package com.dcplatform.api.calculator;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CalculatorRequest {

    @NotNull(message = "La carga térmica en kW es obligatoria")
    @Positive(message = "La carga térmica debe ser un valor numérico positivo")
    private Double cargaTermicaKw;

  
    public CalculatorRequest() {}

   
    public CalculatorRequest(Double cargaTermicaKw) {
        this.cargaTermicaKw = cargaTermicaKw;
    }

   
    public Double getCargaTermicaKw() {
        return cargaTermicaKw;
    }

    public void setCargaTermicaKw(Double cargaTermicaKw) {
        this.cargaTermicaKw = cargaTermicaKw;
    }
}