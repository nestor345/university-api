package com.university.administration.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.administration.domain.dto.CourseRequestDTO;
import com.university.administration.domain.dto.CourseResponseDTO;
import com.university.administration.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCourse() throws Exception {

        UUID programId = UUID.randomUUID();

        CourseRequestDTO request =
                new CourseRequestDTO(
                        "Math",
                        3,
                        programId
                );

        CourseResponseDTO response =
                new CourseResponseDTO(
                        UUID.randomUUID(),
                        "Math",
                        3,
                        programId,
                        "computer science"
                );

        when(service.create(any(CourseRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Math"))
                .andExpect(jsonPath("$.credits").value(3));
    }

    @Test
    void shouldFindById() throws Exception {

        UUID id = UUID.randomUUID();
        UUID programId = UUID.randomUUID();

        CourseResponseDTO response =
                new CourseResponseDTO(
                        id,
                        "Physics",
                        4,
                        programId,
                        "engeneering"
                );

        when(service.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/courses/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Physics"));
    }

    @Test
    void shouldFindAll() throws Exception {

        UUID programId = UUID.randomUUID();

        CourseResponseDTO response =
                new CourseResponseDTO(
                        UUID.randomUUID(),
                        "Biology",
                        2,
                        programId,
                        "science"
                );

        when(service.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void shouldUpdate() throws Exception {

        UUID id = UUID.randomUUID();
        UUID programId = UUID.randomUUID();

        CourseRequestDTO request =
                new CourseRequestDTO(
                        "Updated",
                        5,
                        programId
                );

        CourseResponseDTO response =
                new CourseResponseDTO(
                        id,
                        "Updated",
                        5,
                        programId,
                        "medicine"
                );

        when(service.update(eq(id), any(CourseRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/courses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void shouldDelete() throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/courses/{id}", id))
                .andExpect(status().isNoContent());
    }
}
