package com.university.administration.domain.mapper;

import com.university.administration.domain.dto.StudentRequestDTO;
import com.university.administration.domain.dto.StudentResponseDTO;
import com.university.administration.infrastructure.persistence.entity.Program;
import com.university.administration.infrastructure.persistence.entity.Student;
import com.university.administration.infrastructure.persistence.entity.User;

public class StudentMapper {

    public static Student toEntity(StudentRequestDTO dto, User user, Program program) {

        return Student.builder()
                .user(user)
                .program(program)
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .documentNumber(dto.documentNumber())
                .birthDate(dto.birthDate())
                .phone(dto.phone())
                .address(dto.address())
                .build();
    }

    public static StudentResponseDTO toResponse(Student student) {

        return new StudentResponseDTO(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getDocumentNumber(),
                student.getBirthDate(),
                student.getPhone(),
                student.getAddress(),
                student.getCreatedAt()
        );
    }
}

