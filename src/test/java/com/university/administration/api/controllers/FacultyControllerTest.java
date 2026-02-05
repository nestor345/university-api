package com.university.administration.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.administration.domain.dto.FacultyRequestDTO;
import com.university.administration.domain.dto.FacultyResponseDTO;
import com.university.administration.service.FacultyService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
@AutoConfigureMockMvc(addFilters = false)
class FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create() throws Exception {

        FacultyRequestDTO request =
                new FacultyRequestDTO("Engineering", "Engineering Faculty");

        FacultyResponseDTO response =
                new FacultyResponseDTO(
                        UUID.randomUUID(),
                        "Engineering",
                        "Engineering Faculty"
                );

        when(facultyService.create(any(FacultyRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Engineering"))
                .andExpect(jsonPath("$.description").value("Engineering Faculty"));
    }

    @Test
    void findById() throws Exception {

        UUID id = UUID.randomUUID();

        FacultyResponseDTO response =
                new FacultyResponseDTO(
                        id,
                        "Medicine",
                        "Medical Faculty"
                );

        when(facultyService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/faculties/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Medicine"));
    }

    @Test
    void findAll() throws Exception {

        FacultyResponseDTO response =
                new FacultyResponseDTO(
                        UUID.randomUUID(),
                        "Law",
                        "Law Faculty"
                );

        when(facultyService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/faculties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void delete() throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/faculties/{id}", id))
                .andExpect(status().isNoContent());
    }
}
