package com.clinic.factory;

import java.math.BigDecimal;

public class ExtractionCalculator implements TreatmentCostCalculator {
    @Override
    public BigDecimal calculateCost() {
        return new BigDecimal("120.00");
    }
}
