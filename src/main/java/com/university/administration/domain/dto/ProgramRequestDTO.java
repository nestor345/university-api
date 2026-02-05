package com.university.administration.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProgramRequestDTO(

        @NotBlank
        String name,

        @NotNull
        UUID facultyId

) {}
