package com.university.administration.service.impl;

import com.university.administration.domain.dto.EnrollmentRequestDTO;
import com.university.administration.domain.enums.EnrollmentStatus;
import com.university.administration.exceptions.BusinessException;
import com.university.administration.infrastructure.persistence.entity.*;
import com.university.administration.infrastructure.persistence.repository.EnrollmentRepository;
import com.university.administration.infrastructure.persistence.repository.StudentRepository;
import com.university.administration.service.CourseOfferingService;
import com.university.administration.service.ScheduleService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseOfferingService offeringService;

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private EnrollmentServiceImpl service;

    private UUID studentId;
    private UUID offeringId;

    @BeforeEach
    void setup() {
        studentId = UUID.randomUUID();
        offeringId = UUID.randomUUID();
    }


    @Test
    void shouldThrowWhenStudentNotFound() {

        EnrollmentRequestDTO dto =
                new EnrollmentRequestDTO(studentId, offeringId);

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.enroll(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Student not found");
    }


    @Test
    void shouldThrowWhenPeriodNotActive() {

        EnrollmentRequestDTO dto =
                new EnrollmentRequestDTO(studentId, offeringId);

        Student student = new Student();
        student.setId(studentId);

        AcademicPeriod period = new AcademicPeriod();
        period.setActive(false);

        CourseOffering offering = new CourseOffering();
        offering.setId(offeringId);
        offering.setPeriod(period);

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));

        when(offeringService.findEntity(offeringId))
                .thenReturn(offering);

        assertThatThrownBy(() -> service.enroll(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Academic period is not active");
    }


    @Test
    void shouldThrowWhenAlreadyEnrolled() {

        EnrollmentRequestDTO dto =
                new EnrollmentRequestDTO(studentId, offeringId);

        Student student = new Student();
        student.setId(studentId);

        AcademicPeriod period = new AcademicPeriod();
        period.setActive(true);

        CourseOffering offering = new CourseOffering();
        offering.setId(offeringId);
        offering.setPeriod(period);

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));
        when(offeringService.findEntity(offeringId))
                .thenReturn(offering);

        when(enrollmentRepository.existsByStudent_IdAndCourseOffering_Id(
                studentId, offeringId))
                .thenReturn(true);

        assertThatThrownBy(() -> service.enroll(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Student already enrolled");
    }


    @Test
    void shouldThrowWhenCourseIsFull() {

        EnrollmentRequestDTO dto =
                new EnrollmentRequestDTO(studentId, offeringId);

        Student student = new Student();
        student.setId(studentId);

        AcademicPeriod period = new AcademicPeriod();
        period.setActive(true);

        CourseOffering offering = new CourseOffering();
        offering.setId(offeringId);
        offering.setPeriod(period);
        offering.setMaxStudents(1);

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));
        when(offeringService.findEntity(offeringId))
                .thenReturn(offering);

        when(enrollmentRepository.existsByStudent_IdAndCourseOffering_Id(
                studentId, offeringId))
                .thenReturn(false);

        when(enrollmentRepository.countByCourseOffering_Id(offeringId))
                .thenReturn(1L);

        assertThatThrownBy(() -> service.enroll(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Course offering is full");
    }


    @Test
    void shouldThrowWhenScheduleConflict() {

        EnrollmentRequestDTO dto =
                new EnrollmentRequestDTO(studentId, offeringId);

        Student student = new Student();
        student.setId(studentId);

        AcademicPeriod period = new AcademicPeriod();
        period.setId(UUID.randomUUID());
        period.setActive(true);

        CourseOffering offering = new CourseOffering();
        offering.setId(offeringId);
        offering.setPeriod(period);
        offering.setMaxStudents(10);
        offering.setSchedules(List.of());

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));
        when(offeringService.findEntity(offeringId))
                .thenReturn(offering);

        when(enrollmentRepository.existsByStudent_IdAndCourseOffering_Id(
                studentId, offeringId))
                .thenReturn(false);

        when(enrollmentRepository.countByCourseOffering_Id(offeringId))
                .thenReturn(0L);

        when(scheduleService.hasStudentConflict(
                studentId,
                period.getId(),
                offering.getSchedules()))
                .thenReturn(true);

        assertThatThrownBy(() -> service.enroll(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Schedule conflict detected");
    }


    @Test
    void shouldEnrollSuccessfully() {

        EnrollmentRequestDTO dto =
                new EnrollmentRequestDTO(studentId, offeringId);

        Student student = new Student();
        student.setId(studentId);

        AcademicPeriod period = new AcademicPeriod();
        period.setId(UUID.randomUUID());
        period.setActive(true);

        Course course = new Course();
        course.setName("Math");

        CourseOffering offering = new CourseOffering();
        offering.setId(offeringId);
        offering.setPeriod(period);
        offering.setCourse(course);
        offering.setMaxStudents(10);
        offering.setSchedules(List.of());

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));
        when(offeringService.findEntity(offeringId))
                .thenReturn(offering);

        when(enrollmentRepository.existsByStudent_IdAndCourseOffering_Id(
                studentId, offeringId))
                .thenReturn(false);

        when(enrollmentRepository.countByCourseOffering_Id(offeringId))
                .thenReturn(0L);

        when(scheduleService.hasStudentConflict(
                studentId,
                period.getId(),
                offering.getSchedules()))
                .thenReturn(false);

        Enrollment saved = Enrollment.builder()
                .id(UUID.randomUUID())
                .student(student)
                .courseOffering(offering)
                .status(EnrollmentStatus.ACTIVE)
                .enrolledAt(Instant.now())
                .build();

        when(enrollmentRepository.save(any()))
                .thenReturn(saved);

        var response = service.enroll(dto);

        assertThat(response.studentId()).isEqualTo(studentId);
        assertThat(response.offeringId()).isEqualTo(offeringId);
        assertThat(response.courseName()).isEqualTo("Math");
        assertThat(response.status()).isEqualTo(EnrollmentStatus.ACTIVE);
    }


    @Test
    void shouldCancelEnrollment() {

        UUID enrollmentId = UUID.randomUUID();

        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        when(enrollmentRepository.findById(enrollmentId))
                .thenReturn(Optional.of(enrollment));

        service.cancel(enrollmentId);

        assertThat(enrollment.getStatus())
                .isEqualTo(EnrollmentStatus.CANCELLED);
    }

    @Test
    void shouldThrowWhenCancelNotFound() {

        UUID enrollmentId = UUID.randomUUID();

        when(enrollmentRepository.findById(enrollmentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancel(enrollmentId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Enrollment not found");
    }
}
