package com.clinic.controller;

import com.clinic.dao.HelpGuideRepository;
import com.clinic.model.HelpGuide;
import com.clinic.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/help-guide")
public class HelpGuideController {

    private final HelpGuideRepository helpGuideRepository;

    @Autowired
    public HelpGuideController(HelpGuideRepository helpGuideRepository) {
        this.helpGuideRepository = helpGuideRepository;
    }

    @GetMapping
    public ResponseEntity<?> getHelpGuide() {
        List<HelpGuide> guides = helpGuideRepository.findAll();
        if (guides.isEmpty()) {
            return ResponseEntity.ok(new HelpGuide("No step-by-step instructions loaded.", "No design constraints loaded."));
        }
        return ResponseEntity.ok(guides.get(0));
    }

    @PutMapping
    public ResponseEntity<?> updateHelpGuide(@RequestBody HelpGuide updatedGuide, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        if (!"DENTIST".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Dentists are allowed to edit the Help Guide");
        }

        List<HelpGuide> guides = helpGuideRepository.findAll();
        HelpGuide guideToSave;
        if (guides.isEmpty()) {
            guideToSave = updatedGuide;
        } else {
            guideToSave = guides.get(0);
            guideToSave.setStepInstructions(updatedGuide.getStepInstructions());
            guideToSave.setDesignConstraints(updatedGuide.getDesignConstraints());
        }

        HelpGuide saved = helpGuideRepository.save(guideToSave);
        return ResponseEntity.ok(saved);
    }
}
