package com.university.administration.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.administration.domain.dto.StudentScheduleResponseDTO;
import com.university.administration.domain.enums.DayOfWeekEnum;
import com.university.administration.service.StudentScheduleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentScheduleController.class)
@AutoConfigureMockMvc(addFilters = false)
class StudentScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentScheduleService scheduleService;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void getSchedule() throws Exception {

        // Necesario para evitar NumberFormatException
        MDC.put("startTime", "1");

        UUID studentId = UUID.randomUUID();

        StudentScheduleResponseDTO response =
                new StudentScheduleResponseDTO(
                        studentId,
                        Map.of(DayOfWeekEnum.MONDAY, List.of())
                );

        when(scheduleService.getSchedule(studentId))
                .thenReturn(response);

        mockMvc.perform(get("/students/{studentId}/schedule", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(studentId.toString()));
    }
}
