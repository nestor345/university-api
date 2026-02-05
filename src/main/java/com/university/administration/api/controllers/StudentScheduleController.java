package com.university.administration.api.controllers;

import com.university.administration.domain.dto.StudentScheduleResponseDTO;
import com.university.administration.domain.logs.ResultTransaction;
import com.university.administration.service.StudentScheduleService;
import com.university.administration.utils.logs.APILogger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentScheduleController {

    private static final String START_TIME_KEY = "startTime";

    private final StudentScheduleService scheduleService;

    @GetMapping("/{studentId}/schedule")
    public ResponseEntity<StudentScheduleResponseDTO> getSchedule( HttpServletRequest request,
            @PathVariable UUID studentId) {
        StudentScheduleResponseDTO response = scheduleService.getSchedule(studentId);

        APILogger.writeOutputLog(request.getMethod().toUpperCase() + " " + request.getRequestURI(),
                ResultTransaction.SUCCESS, Long.parseLong(MDC.get(START_TIME_KEY)), null);

        return ResponseEntity.ok(response);
    }
}

