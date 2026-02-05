package com.university.administration.infrastructure.persistence.repository;

import com.university.administration.infrastructure.persistence.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    boolean existsByStudent_IdAndCourseOffering_Id(
            UUID studentId,
            UUID offeringId
    );

    long countByCourseOffering_Id(UUID offeringId);

    Optional<Enrollment> findByStudentIdAndCourseOfferingId(UUID studentId, UUID courseOfferingId);
}


