package com.university.administration.service;

import com.university.administration.domain.dto.EnrollmentRequestDTO;
import com.university.administration.domain.dto.EnrollmentResponseDTO;

import java.util.UUID;

public interface EnrollmentService {

    EnrollmentResponseDTO enroll(EnrollmentRequestDTO dto);

    void cancel(UUID id);
}

