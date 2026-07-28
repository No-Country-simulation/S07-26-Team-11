package com.dcplatform.api.calculator;

import java.math.BigDecimal;

public class CalculatorResponse {
    private BigDecimal strandedCapacityKw;
    private BigDecimal efficiencyLossPct;

    public CalculatorResponse(BigDecimal strandedCapacityKw, BigDecimal efficiencyLossPct) {
        this.strandedCapacityKw = strandedCapacityKw;
        this.efficiencyLossPct = efficiencyLossPct;
    }

    // Getters y Setters
    public BigDecimal getStrandedCapacityKw() { return strandedCapacityKw; }
    public void setStrandedCapacityKw(BigDecimal strandedCapacityKw) { this.strandedCapacityKw = strandedCapacityKw; }

    public BigDecimal getEfficiencyLossPct() { return efficiencyLossPct; }
    public void setEfficiencyLossPct(BigDecimal efficiencyLossPct) { this.efficiencyLossPct = efficiencyLossPct; }
}