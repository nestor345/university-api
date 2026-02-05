package com.university.administration.service;

import com.university.administration.domain.dto.StudentRequestDTO;
import com.university.administration.domain.dto.StudentResponseDTO;

import java.util.List;
import java.util.UUID;

public interface StudentService {

    StudentResponseDTO create(StudentRequestDTO request);

    StudentResponseDTO update(UUID id, StudentRequestDTO request);

    void delete(UUID id);

    StudentResponseDTO findById(UUID id);

    List<StudentResponseDTO> findAll();
}

