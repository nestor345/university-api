package com.university.administration.service;

import com.university.administration.domain.dto.AcademicPeriodCreateDTO;
import com.university.administration.domain.dto.AcademicPeriodResponseDTO;
import com.university.administration.infrastructure.persistence.entity.AcademicPeriod;

import java.util.UUID;

public interface AcademicPeriodService {

    AcademicPeriodResponseDTO create(AcademicPeriodCreateDTO dto);

    AcademicPeriodResponseDTO activate(UUID id);

    AcademicPeriod findActiveEntity();

    AcademicPeriod findEntity(UUID id);
}

