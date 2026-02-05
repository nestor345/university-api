package com.university.administration.service.impl;

import com.university.administration.domain.dto.EnrollmentRequestDTO;
import com.university.administration.domain.dto.EnrollmentResponseDTO;
import com.university.administration.domain.enums.EnrollmentStatus;
import com.university.administration.exceptions.BusinessException;
import com.university.administration.infrastructure.persistence.entity.CourseOffering;
import com.university.administration.infrastructure.persistence.entity.Enrollment;
import com.university.administration.infrastructure.persistence.entity.Student;
import com.university.administration.infrastructure.persistence.repository.EnrollmentRepository;
import com.university.administration.infrastructure.persistence.repository.StudentRepository;
import com.university.administration.service.CourseOfferingService;
import com.university.administration.service.EnrollmentService;
import com.university.administration.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseOfferingService offeringService;
    private final ScheduleService scheduleService;

    @Override
    public EnrollmentResponseDTO enroll(EnrollmentRequestDTO dto) {

        Student student = studentRepository.findById(dto.studentId())
                .orElseThrow(() ->
                        new jakarta.persistence.EntityNotFoundException("Student not found"));

        CourseOffering offering =
                offeringService.findEntity(dto.courseOfferingId());

        if (!offering.getPeriod().getActive()) {
            throw new BusinessException("Academic period is not active");
        }

        if (enrollmentRepository.existsByStudent_IdAndCourseOffering_Id(
                student.getId(),
                offering.getId())) {

            throw new BusinessException("Student already enrolled");
        }

        long count =
                enrollmentRepository.countByCourseOffering_Id(offering.getId());

        if (count >= offering.getMaxStudents()) {
            throw new BusinessException("Course offering is full");
        }

        if (scheduleService.hasStudentConflict(
                student.getId(),
                offering.getPeriod().getId(),
                offering.getSchedules())) {

            throw new BusinessException("Schedule conflict detected");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .courseOffering(offering)
                .status(EnrollmentStatus.ACTIVE)
                .build();

        enrollmentRepository.save(enrollment);

        return new EnrollmentResponseDTO(
                enrollment.getId(),
                student.getId(),
                offering.getId(),
                offering.getCourse().getName(),
                enrollment.getStatus(),
                enrollment.getEnrolledAt()
        );
    }


    @Override
    public void cancel(UUID id) {

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Enrollment not found"));

        enrollment.setStatus(EnrollmentStatus.CANCELLED);
    }
}
