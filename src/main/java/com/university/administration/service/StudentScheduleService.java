package com.university.administration.service;

import com.university.administration.domain.dto.StudentScheduleResponseDTO;

import java.util.UUID;

public interface StudentScheduleService {
    StudentScheduleResponseDTO getSchedule(UUID studentId);
}
