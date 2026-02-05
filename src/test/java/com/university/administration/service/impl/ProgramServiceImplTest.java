package com.university.administration.service.impl;

import com.university.administration.domain.dto.ProgramRequestDTO;
import com.university.administration.domain.dto.ProgramResponseDTO;
import com.university.administration.exceptions.ProgramNotFoundException;
import com.university.administration.infrastructure.persistence.entity.Faculty;
import com.university.administration.infrastructure.persistence.entity.Program;
import com.university.administration.infrastructure.persistence.repository.FacultyRepository;
import com.university.administration.infrastructure.persistence.repository.ProgramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProgramServiceImplTest {

    private ProgramRepository programRepository;
    private FacultyRepository facultyRepository;
    private ProgramServiceImpl service;

    @BeforeEach
    void setUp() {
        programRepository = mock(ProgramRepository.class);
        facultyRepository = mock(FacultyRepository.class);
        service = new ProgramServiceImpl(programRepository, facultyRepository);
    }

    private Faculty buildFaculty(UUID id) {
        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName("Engineering");
        return faculty;
    }

    private Program buildProgram(UUID id, Faculty faculty) {
        Program program = new Program();
        program.setId(id);
        program.setName("Systems");
        program.setFaculty(faculty);
        return program;
    }

    @Test
    void shouldCreateProgramSuccessfully() {

        UUID facultyId = UUID.randomUUID();
        Faculty faculty = buildFaculty(facultyId);

        when(facultyRepository.findById(facultyId)).thenReturn(Optional.of(faculty));

        Program saved = buildProgram(UUID.randomUUID(), faculty);
        when(programRepository.save(any())).thenReturn(saved);

        ProgramRequestDTO request = new ProgramRequestDTO("Systems", facultyId);

        ProgramResponseDTO response = service.create(request);

        assertThat(response.name()).isEqualTo("Systems");
        assertThat(response.facultyId()).isEqualTo(facultyId);

        verify(programRepository).save(any());
    }

    @Test
    void shouldThrowWhenFacultyNotFoundOnCreate() {

        UUID facultyId = UUID.randomUUID();
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.empty());

        ProgramRequestDTO request = new ProgramRequestDTO("Systems", facultyId);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Faculty not found with id: " + facultyId);
    }

    @Test
    void shouldFindByIdSuccessfully() {

        UUID id = UUID.randomUUID();
        Faculty faculty = buildFaculty(UUID.randomUUID());
        Program program = buildProgram(id, faculty);

        when(programRepository.findById(id)).thenReturn(Optional.of(program));

        ProgramResponseDTO response = service.findById(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.facultyName()).isEqualTo("Engineering");
    }

    @Test
    void shouldThrowWhenProgramNotFoundOnFindById() {

        UUID id = UUID.randomUUID();
        when(programRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(ProgramNotFoundException.class)
                .hasMessage("Program not found with id: " + id);
    }

    @Test
    void shouldReturnAllPrograms() {

        Faculty faculty = buildFaculty(UUID.randomUUID());
        Program program = buildProgram(UUID.randomUUID(), faculty);

        when(programRepository.findAll()).thenReturn(List.of(program));

        List<ProgramResponseDTO> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Systems");
    }

    @Test
    void shouldReturnEmptyListWhenNoPrograms() {

        when(programRepository.findAll()).thenReturn(List.of());

        List<ProgramResponseDTO> result = service.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateSuccessfully() {

        UUID id = UUID.randomUUID();
        UUID facultyId = UUID.randomUUID();

        Faculty faculty = buildFaculty(facultyId);
        Program program = buildProgram(id, faculty);

        when(programRepository.findById(id)).thenReturn(Optional.of(program));
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.of(faculty));
        when(programRepository.save(any())).thenReturn(program);

        ProgramRequestDTO request = new ProgramRequestDTO("Updated", facultyId);

        ProgramResponseDTO response = service.update(id, request);

        assertThat(response.name()).isEqualTo("Updated");
    }

    @Test
    void shouldThrowWhenProgramNotFoundOnUpdate() {

        UUID id = UUID.randomUUID();
        when(programRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.update(id, new ProgramRequestDTO("X", UUID.randomUUID()))
        ).isInstanceOf(ProgramNotFoundException.class);
    }

    @Test
    void shouldThrowWhenFacultyNotFoundOnUpdate() {

        UUID id = UUID.randomUUID();
        UUID facultyId = UUID.randomUUID();

        Program program = mock(Program.class);

        when(programRepository.findById(id)).thenReturn(Optional.of(program));
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.update(id, new ProgramRequestDTO("X", facultyId))
        ).isInstanceOf(RuntimeException.class)
                .hasMessage("Faculty not found with id: " + facultyId);
    }

    @Test
    void shouldDeleteSuccessfully() {

        UUID id = UUID.randomUUID();
        Program program = mock(Program.class);

        when(programRepository.findById(id)).thenReturn(Optional.of(program));

        service.delete(id);

        verify(programRepository).delete(program);
    }

    @Test
    void shouldThrowWhenProgramNotFoundOnDelete() {

        UUID id = UUID.randomUUID();
        when(programRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(ProgramNotFoundException.class)
                .hasMessage("Program not found with id: " + id);
    }
}
