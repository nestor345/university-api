package com.university.administration.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.administration.domain.dto.ProgramRequestDTO;
import com.university.administration.domain.dto.ProgramResponseDTO;
import com.university.administration.service.ProgramService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProgramController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProgramControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProgramService programService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create() throws Exception {

        UUID facultyId = UUID.randomUUID();

        ProgramRequestDTO request =
                new ProgramRequestDTO("Engineering", facultyId);

        ProgramResponseDTO response =
                new ProgramResponseDTO(
                        UUID.randomUUID(),
                        "Engineering",
                        facultyId,
                        "Faculty of Engineering"
                );

        when(programService.create(any(ProgramRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/programs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Engineering"))
                .andExpect(jsonPath("$.facultyName").value("Faculty of Engineering"));
    }

    @Test
    void findById() throws Exception {

        UUID id = UUID.randomUUID();
        UUID facultyId = UUID.randomUUID();

        ProgramResponseDTO response =
                new ProgramResponseDTO(
                        id,
                        "Medicine",
                        facultyId,
                        "Faculty of Health"
                );

        when(programService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/programs/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Medicine"));
    }

    @Test
    void findAll() throws Exception {

        UUID facultyId = UUID.randomUUID();

        ProgramResponseDTO response =
                new ProgramResponseDTO(
                        UUID.randomUUID(),
                        "Law",
                        facultyId,
                        "Faculty of Law"
                );

        when(programService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/programs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void update() throws Exception {

        UUID id = UUID.randomUUID();
        UUID facultyId = UUID.randomUUID();

        ProgramRequestDTO request =
                new ProgramRequestDTO("Updated Program", facultyId);

        ProgramResponseDTO response =
                new ProgramResponseDTO(
                        id,
                        "Updated Program",
                        facultyId,
                        "Updated Faculty"
                );

        when(programService.update(eq(id), any(ProgramRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/programs/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Program"));
    }

    @Test
    void delete() throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/programs/{id}", id))
                .andExpect(status().isNoContent());
    }
}
