package com.clinic.factory;

public class TreatmentCostCalculatorFactory {

    public static TreatmentCostCalculator getCalculator(String treatmentType) {
        if (treatmentType == null) {
            return new DefaultCalculator();
        }
        
        String cleanType = treatmentType.trim().toLowerCase();
        
        switch (cleanType) {
            case "cleaning":
                return new CleaningCalculator();
            case "filling":
                return new FillingCalculator();
            case "extraction":
                return new ExtractionCalculator();
            case "root canal":
            case "rootcanal":
                return new RootCanalCalculator();
            default:
                return new DefaultCalculator();
        }
    }
}
