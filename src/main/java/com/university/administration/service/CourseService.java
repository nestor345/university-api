package com.university.administration.service;

import com.university.administration.domain.dto.CourseRequestDTO;
import com.university.administration.domain.dto.CourseResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CourseService {

    CourseResponseDTO create(CourseRequestDTO request);

    CourseResponseDTO findById(UUID id);

    List<CourseResponseDTO> findAll();

    CourseResponseDTO update(UUID id, CourseRequestDTO request);

    void delete(UUID id);
}

