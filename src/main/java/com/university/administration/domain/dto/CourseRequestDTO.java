package com.university.administration.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CourseRequestDTO(

        @NotBlank
        String name,

        @NotNull
        @Positive
        Integer credits,

        @NotNull
        UUID programId

) {}

