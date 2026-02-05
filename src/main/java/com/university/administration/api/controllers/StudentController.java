package com.university.administration.api.controllers;

import com.university.administration.domain.dto.StudentRequestDTO;
import com.university.administration.domain.dto.StudentResponseDTO;
import com.university.administration.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<StudentResponseDTO> create(
            @Valid @RequestBody StudentRequestDTO request) {

        StudentResponseDTO response = studentService.create(request);

        return ResponseEntity
                .created(URI.create("/api/students/" + response.id()))
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody StudentRequestDTO request) {

        StudentResponseDTO response = studentService.update(id, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> findById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(studentService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> findAll() {

        return ResponseEntity.ok(studentService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {

        studentService.delete(id);

        return ResponseEntity.noContent().build();
    }
}

