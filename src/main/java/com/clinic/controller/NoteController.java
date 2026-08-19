package com.clinic.controller;

import com.clinic.dao.NoteRepository;
import com.clinic.model.Note;
import com.clinic.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteRepository noteRepository;

    @Autowired
    public NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @GetMapping
    public ResponseEntity<?> getNotes(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        List<Note> notes = noteRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(notes);
    }

    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody Note tempNote, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        String senderRole = user.getRole();
        String receiverRole = "";
        
        if ("DENTIST".equalsIgnoreCase(senderRole)) {
            receiverRole = "RECEPTIONIST";
        } else if ("RECEPTIONIST".equalsIgnoreCase(senderRole)) {
            receiverRole = "DENTIST";
        } else {
            receiverRole = "ALL";
        }

        Note note = new Note(
                user.getUsername(),
                user.getFullname(),
                user.getRole(),
                receiverRole,
                tempNote.getContent()
        );

        Note saved = noteRepository.save(note);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLikeNote(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        Optional<Note> noteOpt = noteRepository.findById(id);
        if (noteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }

        Note note = noteOpt.get();
        note.setLiked(!note.isLiked());
        Note saved = noteRepository.save(note);
        return ResponseEntity.ok(saved);
    }
}
