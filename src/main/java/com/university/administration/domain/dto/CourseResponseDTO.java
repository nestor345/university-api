package com.university.administration.domain.dto;

import java.util.UUID;

public record CourseResponseDTO(
        UUID id,
        String name,
        Integer credits,
        UUID programId,
        String programName
) {}

