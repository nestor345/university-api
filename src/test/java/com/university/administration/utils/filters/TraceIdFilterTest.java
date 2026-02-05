package com.university.administration.utils.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TraceIdFilterTest {

    private TraceIdFilter filter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    private final String HEADER_NAME = "X-Transaction-Id";

    @BeforeEach
    void setUp() {
        filter = new TraceIdFilter();
        ReflectionTestUtils.setField(filter, "traceHeaderName", HEADER_NAME);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
    }


    @Test
    void shouldUseHeaderTransactionId() throws Exception {

        when(request.getHeader(HEADER_NAME)).thenReturn("custom-id");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");

        filter.doFilter(request, response, chain);

        verify(response).setHeader(HEADER_NAME, "custom-id");
        verify(chain).doFilter(request, response);

        assertThat(MDC.get("transactionId")).isNull(); // cleared after execution
    }


    @Test
    void shouldGenerateTransactionIdWhenHeaderNull() throws Exception {

        when(request.getHeader(HEADER_NAME)).thenReturn(null);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/test");

        filter.doFilter(request, response, chain);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq(HEADER_NAME), captor.capture());

        String generatedId = captor.getValue();

        assertThat(generatedId).isNotBlank();
        verify(chain).doFilter(request, response);
    }


    @Test
    void shouldGenerateTransactionIdWhenHeaderEmpty() throws Exception {

        when(request.getHeader(HEADER_NAME)).thenReturn("");
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/test");

        filter.doFilter(request, response, chain);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq(HEADER_NAME), captor.capture());

        assertThat(captor.getValue()).isNotBlank();
    }



    @Test
    void shouldGenerateTransactionIdWhenHeaderBlank() throws Exception {

        when(request.getHeader(HEADER_NAME)).thenReturn("   ");
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn("/test");

        filter.doFilter(request, response, chain);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq(HEADER_NAME), captor.capture());

        assertThat(captor.getValue()).isNotBlank();
    }
}
