package com.university.administration.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.administration.domain.dto.EnrollmentRequestDTO;
import com.university.administration.domain.dto.EnrollmentResponseDTO;
import com.university.administration.domain.enums.EnrollmentStatus;
import com.university.administration.service.EnrollmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnrollmentService enrollmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void enroll() throws Exception {

        // Necesario para evitar NumberFormatException
        MDC.put("startTime", "1");

        UUID studentId = UUID.randomUUID();
        UUID offeringId = UUID.randomUUID();

        EnrollmentRequestDTO request =
                new EnrollmentRequestDTO(studentId, offeringId);

        EnrollmentResponseDTO response =
                new EnrollmentResponseDTO(
                        UUID.randomUUID(),
                        studentId,
                        offeringId,
                        "Math I",
                        EnrollmentStatus.ACTIVE,
                        Instant.now()
                );

        when(enrollmentService.enroll(any(EnrollmentRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseName").value("Math I"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
