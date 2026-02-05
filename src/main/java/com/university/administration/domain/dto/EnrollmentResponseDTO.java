package com.university.administration.domain.dto;

import com.university.administration.domain.enums.EnrollmentStatus;

import java.time.Instant;
import java.util.UUID;

public record EnrollmentResponseDTO(

        UUID id,
        UUID studentId,
        UUID offeringId,
        String courseName,
        EnrollmentStatus status,
        Instant enrolledAt

) {}


