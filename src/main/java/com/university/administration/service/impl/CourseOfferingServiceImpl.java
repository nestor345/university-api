package com.university.administration.service.impl;

import com.university.administration.domain.dto.CourseOfferingCreateDTO;
import com.university.administration.domain.dto.CourseOfferingResponseDTO;
import com.university.administration.domain.dto.ScheduleResponseDTO;
import com.university.administration.exceptions.BusinessException;
import com.university.administration.infrastructure.persistence.entity.*;
import com.university.administration.infrastructure.persistence.repository.CourseOfferingRepository;
import com.university.administration.infrastructure.persistence.repository.CourseRepository;
import com.university.administration.infrastructure.persistence.repository.TeacherRepository;
import com.university.administration.service.AcademicPeriodService;
import com.university.administration.service.CourseOfferingService;
import com.university.administration.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseOfferingServiceImpl implements CourseOfferingService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final AcademicPeriodService academicPeriodService;
    private final CourseOfferingRepository offeringRepository;
    private final ScheduleService scheduleService;

    @Override
    public CourseOfferingResponseDTO create(CourseOfferingCreateDTO dto) {

        Course course = courseRepository.findById(dto.courseId())
                .orElseThrow(() -> new BusinessException("Course not found"));

        Teacher teacher = teacherRepository.findById(dto.teacherId())
                .orElseThrow(() -> new BusinessException("Teacher not found"));

        AcademicPeriod period = academicPeriodService.findEntity(dto.periodId());

        if (!period.getActive()) {
            throw new BusinessException("Academic period is not active");
        }

        List<CourseOfferingSchedule> schedules =
                scheduleService.buildSchedules(dto.schedules());

        if (scheduleService.hasTeacherConflict(
                teacher.getId(),
                period.getId(),
                schedules)) {

            throw new BusinessException("Teacher schedule conflict detected");
        }

        CourseOffering offering = CourseOffering.builder()
                .course(course)
                .teacher(teacher)
                .period(period)
                .maxStudents(dto.maxStudents())
                .schedules(schedules)
                .build();

        offeringRepository.save(offering);

        return map(offering);
    }

    @Override
    public CourseOffering findEntity(UUID id) {
        return offeringRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Course offering not found"));
    }

    private CourseOfferingResponseDTO map(CourseOffering o) {

        List<ScheduleResponseDTO> schedules =
                o.getSchedules().stream()
                        .map(s -> new ScheduleResponseDTO(
                                s.getDayOfWeek(),
                                s.getStartTime(),
                                s.getEndTime()
                        ))
                        .toList();

        return new CourseOfferingResponseDTO(
                o.getId(),
                o.getCourse().getId(),
                o.getCourse().getName(),
                o.getTeacher().getId(),
                o.getPeriod().getId(),
                o.getMaxStudents(),
                schedules
        );
    }
}

