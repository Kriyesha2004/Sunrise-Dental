package com.clinic.factory;

import java.math.BigDecimal;

public class FillingCalculator implements TreatmentCostCalculator {
    @Override
    public BigDecimal calculateCost() {
        return new BigDecimal("80.00");
    }
}
