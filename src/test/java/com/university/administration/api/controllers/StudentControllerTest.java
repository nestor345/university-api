package com.university.administration.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.administration.domain.dto.StudentRequestDTO;
import com.university.administration.domain.dto.StudentResponseDTO;
import com.university.administration.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc(addFilters = false)
class StudentControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean
    private StudentService service;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void create() throws Exception {

        UUID programId = UUID.randomUUID();

        StudentRequestDTO request =
                new StudentRequestDTO(
                        UUID.randomUUID(),
                        programId,
                        "John",
                        "Doe",
                        "12345678",
                        LocalDate.of(2000, 1, 1),
                        "3001234567",
                        "Street 123"
                );

        StudentResponseDTO response =
                new StudentResponseDTO(
                        UUID.randomUUID(),
                        "John",
                        "Doe",
                        "12345678",
                        LocalDate.of(2000, 1, 1),
                        "3001234567",
                        "Street 123",
                        Instant.now()
                );

        when(service.create(any(StudentRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void findById() throws Exception {
        UUID id = UUID.randomUUID();

        StudentResponseDTO response =
                new StudentResponseDTO(
                        id,
                        "John",
                        "Doe",
                        "12345678",
                        LocalDate.of(2000, 1, 1),
                        "3001234567",
                        "Street 123",
                        Instant.now()
                );

        when(service.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/students/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void findAll() throws Exception {
        when(service.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk());
    }

    @Test
    void update() throws Exception {
        UUID id = UUID.randomUUID();
        UUID programId = UUID.randomUUID();

        StudentRequestDTO request =
                new StudentRequestDTO(
                        UUID.randomUUID(),
                        programId,
                        "John",
                        "Doe",
                        "12345678",
                        LocalDate.of(2000, 1, 1),
                        "3001234567",
                        "Street 123"
                );

        StudentResponseDTO response =
                new StudentResponseDTO(
                        id,
                        "John",
                        "Doe",
                        "12345678",
                        LocalDate.of(2000, 1, 1),
                        "3001234567",
                        "Street 123",
                        Instant.now()
                );

        when(service.update(eq(id), any(StudentRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/students/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/students/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }
}
