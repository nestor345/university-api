package com.university.administration.service.impl;

import com.university.administration.domain.dto.CourseRequestDTO;
import com.university.administration.domain.dto.CourseResponseDTO;
import com.university.administration.exceptions.CourseNotFoundException;
import com.university.administration.exceptions.ProgramNotFoundException;
import com.university.administration.infrastructure.persistence.entity.Course;
import com.university.administration.infrastructure.persistence.entity.Program;
import com.university.administration.infrastructure.persistence.repository.CourseRepository;
import com.university.administration.infrastructure.persistence.repository.ProgramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceImplTest {

    private CourseRepository courseRepository;
    private ProgramRepository programRepository;
    private CourseServiceImpl service;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        programRepository = mock(ProgramRepository.class);
        service = new CourseServiceImpl(courseRepository, programRepository);
    }

    private CourseRequestDTO buildRequest(UUID programId) {
        return new CourseRequestDTO("Math", 4, programId);
    }

    @Test
    void shouldCreateCourseSuccessfully() {

        UUID programId = UUID.randomUUID();
        Program program = Program.builder()
                .id(programId)
                .name("Engineering")
                .build();

        Course course = Course.builder()
                .id(UUID.randomUUID())
                .name("Math")
                .credits(4)
                .program(program)
                .build();

        when(programRepository.findById(programId)).thenReturn(Optional.of(program));
        when(courseRepository.save(any())).thenReturn(course);

        CourseResponseDTO response = service.create(buildRequest(programId));

        assertThat(response.name()).isEqualTo("Math");
        assertThat(response.programName()).isEqualTo("Engineering");

        verify(courseRepository).save(any());
    }

    @Test
    void shouldThrowWhenProgramNotFoundOnCreate() {

        UUID programId = UUID.randomUUID();
        when(programRepository.findById(programId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(buildRequest(programId)))
                .isInstanceOf(ProgramNotFoundException.class)
                .hasMessage("Program not found with id: " + programId);
    }

    @Test
    void shouldFindByIdSuccessfully() {

        UUID id = UUID.randomUUID();

        Program program = Program.builder()
                .id(UUID.randomUUID())
                .name("Engineering")
                .build();

        Course course = Course.builder()
                .id(id)
                .name("Physics")
                .credits(3)
                .program(program)
                .build();

        when(courseRepository.findById(id)).thenReturn(Optional.of(course));

        CourseResponseDTO response = service.findById(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.programName()).isEqualTo("Engineering");
    }

    @Test
    void shouldThrowWhenCourseNotFoundOnFindById() {

        UUID id = UUID.randomUUID();
        when(courseRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessage("Course not found with id: " + id);
    }

    @Test
    void shouldReturnAllCourses() {

        Program program = Program.builder()
                .id(UUID.randomUUID())
                .name("Engineering")
                .build();

        Course course = Course.builder()
                .id(UUID.randomUUID())
                .name("Algebra")
                .credits(5)
                .program(program)
                .build();

        when(courseRepository.findAll()).thenReturn(List.of(course));

        List<CourseResponseDTO> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Algebra");
    }

    @Test
    void shouldReturnEmptyListWhenNoCourses() {

        when(courseRepository.findAll()).thenReturn(List.of());

        List<CourseResponseDTO> result = service.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateSuccessfully() {

        UUID id = UUID.randomUUID();
        UUID programId = UUID.randomUUID();

        Program program = Program.builder()
                .id(programId)
                .name("Science")
                .build();

        Course course = Course.builder()
                .id(id)
                .name("Old")
                .credits(2)
                .program(program)
                .build();

        when(courseRepository.findById(id)).thenReturn(Optional.of(course));
        when(programRepository.findById(programId)).thenReturn(Optional.of(program));

        CourseRequestDTO request = new CourseRequestDTO("New", 6, programId);

        CourseResponseDTO response = service.update(id, request);

        assertThat(response.name()).isEqualTo("New");
        assertThat(response.credits()).isEqualTo(6);
    }

    @Test
    void shouldThrowWhenCourseNotFoundOnUpdate() {

        UUID id = UUID.randomUUID();
        when(courseRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.update(id, buildRequest(UUID.randomUUID()))
        ).isInstanceOf(CourseNotFoundException.class);
    }

    @Test
    void shouldThrowWhenProgramNotFoundOnUpdate() {

        UUID id = UUID.randomUUID();
        UUID programId = UUID.randomUUID();

        Course course = mock(Course.class);

        when(courseRepository.findById(id)).thenReturn(Optional.of(course));
        when(programRepository.findById(programId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.update(id, buildRequest(programId))
        ).isInstanceOf(ProgramNotFoundException.class);
    }

    @Test
    void shouldDeleteSuccessfully() {

        UUID id = UUID.randomUUID();
        Course course = mock(Course.class);

        when(courseRepository.findById(id)).thenReturn(Optional.of(course));

        service.delete(id);

        verify(courseRepository).delete(course);
    }

    @Test
    void shouldThrowWhenCourseNotFoundOnDelete() {

        UUID id = UUID.randomUUID();
        when(courseRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessage("Course not found with id: " + id);
    }
}
