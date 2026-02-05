package com.university.administration.service.impl;

import com.university.administration.domain.dto.ScheduleRequestDTO;
import com.university.administration.exceptions.BusinessException;
import com.university.administration.infrastructure.persistence.entity.CourseOfferingSchedule;
import com.university.administration.infrastructure.persistence.repository.CourseOfferingScheduleRepository;
import com.university.administration.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleServiceImpl implements ScheduleService {

    private final CourseOfferingScheduleRepository scheduleRepository;

    @Override
    public List<CourseOfferingSchedule> buildSchedules(List<ScheduleRequestDTO> dtos) {

        return dtos.stream().map(dto -> {

            if (!dto.startTime().isBefore(dto.endTime())) {
                throw new BusinessException("Invalid schedule time range");
            }

            return CourseOfferingSchedule.builder()
                    .dayOfWeek(dto.dayOfWeek())
                    .startTime(dto.startTime())
                    .endTime(dto.endTime())
                    .build();
        }).toList();
    }

    @Override
    public boolean hasTeacherConflict(UUID teacherId, UUID periodId,
                                      List<CourseOfferingSchedule> schedules) {

        List<CourseOfferingSchedule> existing =
                scheduleRepository.findByCourseOffering_Teacher_IdAndCourseOffering_Period_Id(teacherId, periodId);

        return hasConflict(existing, schedules);
    }

    @Override
    public boolean hasStudentConflict(UUID studentId, UUID periodId,
                                      List<CourseOfferingSchedule> schedules) {

        List<CourseOfferingSchedule> existing =
                scheduleRepository.findSchedulesByStudentAndPeriod(studentId, periodId);

        return hasConflict(existing, schedules);
    }

    private boolean hasConflict(List<CourseOfferingSchedule> existing,
                                List<CourseOfferingSchedule> incoming) {

        for (CourseOfferingSchedule e : existing) {
            for (CourseOfferingSchedule n : incoming) {

                if (e.getDayOfWeek().equals(n.getDayOfWeek())) {

                    boolean overlap =
                            n.getStartTime().isBefore(e.getEndTime()) &&
                                    n.getEndTime().isAfter(e.getStartTime());

                    if (overlap) return true;
                }
            }
        }

        return false;
    }
}

