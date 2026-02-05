package com.university.administration.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CourseOfferingCreateDTO(

        @NotNull
        UUID courseId,

        @NotNull
        UUID teacherId,

        @NotNull
        UUID periodId,

        @Min(1)
        Integer maxStudents,

        @NotEmpty
        List<ScheduleRequestDTO> schedules

) {}

