package com.university.administration.infrastructure.persistence.repository;

import com.university.administration.infrastructure.persistence.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FacultyRepository extends JpaRepository<Faculty, UUID> {

    Optional<Faculty> findByName(String name);

    boolean existsByName(String name);
}

