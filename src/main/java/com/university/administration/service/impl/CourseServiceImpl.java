package com.university.administration.service.impl;

import com.university.administration.domain.dto.CourseRequestDTO;
import com.university.administration.domain.dto.CourseResponseDTO;
import com.university.administration.exceptions.CourseNotFoundException;
import com.university.administration.exceptions.ProgramNotFoundException;
import com.university.administration.infrastructure.persistence.entity.Course;
import com.university.administration.infrastructure.persistence.entity.Program;
import com.university.administration.infrastructure.persistence.repository.CourseRepository;
import com.university.administration.infrastructure.persistence.repository.ProgramRepository;
import com.university.administration.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ProgramRepository programRepository;

    @Override
    public CourseResponseDTO create(CourseRequestDTO request) {

        Program program = programRepository.findById(request.programId())
                .orElseThrow(() ->
                        new ProgramNotFoundException("Program not found with id: " + request.programId())
                );

        Course course = Course.builder()
                .name(request.name())
                .credits(request.credits())
                .program(program)
                .build();

        Course saved = courseRepository.save(course);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO findById(UUID id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new CourseNotFoundException("Course not found with id: " + id)
                );

        return mapToResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> findAll() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CourseResponseDTO update(UUID id, CourseRequestDTO request) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new CourseNotFoundException("Course not found with id: " + id)
                );

        Program program = programRepository.findById(request.programId())
                .orElseThrow(() ->
                        new ProgramNotFoundException("Program not found with id: " + request.programId())
                );

        course.setName(request.name());
        course.setCredits(request.credits());
        course.setProgram(program);

        return mapToResponse(course);
    }

    @Override
    public void delete(UUID id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new CourseNotFoundException("Course not found with id: " + id)
                );

        courseRepository.delete(course);
    }

    private CourseResponseDTO mapToResponse(Course course) {
        return new CourseResponseDTO(
                course.getId(),
                course.getName(),
                course.getCredits(),
                course.getProgram().getId(),
                course.getProgram().getName()
        );
    }
}
