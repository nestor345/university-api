package com.university.administration.service.impl;

import com.university.administration.domain.dto.ProgramRequestDTO;
import com.university.administration.domain.dto.ProgramResponseDTO;
import com.university.administration.exceptions.ProgramNotFoundException;
import com.university.administration.infrastructure.persistence.entity.Faculty;
import com.university.administration.infrastructure.persistence.entity.Program;
import com.university.administration.infrastructure.persistence.repository.FacultyRepository;
import com.university.administration.infrastructure.persistence.repository.ProgramRepository;
import com.university.administration.service.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository programRepository;
    private final FacultyRepository facultyRepository;

    @Override
    public ProgramResponseDTO create(ProgramRequestDTO request) {

        Faculty faculty = facultyRepository.findById(request.facultyId())
                .orElseThrow(() ->
                        new RuntimeException("Faculty not found with id: " + request.facultyId())
                );

        Program program = new Program();
        program.setName(request.name());
        program.setFaculty(faculty);

        Program saved = programRepository.save(program);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramResponseDTO findById(UUID id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() ->
                        new ProgramNotFoundException("Program not found with id: " + id)
                );

        return mapToResponse(program);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramResponseDTO> findAll() {
        return programRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProgramResponseDTO update(UUID id, ProgramRequestDTO request) {

        Program program = programRepository.findById(id)
                .orElseThrow(() ->
                        new ProgramNotFoundException("Program not found with id: " + id)
                );

        Faculty faculty = facultyRepository.findById(request.facultyId())
                .orElseThrow(() ->
                        new RuntimeException("Faculty not found with id: " + request.facultyId())
                );

        program.setName(request.name());
        program.setFaculty(faculty);

        Program updated = programRepository.save(program);

        return mapToResponse(updated);
    }

    @Override
    public void delete(UUID id) {

        Program program = programRepository.findById(id)
                .orElseThrow(() ->
                        new ProgramNotFoundException("Program not found with id: " + id)
                );

        programRepository.delete(program);
    }

    private ProgramResponseDTO mapToResponse(Program program) {
        return new ProgramResponseDTO(
                program.getId(),
                program.getName(),
                program.getFaculty().getId(),
                program.getFaculty().getName()
        );
    }
}

