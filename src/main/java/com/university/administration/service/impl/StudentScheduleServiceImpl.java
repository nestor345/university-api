package com.university.administration.service.impl;

import com.university.administration.domain.dto.ScheduleItem;
import com.university.administration.domain.dto.StudentScheduleResponseDTO;
import com.university.administration.domain.enums.DayOfWeekEnum;
import com.university.administration.exceptions.BusinessException;
import com.university.administration.infrastructure.persistence.entity.CourseOfferingSchedule;
import com.university.administration.infrastructure.persistence.repository.CourseOfferingScheduleRepository;
import com.university.administration.infrastructure.persistence.repository.StudentRepository;
import com.university.administration.service.StudentScheduleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentScheduleServiceImpl implements StudentScheduleService {
    private final StudentRepository studentRepository;
    private final CourseOfferingScheduleRepository scheduleRepository;
    private static final Set<DayOfWeekEnum> WEEK_DAYS = Set.of(
            DayOfWeekEnum.MONDAY,
            DayOfWeekEnum.TUESDAY,
            DayOfWeekEnum.WEDNESDAY,
            DayOfWeekEnum.THURSDAY,
            DayOfWeekEnum.FRIDAY
    );

    public StudentScheduleResponseDTO getSchedule(UUID studentId) {

        studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        List<CourseOfferingSchedule> schedules =
                scheduleRepository.findActiveSchedulesByStudentId(studentId);

        Map<DayOfWeekEnum, List<ScheduleItem>> grouped =
                Arrays.stream(DayOfWeekEnum.values())
                        .filter(WEEK_DAYS::contains)
                        .collect(Collectors.toMap(
                                day -> day,
                                day -> new ArrayList<>()
                        ));


        for (CourseOfferingSchedule schedule : schedules) {
            DayOfWeekEnum day = schedule.getDayOfWeek();

            ScheduleItem item = new ScheduleItem(
                    schedule.getCourseOffering().getCourse().getName(),
                    schedule.getStartTime(),
                    schedule.getEndTime()
            );

            grouped.get(day).add(item);
        }

        return new StudentScheduleResponseDTO(studentId, grouped);
    }
}
