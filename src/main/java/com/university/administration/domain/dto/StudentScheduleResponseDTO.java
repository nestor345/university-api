package com.university.administration.domain.dto;

import com.university.administration.domain.enums.DayOfWeekEnum;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record StudentScheduleResponseDTO(
        UUID studentId,
        Map<DayOfWeekEnum, List<ScheduleItem>> map
        ) {}
