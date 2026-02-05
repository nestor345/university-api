package com.university.administration.utils.logs;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.university.administration.domain.logs.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class APILogger {

    private static final ObjectMapper MAPPER = new ObjectMapper().disable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private APILogger() {
    }

    public static void writeInputLog(String service, Map<String, Object> additionalInfo) {
        writeLog(service, ActivityPhase.INPUT, null, ResultTransaction.SUCCESS, System.currentTimeMillis(),
                additionalInfo);
    }

    public static void writeCallLog(String service, CallResource callResource, ResultTransaction resultTransaction,
                                    long startTime, Map<String, Object> additionalInfo) {
        writeLog(service, ActivityPhase.CALL, callResource, resultTransaction, startTime, additionalInfo);
    }

    public static void writeOutputLog(String service, ResultTransaction resultTransaction, long startTime,
                                      Map<String, Object> additionalInfo) {
        writeLog(service, ActivityPhase.OUTPUT, null, resultTransaction, startTime, additionalInfo);
    }

    public static void writeLog(String service, ActivityPhase activityPhase, CallResource callResource,
                                ResultTransaction resultTransaction, long startTime, Map<String, Object> additionalInfo) {
        APILogModel logMessage = new APILogModel();
        logMessage.setLogCategory(LogCategory.BUSINESS);
        logMessage.setService(service);
        logMessage.setActivity(activityPhase);
        logMessage.setCallResource(callResource);
        logMessage.setResult(resultTransaction);
        logMessage.setDuration((System.currentTimeMillis() - startTime) / 1_000.0);
        logMessage.setAditionalInfo(additionalInfo);

        writeLog(logMessage);
    }

    public static void writeLog(APILogModel logModel) {
        log.info(toSingleLineJson(logModel));
    }

    private static String toSingleLineJson(Object pojo) {
        if (pojo == null) {
            return "{}";
        }
        try {
            return MAPPER.writeValueAsString(pojo).replaceAll("[\\r\\n]+", " ");
        } catch (Exception e) {
            return "{\"serializationError\":\"" + e.getClass().getSimpleName() + ": "
                    + e.getMessage().replaceAll("[\\r\\n]+", " ") + "\"}";
        }
    }

}

