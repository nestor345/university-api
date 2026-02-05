package com.university.administration.service;

import com.university.administration.domain.dto.ScheduleRequestDTO;
import com.university.administration.infrastructure.persistence.entity.CourseOfferingSchedule;

import java.util.List;
import java.util.UUID;

public interface ScheduleService {

    List<CourseOfferingSchedule> buildSchedules(List<ScheduleRequestDTO> dtos);

    boolean hasTeacherConflict(UUID teacherId, UUID periodId, List<CourseOfferingSchedule> schedules);

    boolean hasStudentConflict(UUID studentId, UUID periodId, List<CourseOfferingSchedule> schedules);
}

