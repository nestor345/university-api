package com.university.administration.api.controllers;

import com.university.administration.domain.dto.CourseRequestDTO;
import com.university.administration.domain.dto.CourseResponseDTO;
import com.university.administration.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponseDTO create(@Valid @RequestBody CourseRequestDTO request) {
        return courseService.create(request);
    }

    @GetMapping("/{id}")
    public CourseResponseDTO findById(@PathVariable UUID id) {
        return courseService.findById(id);
    }

    @GetMapping
    public List<CourseResponseDTO> findAll() {
        return courseService.findAll();
    }

    @PutMapping("/{id}")
    public CourseResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody CourseRequestDTO request
    ) {
        return courseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        courseService.delete(id);
    }
}

