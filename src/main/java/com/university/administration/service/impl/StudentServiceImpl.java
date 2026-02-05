package com.university.administration.service.impl;


import com.university.administration.domain.dto.StudentRequestDTO;
import com.university.administration.domain.dto.StudentResponseDTO;
import com.university.administration.domain.mapper.StudentMapper;
import com.university.administration.exceptions.StudentNotFoundException;
import com.university.administration.infrastructure.persistence.entity.Program;
import com.university.administration.infrastructure.persistence.entity.Student;
import com.university.administration.infrastructure.persistence.entity.User;
import com.university.administration.infrastructure.persistence.repository.ProgramRepository;
import com.university.administration.infrastructure.persistence.repository.StudentRepository;
import com.university.administration.infrastructure.persistence.repository.UserRepository;
import com.university.administration.service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ProgramRepository programRepository;

    public StudentServiceImpl(StudentRepository studentRepository,
                              UserRepository userRepository,
                              ProgramRepository programRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.programRepository = programRepository;
    }

    @Override
    public StudentResponseDTO create(StudentRequestDTO request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Program program = programRepository.findById(request.programId())
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        Student student = StudentMapper.toEntity(request, user, program);

        Student saved = studentRepository.save(student);

        return StudentMapper.toResponse(saved);
    }

    @Override
    public StudentResponseDTO update(UUID id, StudentRequestDTO request) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setPhone(request.phone());
        student.setAddress(request.address());

        Student updated = studentRepository.save(student);

        return StudentMapper.toResponse(updated);
    }

    @Override
    public void delete(UUID id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        studentRepository.delete(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO findById(UUID id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        return StudentMapper.toResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> findAll() {

        return studentRepository.findAll()
                .stream()
                .map(StudentMapper::toResponse)
                .toList();
    }
}

