package com.university.administration.service.impl;

import com.university.administration.domain.dto.FacultyRequestDTO;
import com.university.administration.domain.dto.FacultyResponseDTO;
import com.university.administration.infrastructure.persistence.entity.Faculty;
import com.university.administration.infrastructure.persistence.repository.FacultyRepository;
import com.university.administration.service.FacultyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Override
    public FacultyResponseDTO create(FacultyRequestDTO request) {

        Faculty faculty = new Faculty();
        faculty.setName(request.name());
        faculty.setDescription(request.description());

        Faculty saved = facultyRepository.save(faculty);

        return new FacultyResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getDescription()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public FacultyResponseDTO findById(UUID id) {

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        return new FacultyResponseDTO(
                faculty.getId(),
                faculty.getName(),
                faculty.getDescription()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacultyResponseDTO> findAll() {
        return facultyRepository.findAll()
                .stream()
                .map(f -> new FacultyResponseDTO(
                        f.getId(),
                        f.getName(),
                        f.getDescription()))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        facultyRepository.deleteById(id);
    }
}

