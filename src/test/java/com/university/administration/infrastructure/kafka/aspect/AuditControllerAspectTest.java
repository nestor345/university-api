package com.university.administration.infrastructure.kafka.aspect;

import com.university.administration.infrastructure.kafka.AuditPublisher;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuditControllerAspectTest {

    private AuditPublisher auditPublisher;
    private HttpServletRequest request;
    private AuditControllerAspect aspect;
    private ProceedingJoinPoint joinPoint;

    @BeforeEach
    void setup() {
        auditPublisher = mock(AuditPublisher.class);
        request = mock(HttpServletRequest.class);
        joinPoint = mock(ProceedingJoinPoint.class);

        aspect = new AuditControllerAspect(auditPublisher, request);
    }

    @Test
    void shouldAuditSuccessExecution() throws Throwable {

        when(joinPoint.proceed()).thenReturn("OK");

        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("TestController.test()");
        when(joinPoint.getSignature()).thenReturn(signature);

        Object result = aspect.auditAllControllers(joinPoint);

        verify(auditPublisher).publish(
                eq(request),
                eq("CONTROLLER_EXECUTION"),
                eq("SUCCESS"),
                argThat(metadata ->
                        metadata.containsKey("controllerMethod") &&
                                metadata.containsKey("durationMs")
                )
        );

        verify(joinPoint).proceed();
        assertThat(result).isEqualTo("OK");
    }

    @Test
    void shouldAuditFailureExecutionAndRethrow() throws Throwable {

        RuntimeException exception = new RuntimeException("boom");

        when(joinPoint.proceed()).thenThrow(exception);

        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("TestController.test()");
        when(joinPoint.getSignature()).thenReturn(signature);

        assertThatThrownBy(() -> aspect.auditAllControllers(joinPoint))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("boom");

        verify(auditPublisher).publish(
                eq(request),
                eq("CONTROLLER_EXECUTION"),
                eq("FAIL"),
                argThat(metadata ->
                        metadata.containsKey("controllerMethod") &&
                                metadata.containsKey("error") &&
                                metadata.containsKey("message") &&
                                metadata.containsKey("durationMs")
                )
        );
    }
}
