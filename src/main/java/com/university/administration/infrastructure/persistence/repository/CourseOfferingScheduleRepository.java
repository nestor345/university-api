package com.university.administration.infrastructure.persistence.repository;

import com.university.administration.infrastructure.persistence.entity.CourseOfferingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CourseOfferingScheduleRepository
        extends JpaRepository<CourseOfferingSchedule, UUID> {

    List<CourseOfferingSchedule> findByCourseOfferingId(UUID offeringId);
    List<CourseOfferingSchedule>
    findByCourseOffering_Teacher_IdAndCourseOffering_Period_Id(
            UUID teacherId,
            UUID periodId
    );

    @Query("""
    SELECT s
    FROM CourseOfferingSchedule s
    JOIN s.courseOffering o
    JOIN Enrollment e ON e.courseOffering = o
    WHERE o.period.id = :periodId
      AND e.student.id = :studentId
""")
    List<CourseOfferingSchedule> findSchedulesByStudentAndPeriod(
            @Param("studentId") UUID studentId,
            @Param("periodId") UUID periodId
    );

    @Query("""
    SELECT s
    FROM CourseOfferingSchedule s
    JOIN s.courseOffering co
    JOIN co.enrollments e
    WHERE e.student.id = :studentId
      AND e.status = 'ACTIVE'
""")
    List<CourseOfferingSchedule> findActiveSchedulesByStudentId(UUID studentId);

}

