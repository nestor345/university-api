package com.university.administration.service;

import com.university.administration.domain.dto.FacultyRequestDTO;
import com.university.administration.domain.dto.FacultyResponseDTO;

import java.util.List;
import java.util.UUID;

public interface FacultyService {

    FacultyResponseDTO create(FacultyRequestDTO request);

    FacultyResponseDTO findById(UUID id);

    List<FacultyResponseDTO> findAll();

    void delete(UUID id);
}

