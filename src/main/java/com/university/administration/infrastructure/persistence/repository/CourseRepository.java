package com.university.administration.infrastructure.persistence.repository;


import com.university.administration.infrastructure.persistence.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    Optional<Course> findByCode(String code);

    List<Course> findByProgramId(UUID programId);

    boolean existsByCode(String code);
}

