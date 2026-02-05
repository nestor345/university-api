package com.university.administration.api.controllers;

import com.university.administration.domain.dto.ProgramRequestDTO;
import com.university.administration.domain.dto.ProgramResponseDTO;
import com.university.administration.service.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProgramResponseDTO create(@Valid @RequestBody ProgramRequestDTO request) {
        return programService.create(request);
    }

    @GetMapping("/{id}")
    public ProgramResponseDTO findById(@PathVariable UUID id) {
        return programService.findById(id);
    }

    @GetMapping
    public List<ProgramResponseDTO> findAll() {
        return programService.findAll();
    }

    @PutMapping("/{id}")
    public ProgramResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody ProgramRequestDTO request
    ) {
        return programService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        programService.delete(id);
    }
}

