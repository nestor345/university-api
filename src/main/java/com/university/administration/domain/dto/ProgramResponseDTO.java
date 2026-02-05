package com.university.administration.domain.dto;

import java.util.UUID;

public record ProgramResponseDTO(
        UUID id,
        String name,
        UUID facultyId,
        String facultyName
) {}
