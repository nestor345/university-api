package com.university.administration.domain.dto;

import java.util.UUID;

public record FacultyResponseDTO(
        UUID id,
        String name,
        String description
) {}

