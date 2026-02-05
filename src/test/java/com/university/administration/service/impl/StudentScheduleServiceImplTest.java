package com.university.administration.service.impl;

import com.university.administration.domain.dto.StudentScheduleResponseDTO;
import com.university.administration.domain.enums.DayOfWeekEnum;
import com.university.administration.infrastructure.persistence.entity.Course;
import com.university.administration.infrastructure.persistence.entity.CourseOffering;
import com.university.administration.infrastructure.persistence.entity.CourseOfferingSchedule;
import com.university.administration.infrastructure.persistence.entity.Student;
import com.university.administration.infrastructure.persistence.repository.CourseOfferingScheduleRepository;
import com.university.administration.infrastructure.persistence.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentScheduleServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseOfferingScheduleRepository scheduleRepository;

    @InjectMocks
    private StudentScheduleServiceImpl service;

    private UUID studentId;

    @BeforeEach
    void setup() {
        studentId = UUID.randomUUID();
    }


    @Test
    void shouldThrowWhenStudentNotFound() {

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getSchedule(studentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Student not found");
    }


    @Test
    void shouldReturnEmptyScheduleWhenNoClasses() {

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(new Student()));

        when(scheduleRepository.findActiveSchedulesByStudentId(studentId))
                .thenReturn(List.of());

        StudentScheduleResponseDTO response =
                service.getSchedule(studentId);

        assertThat(response.studentId()).isEqualTo(studentId);

        assertThat(response.map()).containsKeys(
                DayOfWeekEnum.MONDAY,
                DayOfWeekEnum.TUESDAY,
                DayOfWeekEnum.WEDNESDAY,
                DayOfWeekEnum.THURSDAY,
                DayOfWeekEnum.FRIDAY
        );

        response.map().values()
                .forEach(list -> assertThat(list).isEmpty());
    }


    @Test
    void shouldGroupSchedulesByDay() {

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(new Student()));

        Course course = new Course();
        course.setName("Math");

        CourseOffering offering = new CourseOffering();
        offering.setCourse(course);

        CourseOfferingSchedule schedule = CourseOfferingSchedule.builder()
                .dayOfWeek(DayOfWeekEnum.MONDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .courseOffering(offering)
                .build();

        when(scheduleRepository.findActiveSchedulesByStudentId(studentId))
                .thenReturn(List.of(schedule));

        StudentScheduleResponseDTO response =
                service.getSchedule(studentId);

        assertThat(response.map().get(DayOfWeekEnum.MONDAY))
                .hasSize(1);

        assertThat(response.map().get(DayOfWeekEnum.MONDAY).get(0).courseName())
                .isEqualTo("Math");
    }
}
