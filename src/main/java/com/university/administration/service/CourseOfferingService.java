package com.university.administration.service;

import com.university.administration.domain.dto.CourseOfferingCreateDTO;
import com.university.administration.domain.dto.CourseOfferingResponseDTO;
import com.university.administration.infrastructure.persistence.entity.CourseOffering;

import java.util.UUID;

public interface CourseOfferingService {

    CourseOfferingResponseDTO create(CourseOfferingCreateDTO dto);

    CourseOffering findEntity(UUID id);
}

