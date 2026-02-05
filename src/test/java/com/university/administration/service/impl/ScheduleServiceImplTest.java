package com.university.administration.service.impl;

import com.university.administration.domain.dto.ScheduleRequestDTO;
import com.university.administration.domain.enums.DayOfWeekEnum;
import com.university.administration.exceptions.BusinessException;
import com.university.administration.infrastructure.persistence.entity.CourseOfferingSchedule;
import com.university.administration.infrastructure.persistence.repository.CourseOfferingScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleServiceImplTest {

    private CourseOfferingScheduleRepository repository;
    private ScheduleServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(CourseOfferingScheduleRepository.class);
        service = new ScheduleServiceImpl(repository);
    }

    private CourseOfferingSchedule schedule(DayOfWeekEnum day,
                                            LocalTime start,
                                            LocalTime end) {
        return CourseOfferingSchedule.builder()
                .dayOfWeek(day)
                .startTime(start)
                .endTime(end)
                .build();
    }


    @Test
    void shouldBuildSchedulesSuccessfully() {

        ScheduleRequestDTO dto = new ScheduleRequestDTO(
                DayOfWeekEnum.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(10, 0)
        );

        List<CourseOfferingSchedule> result =
                service.buildSchedules(List.of(dto));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDayOfWeek())
                .isEqualTo(DayOfWeekEnum.MONDAY);
    }

    @Test
    void shouldThrowWhenInvalidTimeRange() {

        ScheduleRequestDTO dto = new ScheduleRequestDTO(
                DayOfWeekEnum.MONDAY,
                LocalTime.of(10, 0),
                LocalTime.of(8, 0)
        );

        assertThatThrownBy(() ->
                service.buildSchedules(List.of(dto))
        ).isInstanceOf(BusinessException.class)
                .hasMessage("Invalid schedule time range");
    }


    @Test
    void shouldReturnTrueWhenTeacherConflictExists() {

        UUID teacherId = UUID.randomUUID();
        UUID periodId = UUID.randomUUID();

        CourseOfferingSchedule existing =
                schedule(DayOfWeekEnum.MONDAY,
                        LocalTime.of(8, 0),
                        LocalTime.of(10, 0));

        CourseOfferingSchedule incoming =
                schedule(DayOfWeekEnum.MONDAY,
                        LocalTime.of(9, 0),
                        LocalTime.of(11, 0)); // overlap

        when(repository
                .findByCourseOffering_Teacher_IdAndCourseOffering_Period_Id(
                        teacherId, periodId))
                .thenReturn(List.of(existing));

        boolean result = service.hasTeacherConflict(
                teacherId, periodId, List.of(incoming));

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNoTeacherConflict() {

        UUID teacherId = UUID.randomUUID();
        UUID periodId = UUID.randomUUID();

        CourseOfferingSchedule existing =
                schedule(DayOfWeekEnum.MONDAY,
                        LocalTime.of(8, 0),
                        LocalTime.of(10, 0));

        CourseOfferingSchedule incoming =
                schedule(DayOfWeekEnum.TUESDAY,
                        LocalTime.of(8, 0),
                        LocalTime.of(10, 0)); // different day

        when(repository
                .findByCourseOffering_Teacher_IdAndCourseOffering_Period_Id(
                        teacherId, periodId))
                .thenReturn(List.of(existing));

        boolean result = service.hasTeacherConflict(
                teacherId, periodId, List.of(incoming));

        assertThat(result).isFalse();
    }


    @Test
    void shouldReturnTrueWhenStudentConflictExists() {

        UUID studentId = UUID.randomUUID();
        UUID periodId = UUID.randomUUID();

        CourseOfferingSchedule existing =
                schedule(DayOfWeekEnum.MONDAY,
                        LocalTime.of(8, 0),
                        LocalTime.of(10, 0));

        CourseOfferingSchedule incoming =
                schedule(DayOfWeekEnum.MONDAY,
                        LocalTime.of(9, 30),
                        LocalTime.of(10, 30)); // overlap

        when(repository.findSchedulesByStudentAndPeriod(
                studentId, periodId))
                .thenReturn(List.of(existing));

        boolean result = service.hasStudentConflict(
                studentId, periodId, List.of(incoming));

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNoStudentConflict() {

        UUID studentId = UUID.randomUUID();
        UUID periodId = UUID.randomUUID();

        when(repository.findSchedulesByStudentAndPeriod(
                studentId, periodId))
                .thenReturn(List.of());

        boolean result = service.hasStudentConflict(
                studentId, periodId, List.of());

        assertThat(result).isFalse();
    }
    @Test
    void shouldReturnFalseWhenSameDayButNoOverlap() {

        UUID teacherId = UUID.randomUUID();
        UUID periodId = UUID.randomUUID();

        CourseOfferingSchedule existing =
                schedule(DayOfWeekEnum.MONDAY,
                        LocalTime.of(8, 0),
                        LocalTime.of(10, 0));

        CourseOfferingSchedule incoming =
                schedule(DayOfWeekEnum.MONDAY,
                        LocalTime.of(10, 0),
                        LocalTime.of(12, 0));

        when(repository
                .findByCourseOffering_Teacher_IdAndCourseOffering_Period_Id(
                        teacherId, periodId))
                .thenReturn(List.of(existing));

        boolean result = service.hasTeacherConflict(
                teacherId, periodId, List.of(incoming));

        assertThat(result).isFalse();
    }

}
