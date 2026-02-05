package com.university.administration.infrastructure.persistence.repository;

import com.university.administration.infrastructure.persistence.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {

    Optional<Student> findByDocumentNumber(String documentNumber);

    boolean existsByDocumentNumber(String documentNumber);

    Optional<Student> findByUserId(UUID userId);
}

