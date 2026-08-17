package com.clinic.factory;

import java.math.BigDecimal;

public class RootCanalCalculator implements TreatmentCostCalculator {
    @Override
    public BigDecimal calculateCost() {
        return new BigDecimal("300.00");
    }
}
