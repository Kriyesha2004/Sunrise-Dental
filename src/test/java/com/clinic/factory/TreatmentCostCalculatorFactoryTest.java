package com.clinic.factory;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class TreatmentCostCalculatorFactoryTest {

    @Test
    public void testCleaningCalculator() {
        TreatmentCostCalculator calculator = TreatmentCostCalculatorFactory.getCalculator("Cleaning");
        assertTrue(calculator instanceof CleaningCalculator);
        assertEquals(new BigDecimal("50.00"), calculator.calculateCost());
    }

    @Test
    public void testFillingCalculator() {
        TreatmentCostCalculator calculator = TreatmentCostCalculatorFactory.getCalculator("Filling");
        assertTrue(calculator instanceof FillingCalculator);
        assertEquals(new BigDecimal("80.00"), calculator.calculateCost());
    }

    @Test
    public void testExtractionCalculator() {
        TreatmentCostCalculator calculator = TreatmentCostCalculatorFactory.getCalculator("Extraction");
        assertTrue(calculator instanceof ExtractionCalculator);
        assertEquals(new BigDecimal("120.00"), calculator.calculateCost());
    }

    @Test
    public void testRootCanalCalculator() {
        TreatmentCostCalculator calculator = TreatmentCostCalculatorFactory.getCalculator("Root Canal");
        assertTrue(calculator instanceof RootCanalCalculator);
        assertEquals(new BigDecimal("300.00"), calculator.calculateCost());
    }

    @Test
    public void testDefaultCalculator() {
        TreatmentCostCalculator calculatorNull = TreatmentCostCalculatorFactory.getCalculator(null);
        assertTrue(calculatorNull instanceof DefaultCalculator);
        assertEquals(new BigDecimal("50.00"), calculatorNull.calculateCost());

        TreatmentCostCalculator calculatorUnknown = TreatmentCostCalculatorFactory.getCalculator("Consultation Only");
        assertTrue(calculatorUnknown instanceof DefaultCalculator);
        assertEquals(new BigDecimal("50.00"), calculatorUnknown.calculateCost());
    }
}
