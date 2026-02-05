package com.university.administration.utils.logs;

import com.university.administration.domain.logs.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class APILoggerTest {


    @Test
    void shouldWriteInputLog() {
        APILogger.writeInputLog("SERVICE_TEST", Map.of("key", "value"));
    }


    @Test
    void shouldWriteCallLog() {
        APILogger.writeCallLog(
                "SERVICE_TEST",
                CallResource.BD_POS,
                ResultTransaction.SUCCESS,
                System.currentTimeMillis(),
                Map.of("k", "v")
        );
    }


    @Test
    void shouldWriteOutputLog() {
        APILogger.writeOutputLog(
                "SERVICE_TEST",
                ResultTransaction.FAIL,
                System.currentTimeMillis(),
                null
        );
    }


    @Test
    void shouldWriteLogDirectly() {
        APILogModel model = new APILogModel();
        model.setService("SERVICE_TEST");
        model.setActivity(ActivityPhase.INPUT);
        model.setResult(ResultTransaction.SUCCESS);

        APILogger.writeLog(model);
    }


    @Test
    void shouldConvertObjectToJson() throws Exception {

        Method method = APILogger.class
                .getDeclaredMethod("toSingleLineJson", Object.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, Map.of("key", "value"));

        assertThat(result).contains("key");
    }


    @Test
    void shouldReturnEmptyJsonWhenNull() throws Exception {

        Method method = APILogger.class
                .getDeclaredMethod("toSingleLineJson", Object.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, new Object[]{null});

        assertThat(result).isEqualTo("{}");
    }


    @Test
    void shouldReturnSerializationErrorJson() throws Exception {

        Method method = APILogger.class
                .getDeclaredMethod("toSingleLineJson", Object.class);
        method.setAccessible(true);

        Object failingObject = new Object() {
            public Object getFail() {
                throw new RuntimeException("boom");
            }
        };

        String result = (String) method.invoke(null, failingObject);

        assertThat(result).contains("serializationError");
    }


    @Test
    void shouldCoverPrivateConstructor() throws Exception {

        Constructor<APILogger> constructor =
                APILogger.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        APILogger instance = constructor.newInstance();

        assertThat(instance).isNotNull();
    }
}
