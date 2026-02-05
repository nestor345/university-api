package com.university.administration.infrastructure.persistence.repository;

import com.university.administration.infrastructure.persistence.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProgramRepository extends JpaRepository<Program, UUID> {

    List<Program> findByFacultyId(UUID facultyId);
}

