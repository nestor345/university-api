package com.university.administration.service.impl;

import com.university.administration.domain.dto.FacultyRequestDTO;
import com.university.administration.domain.dto.FacultyResponseDTO;
import com.university.administration.infrastructure.persistence.entity.Faculty;
import com.university.administration.infrastructure.persistence.repository.FacultyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FacultyServiceImplTest {

    private FacultyRepository repository;
    private FacultyServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(FacultyRepository.class);
        service = new FacultyServiceImpl(repository);
    }

    @Test
    void shouldCreateFacultySuccessfully() {

        FacultyRequestDTO request = new FacultyRequestDTO(
                "Engineering",
                "Engineering Faculty"
        );

        Faculty saved = new Faculty();
        saved.setId(UUID.randomUUID());
        saved.setName("Engineering");
        saved.setDescription("Engineering Faculty");

        when(repository.save(any())).thenReturn(saved);

        FacultyResponseDTO response = service.create(request);

        assertThat(response.name()).isEqualTo("Engineering");
        assertThat(response.description()).isEqualTo("Engineering Faculty");

        verify(repository).save(any());
    }

    @Test
    void shouldFindByIdSuccessfully() {

        UUID id = UUID.randomUUID();

        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName("Science");
        faculty.setDescription("Science Faculty");

        when(repository.findById(id)).thenReturn(Optional.of(faculty));

        FacultyResponseDTO response = service.findById(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.name()).isEqualTo("Science");
    }

    @Test
    void shouldThrowWhenFacultyNotFound() {

        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Faculty not found");
    }

    @Test
    void shouldReturnAllFaculties() {

        Faculty faculty = new Faculty();
        faculty.setId(UUID.randomUUID());
        faculty.setName("Medicine");
        faculty.setDescription("Medical Faculty");

        when(repository.findAll()).thenReturn(List.of(faculty));

        List<FacultyResponseDTO> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Medicine");
    }

    @Test
    void shouldReturnEmptyListWhenNoFaculties() {

        when(repository.findAll()).thenReturn(List.of());

        List<FacultyResponseDTO> result = service.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteFaculty() {

        UUID id = UUID.randomUUID();

        service.delete(id);

        verify(repository).deleteById(id);
    }
}
