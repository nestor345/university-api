package com.university.administration.service.impl;

import com.university.administration.domain.dto.StudentRequestDTO;
import com.university.administration.domain.dto.StudentResponseDTO;
import com.university.administration.exceptions.StudentNotFoundException;
import com.university.administration.infrastructure.persistence.entity.Program;
import com.university.administration.infrastructure.persistence.entity.Student;
import com.university.administration.infrastructure.persistence.entity.User;
import com.university.administration.infrastructure.persistence.repository.ProgramRepository;
import com.university.administration.infrastructure.persistence.repository.StudentRepository;
import com.university.administration.infrastructure.persistence.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProgramRepository programRepository;

    @InjectMocks
    private StudentServiceImpl service;

    private UUID studentId;
    private UUID userId;
    private UUID programId;

    @BeforeEach
    void setup() {
        studentId = UUID.randomUUID();
        userId = UUID.randomUUID();
        programId = UUID.randomUUID();
    }

    private StudentRequestDTO request() {
        return new StudentRequestDTO(
                userId,
                programId,
                "John",
                "Doe",
                "123456",
                LocalDate.of(2000, 1, 1),
                "3001234567",
                "Street 123"
        );
    }

    private Student studentEntity() {
        return Student.builder()
                .id(studentId)
                .firstName("John")
                .lastName("Doe")
                .documentNumber("123456")
                .birthDate(LocalDate.of(2000, 1, 1))
                .phone("3001234567")
                .address("Street 123")
                .createdAt(Instant.now())
                .build();
    }


    @Test
    void shouldCreateStudent() {

        User user = new User();
        Program program = new Program();
        Student student = studentEntity();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(programRepository.findById(programId)).thenReturn(Optional.of(program));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentResponseDTO response = service.create(request());

        assertThat(response.firstName()).isEqualTo("John");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void shouldThrowWhenUserNotFound() {

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void shouldThrowWhenProgramNotFound() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(programRepository.findById(programId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Program not found");
    }


    @Test
    void shouldUpdateStudent() {

        Student student = studentEntity();

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));
        when(studentRepository.save(student))
                .thenReturn(student);

        StudentResponseDTO response =
                service.update(studentId, request());

        assertThat(response.firstName()).isEqualTo("John");
        verify(studentRepository).save(student);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingStudent() {

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.update(studentId, request()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Student not found");
    }


    @Test
    void shouldDeleteStudent() {

        Student student = studentEntity();

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));

        service.delete(studentId);

        verify(studentRepository).delete(student);
    }

    @Test
    void shouldThrowWhenDeletingNonExistingStudent() {

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.delete(studentId))
                .isInstanceOf(StudentNotFoundException.class);
    }


    @Test
    void shouldFindStudentById() {

        Student student = studentEntity();

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));

        StudentResponseDTO response = service.findById(studentId);

        assertThat(response.id()).isEqualTo(studentId);
    }

    @Test
    void shouldThrowWhenFindingNonExistingStudent() {

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.findById(studentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Student not found");
    }


    @Test
    void shouldFindAllStudents() {

        Student student = studentEntity();

        when(studentRepository.findAll())
                .thenReturn(List.of(student));

        List<StudentResponseDTO> result = service.findAll();

        assertThat(result).hasSize(1);
    }
}
