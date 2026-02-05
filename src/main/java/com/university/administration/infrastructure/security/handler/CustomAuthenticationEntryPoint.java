package com.university.administration.infrastructure.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.administration.exceptions.model.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        ApiError error = ApiError.builder()
                .type(request.getRequestURL().toString())
                .title("Unauthorized")
                .status(HttpStatus.UNAUTHORIZED.value())
                .detail("Invalid or missing authentication token")
                .instance(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}

