package com.university.administration.api.controllers;

import com.university.administration.domain.dto.EnrollmentRequestDTO;
import com.university.administration.domain.dto.EnrollmentResponseDTO;
import com.university.administration.domain.logs.ResultTransaction;
import com.university.administration.service.EnrollmentService;
import com.university.administration.utils.logs.APILogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private static final String START_TIME_KEY = "startTime";

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> enroll(HttpServletRequest request,
            @Valid @RequestBody EnrollmentRequestDTO dto) {

        EnrollmentResponseDTO response = enrollmentService.enroll(dto);

        APILogger.writeOutputLog(request.getMethod().toUpperCase() + " " + request.getRequestURI(),
                ResultTransaction.SUCCESS, Long.parseLong(MDC.get(START_TIME_KEY)), null);

        return ResponseEntity.ok(response);
    }
}

