package com.clinic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "help_guides")
public class HelpGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "step_instructions", nullable = false, columnDefinition = "TEXT")
    private String stepInstructions;

    @Column(name = "design_constraints", nullable = false, columnDefinition = "TEXT")
    private String designConstraints;

    public HelpGuide() {}

    public HelpGuide(String stepInstructions, String designConstraints) {
        this.stepInstructions = stepInstructions;
        this.designConstraints = designConstraints;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStepInstructions() {
        return stepInstructions;
    }

    public void setStepInstructions(String stepInstructions) {
        this.stepInstructions = stepInstructions;
    }

    public String getDesignConstraints() {
        return designConstraints;
    }

    public void setDesignConstraints(String designConstraints) {
        this.designConstraints = designConstraints;
    }
}
