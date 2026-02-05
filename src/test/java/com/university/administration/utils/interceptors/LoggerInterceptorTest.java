package com.university.administration.utils.interceptors;

import com.university.administration.utils.logs.APILogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LoggerInterceptorTest {

    private final LoggerInterceptor interceptor = new LoggerInterceptor();


    @Test
    void shouldLogAndSetMdcForNormalRequest() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/students");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        try (MockedStatic<APILogger> loggerMock = mockStatic(APILogger.class)) {

            boolean result = interceptor.preHandle(request, response, new Object());

            assertThat(result).isTrue();
            assertThat(MDC.get("startTime")).isNotNull();

            loggerMock.verify(() ->
                    APILogger.writeInputLog("GET /api/students", null)
            );
        }
    }



    @Test
    void shouldSkipLoggingForActuator() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/actuator/health");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        try (MockedStatic<APILogger> loggerMock = mockStatic(APILogger.class)) {

            boolean result = interceptor.preHandle(request, response, new Object());

            assertThat(result).isTrue();
            assertThat(MDC.get("startTime")).isNull();

            loggerMock.verifyNoInteractions();
        }
    }


    @Test
    void shouldSkipLoggingForFavicon() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/favicon.ico");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        try (MockedStatic<APILogger> loggerMock = mockStatic(APILogger.class)) {

            boolean result = interceptor.preHandle(request, response, new Object());

            assertThat(result).isTrue();
            assertThat(MDC.get("startTime")).isNull();

            loggerMock.verifyNoInteractions();
        }
    }


    @Test
    void shouldExecuteAfterCompletion() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(response.getStatus()).thenReturn(200);

        interceptor.afterCompletion(request, response, new Object(), null);

        verify(response).getStatus();
    }

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }
}
