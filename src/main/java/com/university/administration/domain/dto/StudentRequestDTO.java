package com.university.administration.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record StudentRequestDTO(

        @NotNull UUID userId,
        @NotNull UUID programId,

        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String documentNumber,

        @NotNull LocalDate birthDate,

        String phone,
        String address
) {}

