package com.university.administration.domain.dto;

import java.time.LocalTime;

public record ScheduleItem(
        String courseName,
        LocalTime startTime,
        LocalTime endTime
) {}

