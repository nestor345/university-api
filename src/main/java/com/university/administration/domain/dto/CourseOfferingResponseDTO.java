package com.university.administration.domain.dto;

import java.util.List;
import java.util.UUID;

public record CourseOfferingResponseDTO(

        UUID id,
        UUID courseId,
        String courseName,
        UUID teacherId,
        UUID periodId,
        Integer maxStudents,
        List<ScheduleResponseDTO> schedules

) {}

