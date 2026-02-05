package com.university.administration.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record FacultyRequestDTO(
        @NotBlank String name,
        String description
) {}

