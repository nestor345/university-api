package com.university.administration.domain.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AcademicPeriodResponseDTO(

        UUID id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        boolean active

) {}

