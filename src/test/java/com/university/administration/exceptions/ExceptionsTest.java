package com.university.administration.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {

    @Test
    void shouldCreateBusinessException() {
        BusinessException ex = new BusinessException("error");
        assertThat(ex.getMessage()).isEqualTo("error");
    }

    @Test
    void shouldCreateCourseNotFoundException() {
        CourseNotFoundException ex = new CourseNotFoundException("error");
        assertThat(ex.getMessage()).isEqualTo("error");
    }

    @Test
    void shouldCreateFacultyMismatchException() {
        FacultyMismatchException ex = new FacultyMismatchException("error");
        assertThat(ex.getMessage()).isEqualTo("error");
    }

    @Test
    void shouldCreateInactiveAcademicPeriodException() {
        InactiveAcademicPeriodException ex = new InactiveAcademicPeriodException("error");
        assertThat(ex.getMessage()).isEqualTo("error");
    }

    @Test
    void shouldCreateProgramNotFoundException() {
        ProgramNotFoundException ex = new ProgramNotFoundException("error");
        assertThat(ex.getMessage()).isEqualTo("error");
    }

    @Test
    void shouldCreateStudentNotFoundException() {
        UUID id = UUID.randomUUID();
        StudentNotFoundException ex = new StudentNotFoundException(id);

        assertThat(ex.getMessage())
                .isEqualTo("Student not found with id: " + id);
    }


    @Test
    void shouldCreateTeacherScheduleConflictException() {
        TeacherScheduleConflictException ex = new TeacherScheduleConflictException("error");
        assertThat(ex.getMessage()).isEqualTo("error");
    }
}
