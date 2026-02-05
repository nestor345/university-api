package com.university.administration.domain.dto;

import java.util.List;
import java.util.UUID;

public record CourseOfferingRequestDTO(
        UUID courseId,
        UUID teacherId,
        UUID periodId,
        Integer maxStudents,
        List<ScheduleRequestDTO> schedules
) {}

