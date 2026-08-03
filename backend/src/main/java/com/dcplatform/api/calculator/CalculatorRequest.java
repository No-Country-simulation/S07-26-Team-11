package com.dcplatform.api.calculator;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CalculatorRequest {

    @NotNull
    private BigDecimal cargaTermicaKw;

    // Getters y Setters
    public BigDecimal getCargaTermicaKw() {
        return cargaTermicaKw;
    }

    public void setCargaTermicaKw(BigDecimal cargaTermicaKw) {
        this.cargaTermicaKw = cargaTermicaKw;
    }
}