package com.clinic.factory;

import java.math.BigDecimal;

public class CleaningCalculator implements TreatmentCostCalculator {
    @Override
    public BigDecimal calculateCost() {
        return new BigDecimal("50.00");
    }
}
