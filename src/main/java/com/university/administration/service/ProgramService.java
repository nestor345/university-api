package com.university.administration.service;

import com.university.administration.domain.dto.ProgramRequestDTO;
import com.university.administration.domain.dto.ProgramResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ProgramService {

    ProgramResponseDTO create(ProgramRequestDTO request);

    ProgramResponseDTO findById(UUID id);

    List<ProgramResponseDTO> findAll();

    ProgramResponseDTO update(UUID id, ProgramRequestDTO request);

    void delete(UUID id);
}

