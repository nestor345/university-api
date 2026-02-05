package com.university.administration.infrastructure.persistence.repository;

import com.university.administration.infrastructure.persistence.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeacherRepository extends JpaRepository<Teacher, UUID> {

    Optional<Teacher> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}

