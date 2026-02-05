package com.university.administration.exceptions.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.administration.exceptions.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        MDC.put("startTime", "1");
        MDC.put("transactionId", "tx-123");
    }

    @RestController
    static class TestController {

        @PostMapping("/validate")
        public void validate(@Valid @RequestBody TestRequest request) {}

        @GetMapping("/business")
        public void business() {
            throw new BusinessException("business error");
        }

        @GetMapping("/illegal")
        public void illegal() {
            throw new IllegalStateException("conflict");
        }

        @GetMapping("/not-found")
        public void notFound() {
            throw new EntityNotFoundException("not found");
        }

        @GetMapping("/generic")
        public void generic() {
            throw new RuntimeException("boom");
        }
    }

    static class TestRequest {
        @NotBlank
        public String name;
    }

    @Test
    void shouldHandleValidationException() throws Exception {
        String json = "{}"; // name null → @NotBlank falla

        mockMvc.perform(post("/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleMalformedJson() throws Exception {
        mockMvc.perform(post("/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid-json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleBusinessException() throws Exception {
        mockMvc.perform(get("/business"))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldHandleIllegalState() throws Exception {
        mockMvc.perform(get("/illegal"))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldHandleNotFound() throws Exception {
        mockMvc.perform(get("/not-found"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleGeneric() throws Exception {
        mockMvc.perform(get("/generic"))
                .andExpect(status().isInternalServerError());
    }
}
