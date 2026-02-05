package com.university.administration.domain.mapper;

import com.university.administration.domain.dto.StudentRequestDTO;
import com.university.administration.domain.dto.StudentResponseDTO;
import com.university.administration.infrastructure.persistence.entity.Program;
import com.university.administration.infrastructure.persistence.entity.Student;
import com.university.administration.infrastructure.persistence.entity.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StudentMapperTest {

    @Test
    void shouldCreateInstance() {
        new StudentMapper();
    }

    @Test
    void shouldMapToEntity() {

        UUID userId = UUID.randomUUID();
        UUID programId = UUID.randomUUID();

        StudentRequestDTO dto =
                new StudentRequestDTO(
                        userId,
                        programId,
                        "John",
                        "Doe",
                        "12345678",
                        LocalDate.of(2000,1,1),
                        "3001234567",
                        "Street 123"
                );

        User user = User.builder()
                .id(userId)
                .build();

        Program program = Program.builder()
                .id(programId)
                .build();

        Student student = StudentMapper.toEntity(dto, user, program);

        assertThat(student.getUser()).isEqualTo(user);
        assertThat(student.getProgram()).isEqualTo(program);
        assertThat(student.getFirstName()).isEqualTo("John");
        assertThat(student.getLastName()).isEqualTo("Doe");
        assertThat(student.getDocumentNumber()).isEqualTo("12345678");
        assertThat(student.getBirthDate()).isEqualTo(LocalDate.of(2000,1,1));
        assertThat(student.getPhone()).isEqualTo("3001234567");
        assertThat(student.getAddress()).isEqualTo("Street 123");
    }

    @Test
    void shouldMapToResponse() {

        UUID studentId = UUID.randomUUID();

        Student student = Student.builder()
                .id(studentId)
                .firstName("John")
                .lastName("Doe")
                .documentNumber("12345678")
                .birthDate(LocalDate.of(2000,1,1))
                .phone("3001234567")
                .address("Street 123")
                .createdAt(Instant.now())
                .build();

        StudentResponseDTO response = StudentMapper.toResponse(student);

        assertThat(response.id()).isEqualTo(studentId);
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.documentNumber()).isEqualTo("12345678");
        assertThat(response.birthDate()).isEqualTo(LocalDate.of(2000,1,1));
        assertThat(response.phone()).isEqualTo("3001234567");
        assertThat(response.address()).isEqualTo("Street 123");
        assertThat(response.createdAt()).isEqualTo(student.getCreatedAt());
    }
}
