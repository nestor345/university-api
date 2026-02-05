package com.university.administration.infrastructure.persistence.repository;

import com.university.administration.domain.enums.GradeType;
import com.university.administration.infrastructure.persistence.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GradeRepository extends JpaRepository<Grade, UUID> {

    List<Grade> findByEnrollmentId(UUID enrollmentId);

    Optional<Grade> findByEnrollmentIdAndGradeType(UUID enrollmentId, GradeType gradeType);
}

