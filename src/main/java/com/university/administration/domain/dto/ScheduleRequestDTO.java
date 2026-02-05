package com.university.administration.domain.dto;

import com.university.administration.domain.enums.DayOfWeekEnum;

import java.time.LocalTime;

public record ScheduleRequestDTO(
        DayOfWeekEnum dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}
