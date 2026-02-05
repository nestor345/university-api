package com.university.administration.infrastructure.persistence.repository;

import com.university.administration.infrastructure.persistence.entity.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, UUID> {

    List<CourseOffering> findByTeacherId(UUID teacherId);

    List<CourseOffering> findByPeriodId(UUID periodId);

    List<CourseOffering> findByCourseId(UUID courseId);
    List<CourseOffering> findByTeacherIdAndPeriodId(
            UUID teacherId,
            UUID periodId
    );
}

