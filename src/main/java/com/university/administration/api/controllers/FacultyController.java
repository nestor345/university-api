package com.university.administration.api.controllers;

import com.university.administration.domain.dto.FacultyRequestDTO;
import com.university.administration.domain.dto.FacultyResponseDTO;
import com.university.administration.service.FacultyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/faculties")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public ResponseEntity<FacultyResponseDTO> create(
            @Valid @RequestBody FacultyRequestDTO request) {
        return ResponseEntity.ok(facultyService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacultyResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(facultyService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<FacultyResponseDTO>> findAll() {
        return ResponseEntity.ok(facultyService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        facultyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

