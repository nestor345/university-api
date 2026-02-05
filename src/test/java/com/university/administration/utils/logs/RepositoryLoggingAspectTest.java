package com.university.administration.utils.logs;

import com.university.administration.domain.logs.CallResource;
import com.university.administration.domain.logs.ResultTransaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RepositoryLoggingAspectTest {

    private RepositoryLoggingAspect aspect;
    private ProceedingJoinPoint pjp;

    @BeforeEach
    void setUp() {
        aspect = new RepositoryLoggingAspect();
        pjp = mock(ProceedingJoinPoint.class);

        Signature signature = mock(Signature.class);
        when(signature.getName()).thenReturn("findAll");
        when(pjp.getSignature()).thenReturn(signature);
    }


    @Test
    void shouldLogSuccessWhenRepositoryCallSucceeds() throws Throwable {

        when(pjp.proceed()).thenReturn("result");

        try (MockedStatic<APILogger> mockedLogger = mockStatic(APILogger.class)) {

            Object response = aspect.logRepositoryCall(pjp);

            assertThat(response).isEqualTo("result");

            mockedLogger.verify(() ->
                    APILogger.writeCallLog(
                            startsWith("DB findAll"),
                            eq(CallResource.BD_POS),
                            eq(ResultTransaction.SUCCESS),
                            anyLong(),
                            isNull()
                    )
            );
        }
    }


    @Test
    void shouldLogFailWhenRepositoryThrowsException() throws Throwable {

        when(pjp.proceed()).thenThrow(new RuntimeException("DB error"));

        try (MockedStatic<APILogger> mockedLogger = mockStatic(APILogger.class)) {

            assertThatThrownBy(() -> aspect.logRepositoryCall(pjp))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("DB error");

            mockedLogger.verify(() ->
                    APILogger.writeCallLog(
                            startsWith("DB findAll"),
                            eq(CallResource.BD_POS),
                            eq(ResultTransaction.FAIL),
                            anyLong(),
                            argThat(map ->
                                    map != null &&
                                            "DB error".equals(map.get("error"))
                            )
                    )
            );
        }
    }
}
