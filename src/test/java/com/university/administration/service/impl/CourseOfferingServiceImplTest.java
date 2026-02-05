package com.university.administration.service.impl;

import com.university.administration.domain.dto.CourseOfferingCreateDTO;
import com.university.administration.domain.dto.CourseOfferingResponseDTO;
import com.university.administration.domain.dto.ScheduleRequestDTO;
import com.university.administration.domain.enums.DayOfWeekEnum;
import com.university.administration.exceptions.BusinessException;
import com.university.administration.infrastructure.persistence.entity.*;
import com.university.administration.infrastructure.persistence.repository.CourseOfferingRepository;
import com.university.administration.infrastructure.persistence.repository.CourseRepository;
import com.university.administration.infrastructure.persistence.repository.TeacherRepository;
import com.university.administration.service.AcademicPeriodService;
import com.university.administration.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseOfferingServiceImplTest {

    private CourseRepository courseRepository;
    private TeacherRepository teacherRepository;
    private AcademicPeriodService academicPeriodService;
    private CourseOfferingRepository offeringRepository;
    private ScheduleService scheduleService;

    private CourseOfferingServiceImpl service;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        teacherRepository = mock(TeacherRepository.class);
        academicPeriodService = mock(AcademicPeriodService.class);
        offeringRepository = mock(CourseOfferingRepository.class);
        scheduleService = mock(ScheduleService.class);

        service = new CourseOfferingServiceImpl(
                courseRepository,
                teacherRepository,
                academicPeriodService,
                offeringRepository,
                scheduleService
        );
    }

    private CourseOfferingCreateDTO buildDTO(UUID courseId, UUID teacherId, UUID periodId) {
        return new CourseOfferingCreateDTO(
                courseId,
                teacherId,
                periodId,
                30,
                List.of(new ScheduleRequestDTO(
                        DayOfWeekEnum.MONDAY,
                        LocalTime.of(8, 0),
                        LocalTime.of(10, 0)
                ))
        );
    }

    @Test
    void shouldCreateCourseOfferingSuccessfully() {

        UUID courseId = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();
        UUID periodId = UUID.randomUUID();

        CourseOfferingCreateDTO dto = buildDTO(courseId, teacherId, periodId);

        Course course = Course.builder().id(courseId).name("Math").build();
        Teacher teacher = Teacher.builder().id(teacherId).build();
        AcademicPeriod period = AcademicPeriod.builder()
                .id(periodId)
                .active(true)
                .build();

        CourseOfferingSchedule schedule = CourseOfferingSchedule.builder()
                .dayOfWeek(DayOfWeekEnum.MONDAY)
                .startTime(LocalTime.of(8,0))
                .endTime(LocalTime.of(10,0))
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(academicPeriodService.findEntity(periodId)).thenReturn(period);
        when(scheduleService.buildSchedules(any())).thenReturn(List.of(schedule));
        when(scheduleService.hasTeacherConflict(any(), any(), any())).thenReturn(false);

        CourseOfferingResponseDTO response = service.create(dto);

        verify(offeringRepository).save(any());

        assertThat(response.courseName()).isEqualTo("Math");
        assertThat(response.teacherId()).isEqualTo(teacherId);
        assertThat(response.periodId()).isEqualTo(periodId);
        assertThat(response.schedules()).hasSize(1);
    }

    @Test
    void shouldThrowWhenCourseNotFound() {

        UUID id = UUID.randomUUID();
        CourseOfferingCreateDTO dto = buildDTO(id, UUID.randomUUID(), UUID.randomUUID());

        when(courseRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Course not found");
    }

    @Test
    void shouldThrowWhenTeacherNotFound() {

        UUID courseId = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();

        CourseOfferingCreateDTO dto = buildDTO(courseId, teacherId, UUID.randomUUID());

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mock(Course.class)));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Teacher not found");
    }

    @Test
    void shouldThrowWhenPeriodNotActive() {

        UUID courseId = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();
        UUID periodId = UUID.randomUUID();

        CourseOfferingCreateDTO dto = buildDTO(courseId, teacherId, periodId);

        AcademicPeriod period = AcademicPeriod.builder()
                .id(periodId)
                .active(false)
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mock(Course.class)));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(mock(Teacher.class)));
        when(academicPeriodService.findEntity(periodId)).thenReturn(period);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Academic period is not active");
    }

    @Test
    void shouldThrowWhenTeacherConflictExists() {

        UUID courseId = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();
        UUID periodId = UUID.randomUUID();

        CourseOfferingCreateDTO dto = buildDTO(courseId, teacherId, periodId);

        AcademicPeriod period = AcademicPeriod.builder()
                .id(periodId)
                .active(true)
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mock(Course.class)));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(Teacher.builder().id(teacherId).build()));
        when(academicPeriodService.findEntity(periodId)).thenReturn(period);
        when(scheduleService.buildSchedules(any())).thenReturn(List.of(mock(CourseOfferingSchedule.class)));
        when(scheduleService.hasTeacherConflict(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Teacher schedule conflict detected");
    }

    @Test
    void shouldFindEntitySuccessfully() {

        UUID id = UUID.randomUUID();
        CourseOffering offering = mock(CourseOffering.class);

        when(offeringRepository.findById(id)).thenReturn(Optional.of(offering));

        CourseOffering result = service.findEntity(id);

        assertThat(result).isEqualTo(offering);
    }

    @Test
    void shouldThrowWhenOfferingNotFound() {

        UUID id = UUID.randomUUID();

        when(offeringRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findEntity(id))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Course offering not found");
    }
}
