package com.university.administration.domain.dto;

import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

public record StudentResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String documentNumber,
        LocalDate birthDate,
        String phone,
        String address,
        Instant createdAt
) {}

